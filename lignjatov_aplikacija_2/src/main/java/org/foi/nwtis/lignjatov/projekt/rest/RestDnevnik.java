package org.foi.nwtis.lignjatov.projekt.rest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.foi.nwtis.podaci.Dnevnik;
import org.foi.nwtis.podaci.PosluziteljOdgovor;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The Class RestDnevnik.
 */
@Path("dnevnik")
@RequestScoped
public class RestDnevnik {

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;


  /** DataSource za povezivanje s bazom podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;


  /**
   * Daj sve zapise dnevnika.
   * 
   * @param vrsta Definira vrstu zapisa
   * @param odBroja Broj od kojega kreće uzimati podatke
   * @param broj Količina preuzetih podataka
   * @return response Odgovor koji se vraća
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajDnevnikZapise(@QueryParam("vrsta") String vrsta,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    var zapisi = new ArrayList<Dnevnik>();
    PreparedStatement pstmt = null;
    if (odBroja == 0 || broj == 0) {
      odBroja = 1;
      broj = 20;
    }
    String query = sloziUpitParametara(vrsta, odBroja, broj);
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, String.valueOf(broj));
      pstmt.setString(2, String.valueOf(odBroja));

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Dnevnik zapisDnevnika = new Dnevnik(rs.getString("ZAHTJEV"),
            rs.getTimestamp("VRIJEMEZAHTJEVA"), rs.getString("VRSTA"));
        zapisi.add(zapisDnevnika);
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
    var jsonDnevnik = gson.toJson(zapisi);

    var odgovor = Response.ok().entity(jsonDnevnik).build();

    return odgovor;
  }



  /**
   * Napravi zapis dnevnik.
   *
   * @param zahtjev Zahtjev poslan
   * @param vrsta Vrsta zahtjeva
   * @return Odgovor
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response napraviZapisDnevnik(@FormParam("zahtjev") String zahtjev,
      @FormParam("vrsta") String vrsta) {
    PreparedStatement pstmt = null;
    PosluziteljOdgovor poruka;

    if (!provjeriVrstu(vrsta)) {
      poruka = new PosluziteljOdgovor("400", "Greška pri zapisu");
      var gson = new Gson();
      var jsonDnevnik = gson.toJson(poruka);
      var odgovor = Response.ok().entity(jsonDnevnik).build();
      return odgovor;
    }

    String query = "INSERT INTO DNEVNIK(ZAHTJEV, VRIJEMEZAHTJEVA, VRSTA) VALUES(?,?,?)";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, zahtjev);
      pstmt.setTimestamp(2, vratiTrenutnoVrijeme());
      pstmt.setString(3, vrsta);
      pstmt.executeUpdate();

      pstmt.close();
      con.close();

      poruka = new PosluziteljOdgovor("200", "Podatak zapisan u dnevnik");

    } catch (Exception e) {
      System.out.println(e.getMessage());

      poruka = new PosluziteljOdgovor("400", "Greška pri zapisu");

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
    var jsonDnevnik = gson.toJson(poruka);
    var odgovor = Response.ok().entity(jsonDnevnik).build();
    return odgovor;
  }

  /**
   * Provjeri vrstu.
   *
   * @param vrsta the vrsta
   * @return true, if successful
   */
  private boolean provjeriVrstu(String vrsta) {
    boolean vrijedi = false;
    switch (vrsta) {
      case "AP2":
        vrijedi = true;
        break;
      case "AP4":
        vrijedi = true;
        break;
      case "AP5":
        vrijedi = true;
        break;
    }
    return vrijedi;
  }

  /**
   * Slaže upit ovisno o paramterima upisanima. Ako nije unesena vrsta, vraća sve
   *
   * @param vrsta Vrsta aplikacije
   * @param odBroja od kojeg broja da započinje
   * @param broj količina
   * @return upit za bazu podataka ovisno o paramterima
   */
  private String sloziUpitParametara(String vrsta, int odBroja, int broj) {

    String query = "select * from DNEVNIK ";

    if (vrsta == null) {
      query += " ORDER BY VRIJEMEZAHTJEVA DESC LIMIT ? OFFSET ? ";
      return query;
    }

    switch (vrsta) {
      case "AP2":
        query += " WHERE vrsta='AP2'";
        break;
      case "AP4":
        query += " WHERE vrsta='AP4'";
        break;
      case "AP5":
        query += " WHERE vrsta='AP5'";
        break;
    }
    query += " ORDER BY VRIJEMEZAHTJEVA DESC LIMIT ? OFFSET ? ";
    return query;
  }

  /**
   * Vrati trenutno vrijeme.
   *
   * @return the timestamp
   */
  private Timestamp vratiTrenutnoVrijeme() {
    return new Timestamp(System.currentTimeMillis());
  }
}
