package org.foi.nwtis.lignjatov.projekt.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.lignjatov.projekt.iznimke.PogresnaAutentikacija;
import org.foi.nwtis.podaci.LetAviona;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijentBP;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;

/**
 * The Class WsLetovi.
 */
@WebService(serviceName = "letovi")
public class WsLetovi {

  /** Kontekst web servisa. */
  @Resource
  private WebServiceContext wsc;

  /** Izvor podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;


  /**
   * Daj polaske unutar intervala
   *
   * @param korisnik korisičko ime
   * @param lozinka lozinka korisnika
   * @param odBroja odBroja
   * @param broj broj preuzetih podataka
   * @param icao aerodrom icao
   * @param danOd od kojega dana
   * @param danDo do kojega dana
   * @return lista letova
   */
  @WebMethod
  public List<LetAvionaID> dajPolaskeInterval(@WebParam(name = "korisnik") String korisnik,
      @WebParam(name = "lozinka") String lozinka, @WebParam int odBroja, @WebParam int broj,
      @WebParam(name = "icao") String icao, @WebParam(name = "danOd") String danOd,
      @WebParam(name = "danDo") String danDo) {
    var aerodromi = new ArrayList<LetAviona>();
    PreparedStatement pstmt = null;
    if (odBroja == 0 || broj == 0) {
      odBroja = 1;
      broj = 20;
    }
    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e2) {
      System.out.println(e2.getMessage());
      return null;
    }

    var letovi = new ArrayList<LetAvionaID>();
    // pretvaranje u datum
    var formatter = new SimpleDateFormat("dd.MM.yyyy");
    Date preuzimanjeOd = null;
    Date preuzimanjeDo = null;
    try {
      preuzimanjeOd = formatter.parse(danOd);
      preuzimanjeDo = formatter.parse(danDo);
    } catch (ParseException e1) {
      System.out.println(e1.getMessage());
    }

    Timestamp od = new Timestamp(preuzimanjeOd.getTime());
    Timestamp vrijemeDo = new Timestamp(preuzimanjeDo.getTime());

    String query =
        "SELECT * FROM LETOVI_POLASCI lp WHERE ESTDEPARTUREAIRPORT=? AND STORED BETWEEN ? AND ? LIMIT ? OFFSET ?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.setTimestamp(2, od);
      pstmt.setTimestamp(3, vrijemeDo);
      pstmt.setInt(4, broj);
      pstmt.setInt(5, odBroja);

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("ID");
        String icao24 = rs.getString("ICAO24");
        int firstSeen = rs.getInt("FIRSTSEEN");
        String estDepartureAirport = rs.getString("ESTDEPARTUREAIRPORT");
        int lastSeen = rs.getInt("LASTSEEN");
        String estArrivalAirport = rs.getString("ESTARRIVALAIRPORT");
        String callSign = rs.getString("CALLSIGN");
        int estDepatureAirportHorizDistance = rs.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE");
        int estDepatureAirportVertDistance = rs.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE");

        int estArrivalAirportHorizDistance = rs.getInt("ESTARRIVALAIRPORTHORIZDISTANCE");
        int estArrivalAirportVertDistance = rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE");

        int depatureAirportCandidatesCount = rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE");
        int arrivalAirportCandidatesCount = rs.getInt("ARRIVALAIRPORTCANDIDATESCOUNT");

