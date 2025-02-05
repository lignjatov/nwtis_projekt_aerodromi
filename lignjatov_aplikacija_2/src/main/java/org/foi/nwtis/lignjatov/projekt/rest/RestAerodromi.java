package org.foi.nwtis.lignjatov.projekt.rest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.ligjatov.projekt.pomocni.KomunikacijaPosluzitelj;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestAerodromi.
 */
@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;


  /** DataSource za povezivanje s bazom podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Daj sve aerodrome.
   *
   * @param traziNaziv the trazi naziv
   * @param traziDrzavu the trazi drzavu
   * @param odBroja Broj od kojega kreće uzimati podatke
   * @param broj Količina preuzetih podataka
   * @return response Odgovor koji se vraća
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSveAerodrome(@QueryParam("traziNaziv") String traziNaziv,
      @QueryParam("traziDrzavu") String traziDrzavu, @QueryParam("odBroja") int odBroja,
      @QueryParam("broj") int broj) {
    var aerodromi = new ArrayList<Aerodrom>();
    PreparedStatement pstmt = null;
    if (odBroja == 0 || broj == 0) {
      odBroja = 0;
      broj = 20;
    }

    String query =
        "select ICAO, NAME, ISO_COUNTRY, COORDINATES from AIRPORTS WHERE NAME LIKE ? AND ISO_COUNTRY LIKE ? LIMIT ? OFFSET ?;";

    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, "%%");
      pstmt.setString(2, "%%");
      // String traziDrzavuProba = "%" + traziDrzavu + "%";
      // String traziNazivProba = "%" + traziNaziv + "%";

      if (traziNaziv != null) {
        pstmt.setString(1, traziNaziv + "%");
      }
      if (traziDrzavu != null) {
        pstmt.setString(2, traziDrzavu + "%");
      }

      pstmt.setInt(3, broj);
      pstmt.setInt(4, odBroja);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String icaoAerodroma = rs.getString("ICAO");
        String nazivAerodroma = rs.getString("NAME");
        String isoDrzave = rs.getString("ISO_COUNTRY");
        String lokacija[] = rs.getString("COORDINATES").split(",");
        Lokacija lokacij = new Lokacija(lokacija[0], lokacija[1]);
        Aerodrom ada = new Aerodrom(icaoAerodroma, nazivAerodroma, isoDrzave, lokacij);
        aerodromi.add(ada);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }

    var gson = new Gson();
    var jsonAerodromi = gson.toJson(aerodromi);

    var odgovor = Response.ok().entity(jsonAerodromi).build();

    return odgovor;
  }

  /**
   * Daj jedan aerodrom.
   *
   * @param icao Icao aerodroma
   * @return response Odgovor koji se prosljeđuje korisniku
   */
  @Path("{icao}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodrom(@PathParam("icao") String icao) {
    Aerodrom aerodrom = null;
    Response odgovor = null;
    PreparedStatement pstmt = null;

    String query = "select ICAO, NAME, ISO_COUNTRY, COORDINATES from AIRPORTS WHERE ICAO = ?";

    try (var con = ds.getConnection()) {

      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String[] koordinate = rs.getString("COORDINATES").split(",");
        Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1]);
        Aerodrom ad = new Aerodrom(rs.getString("ICAO"), rs.getString("NAME"),
            rs.getString("ISO_COUNTRY"), lokacija);
        aerodrom = ad;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    if (aerodrom == null) {
      odgovor = Response.status(404).build();
    } else {
      var gson = new Gson();
      var jsonAerodromi = gson.toJson(aerodrom);

      odgovor = Response.ok().entity(jsonAerodromi).build();
    }
    return odgovor;
  }

  /**
   * Daj udaljenosti izmedu dva aerodoma.
   *
   * @param icaoOd Od kojeg aerodroma
   * @param icaoDo Do kojeg aerodroma
   * @return response Odgovor korisniku sa udaljenosti između aerodroma
   */
  @Path("{icaoOd}/{icaoDo}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiIzmeduDvaAerodoma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    var udaljenosti = new ArrayList<UdaljenostAerodromDrzava>();
    PreparedStatement pstmt = null;

    String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from AIRPORTS_DISTANCE_MATRIX";
    query += " WHERE ICAO_FROM = ? AND ICAO_TO = ?";

    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icaoOd);
      pstmt.setString(2, icaoDo);

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String icao = rs.getString("ICAO_TO");
        String drzava = rs.getString("COUNTRY");
        float udaljDrzava = rs.getFloat("DIST_CTRY");
        var put = new UdaljenostAerodromDrzava(icao, drzava, udaljDrzava);
        udaljenosti.add(put);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(udaljenosti);

    var odgovor = Response.ok().entity(jsonUdaljenosti).build();

    return odgovor;
  }

  /**
   * Daj aerodrom udaljenosti.
   *
   * @param icao Podataka iz URI o aerodromu
   * @param odBroja Broj od kojeg kreće preuzimati podatke
   * @param broj Količina podataka preuzeta iz baze
   * @return response Odgovor korisniku
   */
  @Path("{icao}/udaljenosti")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodromUdaljenosti(@PathParam("icao") String icao,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    if (odBroja == 0 || broj == 0) {
      odBroja = 1;
      broj = 20;
    }

    var udaljenosti = new ArrayList<UdaljenostAerodrom>();
    PreparedStatement pstmt = null;

    String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from AIRPORTS_DISTANCE_MATRIX ";
    query += "WHERE ICAO_FROM = ? LIMIT ? OFFSET ?;";

    try (var con = ds.getConnection()) {

      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.setString(2, String.valueOf(broj));
      pstmt.setString(3, String.valueOf(odBroja));

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String icaoOdredisni = rs.getString("ICAO_TO");
        float udaljDrzava = rs.getFloat("DIST_CTRY");
        var put = new UdaljenostAerodrom(icaoOdredisni, udaljDrzava);
        udaljenosti.add(put);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(udaljenosti);

    var odgovor = Response.ok().entity(jsonUdaljenosti).build();

    return odgovor;
  }

  /**
   * Daj izracunate udaljenosti od do. Šalje aplikaciji_1 zahtjev za izračunati udaljenosti
   *
   * @param icaoOd icao od
   * @param icaoDo icao do
   * @return odgovor poslužitelja
   */
  @Path("{icaoOd}/izracunaj/{icaoDo}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajIzracunateUdaljenostiOdDo(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    Aerodrom aerodromOd = dohvatiAerodrom(icaoOd);
    Aerodrom aerodromDo = dohvatiAerodrom(icaoDo);
    if (aerodromOd == null || aerodromDo == null) {
      var odgovor = Response.noContent().build();
      return odgovor;
    }

    String zahtjev = KomunikacijaPosluzitelj.sloziZahtjevUdaljenost(aerodromOd.getLokacija(),
        aerodromDo.getLokacija());
    String udaljenost = KomunikacijaPosluzitelj.posaljiZahtjev(zahtjev);

    UdaljenostAerodrom udaljenosti =
        new UdaljenostAerodrom(aerodromDo.getIcao(), Float.parseFloat(udaljenost.split(" ")[1]));
    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(udaljenosti);
    var odgovor = Response.ok().entity(jsonUdaljenosti).build();
    return odgovor;
  }


  /**
   * Vraća sve aerodrome koji su iz iste države kao odredišni aerodrom i imaju manju kilometražu od
   * udaljenosti ishodišnog i odredišnog
   *
   * @param icaoOd the icao od
   * @param icaoDo the icao do
   * @return the response
   */
  @Path("{icaoOd}/udaljenost1/{icaoDo}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajIzracunateUdaljenosti1(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {
    Aerodrom ishodisni = dohvatiAerodrom(icaoOd);
    Aerodrom odredisni = dohvatiAerodrom(icaoDo);
    List<UdaljenostAerodromDrzava> listaUdaljenihAerodroma =
        new ArrayList<UdaljenostAerodromDrzava>();

    String zahtjev = KomunikacijaPosluzitelj.sloziZahtjevUdaljenost(ishodisni.getLokacija(),
        odredisni.getLokacija());

    String udaljenostMjeraString = KomunikacijaPosluzitelj.posaljiZahtjev(zahtjev);
    Float udaljenostMjera = Float.parseFloat(udaljenostMjeraString.split(" ")[1]);

    PreparedStatement pstmt = null;
    String query = "SELECT * FROM AIRPORTS WHERE ISO_COUNTRY=?";
    try (var con = ds.getConnection()) {

      pstmt = con.prepareStatement(query);
      pstmt.setString(1, odredisni.getDrzava());

      ResultSet rs = pstmt.executeQuery();
      Aerodrom aerodrom = new Aerodrom();
      while (rs.next()) {
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("NAME"));
        aerodrom.setDrzava(rs.getString("ISO_COUNTRY"));
        String[] nesto = rs.getString("COORDINATES").split(",");
        Lokacija koordinate = new Lokacija(nesto[0].trim(), nesto[1].trim());
        aerodrom.setLokacija(koordinate);

        zahtjev = KomunikacijaPosluzitelj.sloziZahtjevUdaljenost(ishodisni.getLokacija(),
            aerodrom.getLokacija());
        String udaljenostString = KomunikacijaPosluzitelj.posaljiZahtjev(zahtjev);
        Float udaljenost = Float.parseFloat(udaljenostString.split(" ")[1]);
        if (udaljenost < udaljenostMjera) {
          listaUdaljenihAerodroma.add(
              new UdaljenostAerodromDrzava(aerodrom.getIcao(), aerodrom.getDrzava(), udaljenost));
        }

      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }

    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(listaUdaljenihAerodroma);
    var odgovor = Response.ok().entity(jsonUdaljenosti).build();
    return odgovor;
  }

  /**
   * Vraća sve aerodromeiz navedene države koji imaju manju udaljenost od navedene u km
   *
   * @param icaoOd icao od
   * @param drzava drzava
   * @param km km
   * @return Response izračunate udaljenosti
   */
  @Path("{icaoOd}/udaljenost2")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajIzracunateUdaljenosti2(@PathParam("icaoOd") String icaoOd,
      @QueryParam("drzava") String drzava, @QueryParam("km") Float km) {
    Aerodrom ishodisni = dohvatiAerodrom(icaoOd);
    List<UdaljenostAerodromDrzava> listaUdaljenihAerodroma =
        new ArrayList<UdaljenostAerodromDrzava>();

    PreparedStatement pstmt = null;
    String query = "SELECT * FROM AIRPORTS WHERE ISO_COUNTRY=?";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, drzava);
      ResultSet rs = pstmt.executeQuery();
      Aerodrom aerodrom = new Aerodrom();
      while (rs.next()) {
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("NAME"));
        aerodrom.setDrzava(rs.getString("ISO_COUNTRY"));
        String[] nesto = rs.getString("COORDINATES").split(",");
        Lokacija koordinate = new Lokacija(nesto[0].trim(), nesto[1].trim());
        aerodrom.setLokacija(koordinate);

        String zahtjev = KomunikacijaPosluzitelj.sloziZahtjevUdaljenost(ishodisni.getLokacija(),
            aerodrom.getLokacija());
        String udaljenostString = KomunikacijaPosluzitelj.posaljiZahtjev(zahtjev);
        Float udaljenost = Float.parseFloat(udaljenostString.split(" ")[1]);
        if (udaljenost < km) {
          listaUdaljenihAerodroma.add(
              new UdaljenostAerodromDrzava(aerodrom.getIcao(), aerodrom.getDrzava(), udaljenost));
        }
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }


    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(listaUdaljenihAerodroma);
    var odgovor = Response.ok().entity(jsonUdaljenosti).build();
    return odgovor;
  }

  /**
   * Dohvati aerodrom.
   *
   * @param icao icao
   * @return aerodrom
   */
  public Aerodrom dohvatiAerodrom(String icao) {
    String query = "SELECT * FROM AIRPORTS WHERE ICAO=?";
    Aerodrom aerodrom = null;
    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);

      ResultSet rs = pstmt.executeQuery();
      aerodrom = new Aerodrom();
      while (rs.next()) {
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("NAME"));
        aerodrom.setDrzava(rs.getString("ISO_COUNTRY"));
        String[] nesto = rs.getString("COORDINATES").split(",");
        Lokacija koordinate = new Lokacija(nesto[0].trim(), nesto[1].trim());
        aerodrom.setLokacija(koordinate);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      aerodrom = null;
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        aerodrom = null;

      }
    }
    return aerodrom;
  }
}
