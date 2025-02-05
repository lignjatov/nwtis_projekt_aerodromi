package org.foi.nwtis.lignjatov.projekt.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Dnevnik;
import com.google.gson.Gson;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;


/**
 * Kontekst servleta.
 */

public class RestKlijentDnevnik {

  /** Postavke. */
  PostavkeBazaPodataka postavke;

  /**
   * Instanciranje novog rest klijent dnevnik objekta.
   *
   * @param baz bazični podaci
   */
  public RestKlijentDnevnik(PostavkeBazaPodataka baz) {
    this.postavke = baz;
  }

  /**
   * Daj dnevnik.
   *
   * @param vrsta Vrsta podataka (AP2, AP4, AP5)
   * @param odBroja Od kojeg broja
   * @param broj Količina podataka
   * @return list zapisa dnevnika
   */
  public List<Dnevnik> dajDnevnik(String vrsta, int odBroja, int broj) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var podaci = rk.dajDnevnik(vrsta, odBroja, broj);
    List<Dnevnik> dnevnik;
    if (podaci == null) {
      dnevnik = new ArrayList<>();
    } else {
      dnevnik = Arrays.asList(podaci);
    }
    return dnevnik;
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
      webTarget = client.target(BASE_URI).path("dnevnik");
    }

    /**
     * Daj dnevnik.
     *
     * @param vrsta Vrsta (AP2,AP4,AP5)
     * @param odBroja Offset podataka
     * @param broj Količina podataka
     * @return dnevnik[] zapise dnevnika
     * @throws ClientErrorException the client error exception
     */
    public Dnevnik[] dajDnevnik(String vrsta, int odBroja, int broj) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.queryParam("vrsta", vrsta);
      resource = resource.queryParam("odBroja", odBroja);
      resource = resource.queryParam("broj", broj);

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Dnevnik[] zapisi = gson.fromJson(request.get(String.class), Dnevnik[].class);
      return zapisi;
    }
  }
}
