package org.foi.nwtis.lignjatov.projekt.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.lignjatov.projekt.iznimke.PogresnaAutentikacija;
import org.foi.nwtis.podaci.Korisnik;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Klasa WsKorisnici.
 */
@WebService(serviceName = "korisnici")
public class WsKorisnici {

  /** Izvor podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Daj korisnike. Vraća korisnike ovisno o parametrima tražiIme i traziPrezime
   *
   * @param korisnik korisničko ime
   * @param lozinka lozinka
   * @param traziImeKorisnik ime korisnika
   * @param traziPrezimeKorisnik prezime korisnika
   * @param odBroja od broja
   * @param broj broja
   * @return lista korisnika
   */
  @WebMethod
  public List<Korisnik> dajKorisnike(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String traziImeKorisnik, @WebParam String traziPrezimeKorisnik,
      @WebParam int odBroja, @WebParam int broj) {
    var korisnici = new ArrayList<Korisnik>();
    PreparedStatement pstmt = null;

    if (odBroja == 0 || broj == 0) {
      odBroja = 0;
      broj = 20;
    }

    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e1) {
      System.out.println(e1.getMessage());
      return korisnici;
    }

    String query = "select * from KORISNICI WHERE IME LIKE ? AND PREZIME LIKE ? LIMIT ? OFFSET ?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, "%%");
      pstmt.setString(2, "%%");
      if (traziImeKorisnik != null) {
        pstmt.setString(1, traziImeKorisnik + "%");
      }
      if (traziPrezimeKorisnik != null) {
        pstmt.setString(2, traziPrezimeKorisnik + "%");
      }

      pstmt.setString(3, String.valueOf(broj));
      pstmt.setString(4, String.valueOf(odBroja));
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String korisnickoIme = rs.getString("KORISNICKO_IME");
        String lozinkaKorisnika = rs.getString("LOZINKA");
        String ime = rs.getString("IME");
        String prezime = rs.getString("PREZIME");
        String email = rs.getString("EMAIL");
        Korisnik korisnikTemp = new Korisnik(korisnickoIme, lozinkaKorisnika, ime, prezime, email);
        korisnici.add(korisnikTemp);
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
    return korisnici;
  }

  /**
   * Daj jednog korisnika prema korisničkom imenu
   *
   * @param korisnik korisničko ime
   * @param lozinka lozinka
   * @param traziKorisnik traženo korisničko ime
   * @return Korisnik
   */
  @WebMethod
  public Korisnik dajKorisnik(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String traziKorisnik) {
    Korisnik odabraniKorisnik = null;
    PreparedStatement pstmt = null;

    try {
      autenticirajKorisnika(korisnik, lozinka);
    } catch (PogresnaAutentikacija e) {
      System.out.println(e.getMessage());
      return odabraniKorisnik;
    }

    String query = "select * from KORISNICI WHERE KORISNICKO_IME=?;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, traziKorisnik);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String korisnickoIme = rs.getString("KORISNICKO_IME");
        String lozinkaKorisnika = rs.getString("LOZINKA");
        String ime = rs.getString("IME");
        String prezime = rs.getString("PREZIME");
        String email = rs.getString("EMAIL");

        Korisnik korisnikTemp = new Korisnik(korisnickoIme, lozinkaKorisnika, ime, prezime, email);
        odabraniKorisnik = korisnikTemp;
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        return null;
      }
    }
    return odabraniKorisnik;
  }

  /**
   * Dodaj korisnika
   *
   * @param korisnik Korisnik
   * @return true ako se uspješno upiše korisnik, inače false
   */
  @WebMethod
  public boolean dodajKorisnik(@WebParam Korisnik korisnik) {
    PreparedStatement pstmt = null;
    int brojKorisnika = WsInfo.brojKorisnika;

    String query =
        "INSERT INTO KORISNICI(KORISNICKO_IME, LOZINKA, EMAIL, IME, PREZIME) VALUES (?,?,?,?,?)";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, korisnik.getKorisnickoIme());
      pstmt.setString(2, korisnik.getLozinka());
      pstmt.setString(3, korisnik.getEmail());
      pstmt.setString(4, korisnik.getIme());
      pstmt.setString(5, korisnik.getPrezime());
      pstmt.executeUpdate();
      pstmt.close();

      query = "SELECT COUNT(*) FROM KORISNICI;";
      pstmt = con.prepareStatement(query);
      var rs = pstmt.executeQuery();
      while (rs.next()) {
        brojKorisnika = rs.getInt(1);
      }
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
        return false;
      }
    }
    WsInfo.brojKorisnika = brojKorisnika;
    WsKlijent websocketKlijent = new WsKlijent();
    websocketKlijent.sendMessage("posalji:brojKorisnika:" + brojKorisnika);
    return true;
  }


  /**
   * Autenticiraj korisnika.
   *
   * @param korisnickoIme korisnicko ime
   * @param lozinka lozinka
   * @return true ako je korisnik uspješno autenticiran
   * @throws PogresnaAutentikacija iznimka u slučaju da korisnički podaci nisu dobri
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
