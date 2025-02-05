package org.foi.nwtis.lignjatov.projekt.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import com.google.gson.Gson;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

/**
 * Klasa RestKlijentAerodromi.
 */
public class RestKlijentAerodromi {

  /** Postavke. */
  PostavkeBazaPodataka postavke;

  /**
   * Instanciranje klase
   *
   * @param baz bazičniPodaci potrebno za rad
   */
  public RestKlijentAerodromi(PostavkeBazaPodataka baz) {
    this.postavke = baz;
  }

  /**
   * Daj aerodrome prema uvjetima postavljenim
   *
   * @param trazeniAerodrom Traženi aerodrom
   * @param drzava Država
   * @param odBroja Od broja
   * @param broj Broj
   * @return lista
   */
  public List<Aerodrom> dajAerodromi(String trazeniAerodrom, String drzava, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(postavke);
    Aerodrom[] json_Aerodromi = rc.dajAerodromi(trazeniAerodrom, drzava, odBroja, broj);
    List<Aerodrom> aerodromi;
    if (json_Aerodromi == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(json_Aerodromi);
    }
    return aerodromi;
  }


  /**
   * Daj aerodrom
   *
   * @param icao Icao
   * @return aerodrom
   */
  public Aerodrom dajAerodrom(String icao) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var aerodrom = rk.dajAerodrom(icao);
    return aerodrom;
  }

  /**
   * Daj udaljenost od aerodroma
   *
   * @param icaoOd Icao od
   * @param icaoDo Icao do
   * @return Lista aerodroma,udaljenosti i država
   */
  public List<UdaljenostAerodromDrzava> dajAerodromiPut(String icaoOd, String icaoDo) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var aerodromiPolje = rk.dajAerodromiPut(icaoOd, icaoDo);
    List<UdaljenostAerodromDrzava> aerodromi = null;
    if (aerodromiPolje == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(aerodromiPolje);
    }
    rk.close();
    return aerodromi;
  }

  /**
   * Daj aerodromi put posluzitelj.
   *
   * @param icaoOd Icao od
   * @param icaoDo Icao do
   * @return List
   */
  public List<UdaljenostAerodromDrzava> dajAerodromiPutPosluzitelj(String icaoOd, String icaoDo) {
    RestKKlijent rk = new RestKKlijent(postavke);;
    var aerodromiPolje = rk.dajAerodromiPutPosluzitelj(icaoOd, icaoDo);
    List<UdaljenostAerodromDrzava> aerodromi = null;
    if (aerodromiPolje == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(aerodromiPolje);
    }
    rk.close();
    return aerodromi;
  }

  /**
   * Daj aerodromi udaljenost.
   *
   * @param icaoOd the icao od
   * @param icaoDo the icao do
   * @return the udaljenost aerodrom
   */
  public UdaljenostAerodrom dajAerodromiUdaljenost(String icaoOd, String icaoDo) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var udaljenost = rk.dajAerodromiUdaljenost(icaoOd, icaoDo);
    return udaljenost;
  }

  /**
   * Daj udaljenosti manje drzava.
   *
   * @param icao the icao
   * @param drzava the drzava
   * @param km the km
   * @return the list
   */
  public List<UdaljenostAerodromDrzava> dajUdaljenostiManjeDrzava(String icao, String drzava,
      float km) {
    RestKKlijent rk = new RestKKlijent(postavke);
    var aerodromiPolje = rk.dajUdaljenostiManjeDrzava(icao, drzava, km);
    List<UdaljenostAerodromDrzava> aerodromi = null;
    if (aerodromiPolje == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(aerodromiPolje);
    }
    rk.close();
    return aerodromi;
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
     * Instancira novi RestKKlijent.
     *
     * @param postavke Postavke
     */
    RestKKlijent(PostavkeBazaPodataka postavke) {
      BASE_URI = postavke.dajPostavku("adresaAplikacije2");
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("aerodromi");
    }

    /**
     * Daj aerodrome u rasponu.
     *
     * @param trazeniAerodrom Traženi aerodrom
     * @param trazenaDrzava Tražena država
     * @param odBroja Početak brojanja elemenata
     * @param broj Broj elemenata koji želimo izvući
     * @return Aerodrom[] Polje aerodroma za daljnju obradu
     * @throws ClientErrorException Grešk?icaoOd=LDZA&icaoDo=LOWWa s klijentom
     */
    public Aerodrom[] dajAerodromi(String trazeniAerodrom, String trazenaDrzava, int odBroja,
        int broj) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.queryParam("traziNaziv", trazeniAerodrom);
      resource = resource.queryParam("traziDrzavu", trazenaDrzava);
      resource = resource.queryParam("odBroja", odBroja);
      resource = resource.queryParam("broj", broj);

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom[] aerodromi = gson.fromJson(request.get(String.class), Aerodrom[].class);
      return aerodromi;
    }

    /**
     * Daj aerodrom.
     *
     * @param icao Icao
     * @return Aerodrom
     * @throws ClientErrorException the client error exception
     */
    public Aerodrom dajAerodrom(String icao) throws ClientErrorException {
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

    // public Aerodrom dajSakupljanje(String icao) throws ClientErrorException {
    // WebTarget resource = webTarget;
    // resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
    // Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    // if (request.get(String.class).isEmpty()) {
    // return null;
    // }
    // Gson gson = new Gson();
    // Aerodrom aerodrom = gson.fromJson(request.get(String.class), Aerodrom.class);
    // return aerodrom;
    // }

    /**
     * Daj udaljenost između dva aerodroma.
     *
     * @param icaoOd Polazišni aerodrom
     * @param icaoDo Odredišni aerodrom
     * @return udaljenostAerodrom[] Polje svih udaljenosti
     */
    public UdaljenostAerodromDrzava[] dajAerodromiPut(String icaoOd, String icaoDo) {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] {icaoOd, icaoDo}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodromDrzava[] aerodromi =
          gson.fromJson(request.get(String.class), UdaljenostAerodromDrzava[].class);
      return aerodromi;
    }

    /**
     * Daj aerodromi put posluzitelj.
     *
     * @param icaoOd Icao od
     * @param icaoDo Icao do
     * @return udaljenosti aerodrom drzava[]
     */
    public UdaljenostAerodromDrzava[] dajAerodromiPutPosluzitelj(String icaoOd, String icaoDo) {
      WebTarget resource = webTarget;
      resource = resource.path(
          java.text.MessageFormat.format("{0}/udaljenost1/{1}", new Object[] {icaoOd, icaoDo}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodromDrzava[] aerodromi =
          gson.fromJson(request.get(String.class), UdaljenostAerodromDrzava[].class);
      return aerodromi;
    }

    /**
     * Daj aerodromi udaljenost uz poslužitelj
     *
     * @param icaoOd Icao od
     * @param icaoDo Icao do
     * @return Udaljenost aerodrom
     */
    public UdaljenostAerodrom dajAerodromiUdaljenost(String icaoOd, String icaoDo) {
      WebTarget resource = webTarget;
      resource = resource
          .path(java.text.MessageFormat.format("{0}/izracunaj/{1}", new Object[] {icaoOd, icaoDo}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodrom aerodromi =
          gson.fromJson(request.get(String.class), UdaljenostAerodrom.class);
      return aerodromi;
    }

    /**
     * Daj udaljenosti uz parametre.
     *
     * @param icao Icao
     * @param drzava Država
     * @param km Km
     * @return Udaljenost aerodrom drzava[]
     */
    public UdaljenostAerodromDrzava[] dajUdaljenostiManjeDrzava(String icao, String drzava,
        float km) {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}/udaljenost2", icao));
      resource = resource.queryParam("drzava", drzava);
      resource = resource.queryParam("km", km);

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodromDrzava[] aerodromi =
          gson.fromJson(request.get(String.class), UdaljenostAerodromDrzava[].class);
      return aerodromi;
    }

    /**
     * Close.
     */
    public void close() {
      client.close();
    }
  }
}
