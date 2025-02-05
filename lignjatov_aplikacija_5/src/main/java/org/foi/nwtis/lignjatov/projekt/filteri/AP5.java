package org.foi.nwtis.lignjatov.projekt.filteri;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;



/**
 * Klasa AP5.
 */
public class AP5 implements Filter {

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** konfiguracija filtera */
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
   * Unos u bazu
   *
   * @param request zahtjev
   * @param response odgovor
   * @param chain lanac
   * @throws IOException U slučaju I/O iznimke
   * @throws ServletException U slučaju Servlet iznimke
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // var postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
    // RestKKlijent rk = new RestKKlijent(postavke);
    RestKKlijent rk = new RestKKlijent();
    rk.posaljiZapis(dohvatiURI(request));
    chain.doFilter(request, response);
  }

  /**
   * Dohvati URI.
   *
   * @param request Zahtjev
   * @return string puni zahtjev
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

  /**
   * Klasa RestKKlijent.
   */
  static class RestKKlijent {

    /** Ciljna stranica. */
    private final WebTarget webTarget;

    /** Klijent. */
    private final Client client;

    /** Korijenski URI. */
    private static String BASE_URI;

    /**
     * Instanciranje klase.
     */
    public RestKKlijent() {
      // BASE_URI = postavke.dajPostavku("adresaAplikacije2");
      BASE_URI = "http://200.20.0.4:8080/lignjatov_aplikacija_2/api";
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("dnevnik");
    }

    /**
     * Posalji zapis.
     *
     * @param zahtjev odnosi se na puni URI
     */
    private void posaljiZapis(String zahtjev) {
      WebTarget resource = webTarget;
      Form form = new Form();
      form.param("zahtjev", zahtjev);
      form.param("vrsta", "AP5");

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    }
  }
}
