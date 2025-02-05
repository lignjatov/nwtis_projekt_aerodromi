package org.foi.nwtis.lignjatov.projekt.ws;

import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.ws.WebServiceContext;

/**
 * Klasa WsMeteo
 */
@WebService(serviceName = "meteo")
public class WsMeteo {

  /** Kontekst web servisa */
  @Resource
  private WebServiceContext wsc;


  /** Izvor podataka */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;


  /**
   * Daj meteo.
   *
   * @param icao Icao
   * @return meteo Vraća meteorološke podatke
   */
  @WebMethod
  public MeteoPodaci dajMeteo(@WebParam String icao) {
    String lon = "";
    String lat = "";
    ServletContext sc = (ServletContext) wsc.getMessageContext()
        .get(jakarta.xml.ws.handler.MessageContext.SERVLET_CONTEXT);
    PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
    OWMKlijent owmKlijent = new OWMKlijent(postavke.dajPostavku("OpenWeatherMap.apikey"));
    RestKKlijent rc = new RestKKlijent(postavke);
    Aerodrom dohvaceniIcao = rc.dohvatiIcao(icao);
    lon = dohvaceniIcao.getLokacija().getLongitude();
    lat = dohvaceniIcao.getLokacija().getLatitude();

    try {
      return owmKlijent.getRealTimeWeather(lat, lon);
    } catch (NwtisRestIznimka e) {
      System.out.println(e.getMessage());
    }

    return null;
  }

  /**
   * Klasa RestKKlijent za interakciju sa REST klijentom.
   */
  static class RestKKlijent {
    /** Ciljna stranica */
    private final WebTarget webTarget;

    /** Klijent */
    private final Client client;

    /** Korijenski URI */
    private static String BASE_URI;

    RestKKlijent(PostavkeBazaPodataka postavke) {
      BASE_URI = postavke.dajPostavku("adresaAplikacije2");
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("aerodromi");
    }

    private Aerodrom dohvatiIcao(String icao) {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom aerodrom = gson.fromJson(request.get(String.class), Aerodrom.class);
      return aerodrom;
    }

  }
}
