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
 * Klasa AP4.
 */
public class AP4 implements Filter {

  /** PristupBazi */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /** Konfiguracija filtera */
  private FilterConfig config = null;

  /**
   * Inicijalizacija filtera
   *
   * @param filterConfig konfiguracija filtera
   * @throws ServletException Iznimka serlveta
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = filterConfig;
    System.out.println("Kreiran fitler");
  }

  /**
   * Uništi
   */
  @Override
  public void destroy() {
    config = null;
  }

  /**
   * Filtriraj tj. pošalji zahtjev upisan na bazu podataka
   *
   * @param request zahtjev
   * @param response odgovor
   * @param chain lanac
   * @throws IOException Iznimka pri I/O operacijama
   * @throws ServletException iznimka servleta
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
      pstmt.setString(3, "AP4");
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
   * Dohvati URI i njegove parametre
   *
   * @param request zahtjev
   * @return string zahtjev cijeli
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
