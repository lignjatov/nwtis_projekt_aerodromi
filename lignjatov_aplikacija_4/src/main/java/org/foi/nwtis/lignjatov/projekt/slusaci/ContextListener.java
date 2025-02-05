package org.foi.nwtis.lignjatov.projekt.slusaci;

import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.ligjatov.projekt.pomocni.KomunikacijaPosluzitelj;
import org.foi.nwtis.lignjatov.projekt.ws.WsInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * The listener interface for receiving context events. The class that is interested in processing a
 * context event implements this interface, and the object created with that class is registered
 * with a component using the component's <code>addContextListener<code> method. When the context
 * event occurs, that object's appropriate method is invoked.
 *
 * @see ContextEvent
 */
@WebListener
public class ContextListener implements ServletContextListener {

  /** The ds. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Inicijalizacija slušaća konteksta servleta.
   *
   * @param sce Varijabla događaja
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    System.out.println("KONTEKST INICIJALIZIRAN");
    var context = sce.getServletContext();
    String konfiguracijskaDatoteka = context.getInitParameter("konfiguracija");
    String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
    PostavkeBazaPodataka postavke = new PostavkeBazaPodataka(putanja + konfiguracijskaDatoteka);
    try {
      postavke.ucitajKonfiguraciju();

    } catch (NeispravnaKonfiguracija e) {
      System.out.println(e.getMessage());
    }

    context.setAttribute("konfig", postavke);

    String adresa = postavke.dajPostavku("adresaPosluzitelja");
    String port = postavke.dajPostavku("mreznaVrataPosluzitelja");

    System.out.println(adresa + "|" + port);
    if (!provjeriPort(adresa, port)) {
      throw new RuntimeException("Server je ugašen");
    }
    KomunikacijaPosluzitelj.adresa = adresa;
    KomunikacijaPosluzitelj.mreznaVrata = Integer.parseInt(port);
    String poruka = KomunikacijaPosluzitelj.posaljiZahtjev("STATUS");
    System.out.println(poruka);
    if (poruka.compareTo("OK 0") == 0) {
      System.out.println("Server je ugašen");
      throw new RuntimeException("Server je ugašen");
    }

    WsInfo.brojAktivnihAerodroma = dohvatiBrojAerodroma();
    WsInfo.brojKorisnika = dohvatiBrojKorisnika();
  }

  /**
   * Uništen kontekst.
   *
   * @param sce Varijabla događaja
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    ServletContextListener.super.contextDestroyed(sce);
  }

  /**
   * Provjeri port.
   *
   * @param adresa adresa poslužitelja na aplikacija 1
   * @param port mrežna vrata aplikacije
   * @return true ako je port dostupan
   */
  public boolean provjeriPort(String adresa, String port) {
    Socket posluzitelj;
    try {
      posluzitelj = new Socket(adresa, Integer.parseInt(port));
      posluzitelj.close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Dohvati broj korisnika.
   *
   * @return broj korisnika pri pokretanju apliakcije
   */
  public int dohvatiBrojKorisnika() {
    PreparedStatement pstmt = null;
    int brojKorisnika = 0;

    String query = "SELECT COUNT(*) FROM KORISNICI;";
    try (var con = ds.getConnection()) {

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
      }
    }
    return brojKorisnika;
  }


  /**
   * Dohvati broj aktivnih aerodroma.
   *
   * @return the int
   */
  public int dohvatiBrojAerodroma() {
    PreparedStatement pstmt = null;
    int brojAerodroma = 0;
    String query = "SELECT COUNT(*) FROM AERODROMI_LETOVI WHERE AKTIVAN=1;";
    try (var con = ds.getConnection()) {

      pstmt = con.prepareStatement(query);
      var rs = pstmt.executeQuery();
      while (rs.next()) {
        brojAerodroma = rs.getInt(1);
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
      }
    }
    return brojAerodroma;
  }

}
