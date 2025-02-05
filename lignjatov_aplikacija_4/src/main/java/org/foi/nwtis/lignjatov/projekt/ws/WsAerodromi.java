package org.foi.nwtis.lignjatov.projekt.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.lignjatov.projekt.iznimke.PogresnaAutentikacija;
import org.foi.nwtis.podaci.AerodromiLetovi;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Klasa WsAerodromi.
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  /** Izvor podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Daj sve aerodrome.
   *
   * @param korisnik korisnikčko ime
   * @param lozinka lozinka
   * @param odBroja OdBroja
   * @param broj Broj aerodroma
   * @return list Lista aerodroma
   */
  @WebMethod
  public List<AerodromiLetovi> dajAerodromeZaLetove(
      @WebParam(name = "korisnickoIme") String korisnik, @WebParam(name = "lozinka") String lozinka,
      @WebParam(name = "odBroja") int odBroja, @WebParam(name = "broj") int broj) {
    var aerodromi = new ArrayList<AerodromiLetovi>();
    PreparedStatement pstmt = null;
    if (odBroja == 0 || broj == 0) {
      odBroja = 1;
      broj = 20;
    }

    String query = "select * from AERODROMI_LETOVI LIMIT ? OFFSET ? ;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, String.valueOf(broj));
      pstmt.setString(2, String.valueOf(odBroja));

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String naziv = rs.getString("NAZIV");
        int aktivan = rs.getInt("AKTIVAN");
        AerodromiLetovi ada = new AerodromiLetovi(naziv, (aktivan == 0) ? false : true);
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
    return aerodromi;
  }

  /**
   * Dodaj aerodrom za prikupljanje letova
   *
   * @param korisnik korisnicko ime
   * @param lozinka lozinka
   * @param icao icao
   * @return vrati true ako je uspješno autenticiran korisnik, inače false
   */
  @WebMethod
  public boolean dodajAerodromZaLetove(@WebParam(name = "korisnickoIme") String korisnik,
      @WebParam(name = "lozinka") String lozinka, @WebParam(name = "icao") String icao) {
    PreparedStatement pstmt = null;
    int brojAerodroma = 0;

    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e1) {
      System.out.println(e1.getMessage());
      return false;
    }

    String query = "INSERT INTO AERODROMI_LETOVI(NAZIV,AKTIVAN) VALUES(?,?)";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.setInt(2, 1);

      pstmt.executeUpdate();
      pstmt.close();

      query = "SELECT COUNT(*) FROM AERODROMI_LETOVI WHERE AKTIVAN=1";
      pstmt = con.prepareStatement(query);
      var rs = pstmt.executeQuery();
      while (rs.next()) {
        brojAerodroma = rs.getInt(1);
      }
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
    WsInfo.brojAktivnihAerodroma = brojAerodroma;
    WsKlijent websocketKlijent = new WsKlijent();
    websocketKlijent.sendMessage("posalji:brojAerodroma:" + brojAerodroma);

    return true;
  }

  /**
   * Pauziraj aerodrome za skupljanje letova
   *
   * @param korisnik korisničko ime
   * @param lozinka lozinka
   * @param icao aerodrom icao
   * @return true ako se korisnik uspješno prijavio, inače false
   */
  @WebMethod
  public boolean pauzirajAerodromeZaLetove(@WebParam String korisnik, String lozinka,
      @WebParam String icao) {
    PreparedStatement pstmt = null;
    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e1) {
      System.out.println(e1.getMessage());
      return false;
    }

    String query = "UPDATE AERODROMI_LETOVI SET AKTIVAN=0 WHERE NAZIV=?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.executeUpdate();
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
    return true;
  }

  /**
   * Aktiviraj aerodrome za preuzimanje letova
   *
   * @param korisnik korisničko ime
   * @param lozinka lozinka
   * @param icao aerodrom icao
   * @return true ako se je korisnik pravilno autenticirao, inače false
   */
  @WebMethod
  public boolean aktivirajAerodromeZaLetove(@WebParam String korisnik, String lozinka,
      @WebParam String icao) {
    PreparedStatement pstmt = null;
    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e1) {
      System.out.println(e1.getMessage());
      return false;
    }

    String query = "UPDATE AERODROMI_LETOVI SET AKTIVAN=1 WHERE NAZIV=?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);
      pstmt.executeUpdate();
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
    return true;
  }

  /**
   * Autenticiraj korisnika
   *
   * @param korisnickoIme korisnicko ime
   * @param lozinka lozinka
   * @return ako se uspješno prijavio, vraća true, inače false
   * @throws PogresnaAutentikacija iznimka u slučaju krivih informacija
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


