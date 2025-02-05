package org.foi.nwtis.lignjatov.projekt.filteri;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;



/**
 * Klasa AP2.
 */
public class AP2 implements Filter {

  /** Baza podataka */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /** Konfiguracija filter */
  private FilterConfig config = null;

  /**
   * Inits the.
   *
   * @param filterConfig the filter config
   * @throws ServletException the servlet exception
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = filterConfig;
    System.out.println("Kreiran fitler");
  }

  /**
   * Destroy.
   */
  @Override
  public void destroy() {
    config = null;
  }

  /**
   * Do filter.
   *
   * @param request Zahtjev
   * @param response Odgovor
   * @param chain chain
   * @throws IOException I/O iznimka okinuta
   * @throws ServletException servlet pogreška
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    PreparedStatement pstmt;
    String query = "INSERT INTO DNEVNIK(ZAHTJEV,VRIJEMEZAHTJEVA, VRSTA) VALUES (?,?,?)";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, dohvatiURI(request));
      pstmt.setTimestamp(2, vratiTrenutnoVrijeme());
      pstmt.setString(3, "AP2");
      pstmt.executeUpdate();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());

    }

    chain.doFilter(request, response);
  }

  /**
   * Vrati trenutno vrijeme.
   *
   * @return timestamp trenutno vrijeme
   */
  private Timestamp vratiTrenutnoVrijeme() {
    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * Dohvati URI. Slaže URI skupa sa parametrima
   *
   * @param request zahtjev poslan prema servisu
   * @return string vraća složeni URI
   */
  private String dohvatiURI(ServletRequest request) {
    String nameServlet = "";
    String argumenti = "";
    nameServlet = ((HttpServletRequest) request).getRequestURI();
    argumenti = ((HttpServletRequest) request).getQueryString();
    if (argumenti != null) {
      return nameServlet + "?" + argumenti;
    }
    return nameServlet;
  }
}
