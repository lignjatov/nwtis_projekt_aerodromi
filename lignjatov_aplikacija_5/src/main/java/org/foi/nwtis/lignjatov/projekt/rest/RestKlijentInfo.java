package org.foi.nwtis.lignjatov.projekt.rest;

import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.PosluziteljOdgovor;
import com.google.gson.Gson;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

/**
 * Klasa RestKlijentInfo.
 */
public class RestKlijentInfo {

  /** Postavke. */
  PostavkeBazaPodataka postavke;

  /**
   * Instanciranje novog rest klijent info.
   *
   * @param baz bazični podaci
   */
  public RestKlijentInfo(PostavkeBazaPodataka baz) {
    this.postavke = baz;
  }

  /**
   * Posalji komandu.
   *
   * @param komanda Komanda
   * @return posluzitelj odgovor poslužitelja
   */
  public PosluziteljOdgovor posaljiKomandu(String komanda) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var odgovor = rk.posaljiKomandu(komanda);
    return odgovor;
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
     * Instanciranje novog rest K klijent.
     *
     * @param postavke Postavke
     */
    RestKKlijent(PostavkeBazaPodataka postavke) {
      BASE_URI = postavke.dajPostavku("adresaAplikacije2");
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nadzor");
    }

    /**
     * Posalji komandu.
     *
     * @param komanda Komanda za poslužitelj
     * @return posluzitelj odgovor poslužitelja
     */
    private PosluziteljOdgovor posaljiKomandu(String komanda) {
      WebTarget resource = webTarget;
      if (komanda.contains("INFO")) {
        String[] info = komanda.split(" ");
        resource = resource
            .path(java.text.MessageFormat.format("{0}/{1}", new Object[] {info[0], info[1]}));
      } else {
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {komanda}));
      }
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Gson gson = new Gson();
      PosluziteljOdgovor aerodrom =
          gson.fromJson(request.get(String.class), PosluziteljOdgovor.class);
      return aerodrom;
    }
  }


}