        LetAvionaID let =
            new LetAvionaID(id, icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport,
                callSign, estDepatureAirportHorizDistance, estDepatureAirportVertDistance,
                estArrivalAirportHorizDistance, estArrivalAirportVertDistance,
                depatureAirportCandidatesCount, arrivalAirportCandidatesCount);
        letovi.add(let);
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
    return letovi;
  }

  /**
   * Daj polaske na dan određeni datum iz baze podataka.
   *
   * @param korisnik korisičko ime
   * @param lozinka lozinka korisnika
   * @param odBroja od broja
   * @param broj broj preuzetih podataka
   * @param icao aerodrom icao
   * @param dan za koji dan
   * @return lista letova
   */
  @WebMethod
  public List<LetAvionaID> dajPolaskeNaDan(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam int odBroja, @WebParam int broj, @WebParam String icao, @WebParam String dan) {
    PreparedStatement pstmt = null;
    if (odBroja == 0 || broj == 0) {
      odBroja = 1;
      broj = 20;
    }
    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e2) {
      System.out.println(e2.getMessage());
      return null;
    }

    var letovi = new ArrayList<LetAvionaID>();
    // pretvaranje u datum
    var formatter = new SimpleDateFormat("dd.MM.yyyy");
    Date preuzimanjeOd = null;
    Timestamp sljedeciDan = null;
    try {
      preuzimanjeOd = formatter.parse(dan);
    } catch (ParseException e1) {
      System.out.println(e1.getMessage());
    }

    Timestamp od = new Timestamp(preuzimanjeOd.getTime());
    LocalDateTime ldt = od.toLocalDateTime();
    ldt = ldt.plusDays(1);
    sljedeciDan = Timestamp.valueOf(ldt);


    String query =
        "SELECT * FROM LETOVI_POLASCI lp WHERE ESTDEPARTUREAIRPORT=? AND STORED BETWEEN ? AND ? LIMIT ? OFFSET ?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.setTimestamp(2, od);
      pstmt.setTimestamp(3, sljedeciDan);
      pstmt.setInt(4, broj);
      pstmt.setInt(5, odBroja);

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("ID");
        String icao24 = rs.getString("ICAO24");
        int firstSeen = rs.getInt("FIRSTSEEN");
        String estDepartureAirport = rs.getString("ESTDEPARTUREAIRPORT");
        int lastSeen = rs.getInt("LASTSEEN");
        String estArrivalAirport = rs.getString("ESTARRIVALAIRPORT");
        String callSign = rs.getString("CALLSIGN");
        int estDepatureAirportHorizDistance = rs.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE");
        int estDepatureAirportVertDistance = rs.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE");

        int estArrivalAirportHorizDistance = rs.getInt("ESTARRIVALAIRPORTHORIZDISTANCE");
        int estArrivalAirportVertDistance = rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE");

        int depatureAirportCandidatesCount = rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE");
        int arrivalAirportCandidatesCount = rs.getInt("ARRIVALAIRPORTCANDIDATESCOUNT");

        LetAvionaID let =
            new LetAvionaID(id, icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport,
                callSign, estDepatureAirportHorizDistance, estDepatureAirportVertDistance,
                estArrivalAirportHorizDistance, estArrivalAirportVertDistance,
                depatureAirportCandidatesCount, arrivalAirportCandidatesCount);
        letovi.add(let);
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
    return letovi;
  }

  /**
   * Daj polaske na dan sa OpenSkyNetwork.
   *
   * @param korisnik the korisnik
   * @param lozinka the lozinka
   * @param icao the icao
   * @param dan the dan
   * @return the list
   */
  @WebMethod
  public List<org.foi.nwtis.rest.podaci.LetAviona> dajPolaskeNaDanOS(@WebParam String korisnik,
      @WebParam String lozinka, @WebParam String icao, @WebParam String dan) {

    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e2) {
      System.out.println(e2.getMessage());
      return null;
    }
    List<org.foi.nwtis.rest.podaci.LetAviona> letovi = null;
    var formatter = new SimpleDateFormat("dd.MM.yyyy");
    Date preuzimanjeOd = null;
    Timestamp sljedeciDan = null;
    try {
      preuzimanjeOd = formatter.parse(dan);

    } catch (ParseException e1) {
      System.out.println(e1.getMessage());
    }

    Timestamp od = new Timestamp(preuzimanjeOd.getTime());
    LocalDateTime ldt = od.toLocalDateTime();
    ldt = ldt.plusDays(1);
    sljedeciDan = Timestamp.valueOf(ldt);
    ServletContext sc = (ServletContext) wsc.getMessageContext()
        .get(jakarta.xml.ws.handler.MessageContext.SERVLET_CONTEXT);
    PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
    OSKlijentBP klijent = new OSKlijentBP(postavke.dajPostavku("OpenSkyNetworkBP.korisnik"),
        postavke.dajPostavku("OpenSkyNetworkBP.lozinka"));

    // OSKlijent klijent = new OSKlijent(postavke.dajPostavku("OpenSkyNetwork.korisnik"),
    // postavke.dajPostavku("OpenSkyNetwork.lozinka"));

    try {
      letovi = klijent.getDepartures(icao, od, sljedeciDan);
    } catch (NwtisRestIznimka e) {
      System.out.println(e.getMessage());
      return null;
    }
    return letovi;
  }



  /**
   * Autenticiraj korisnika.
   *
   * @param korisnickoIme korisničko ime
   * @param lozinka lozinka
   * @return true ako je uspješno autenticiran korisnik. inače false
   * @throws PogresnaAutentikacija iznimka u slučaju da korisnički podaci nisu točni
   */
  private boolean autenticirajKorisnika(String korisnickoIme, String lozinka)
      throws PogresnaAutentikacija {
    PreparedStatement pstmt = null;
    boolean postoji = false;
    String query =
        "select KORISNICKO_IME, LOZINKA from KORISNICI WHERE KORISNICKO_IME=? AND LOZINKA=?";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);

      pstmt.setString(1, korisnickoIme);
      pstmt.setString(2, lozinka);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        postoji = true;
      } else {
        postoji = false;
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
    if (!postoji) {
      throw new PogresnaAutentikacija("Korisnik neuspješno autenticiran!");
    }
    return postoji;
  }

}
