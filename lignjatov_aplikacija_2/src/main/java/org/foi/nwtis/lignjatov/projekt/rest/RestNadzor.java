package org.foi.nwtis.lignjatov.projekt.rest;

import org.foi.nwtis.ligjatov.projekt.pomocni.KomunikacijaPosluzitelj;
import org.foi.nwtis.podaci.PosluziteljOdgovor;
import com.google.gson.Gson;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * Klasa RestNadzor.
 */
@Path("nadzor")
@RequestScoped
public class RestNadzor {

  /**
   * Vraća status poslužitelja
   *
   * @return Odgovor poslužitelja u JSON formatu o statusu
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajStatus() {
    String[] odgovorPosluzitelj = KomunikacijaPosluzitelj.posaljiZahtjev("STATUS").split(" ");
    PosluziteljOdgovor odgovorPosluzitelja;
    odgovorPosluzitelja = statusOdgovora(odgovorPosluzitelj);
    var gson = new Gson();
    var jsonNadzor = gson.toJson(odgovorPosluzitelja);
    var odgovor = Response.ok(200).entity(jsonNadzor).build();
    return odgovor;
  }

  /**
   * Vraća status ovisno o komandi poslanoj
   *
   * @param komanda Komanda koja se šalje
   * @return response Odgovor poslužitelja u JSON formatu
   */
  @Path("/{komanda}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajStatus(@PathParam("komanda") String komanda) {
    String odgovorPosluzitelj = KomunikacijaPosluzitelj.posaljiZahtjev(komanda);
    PosluziteljOdgovor odgovorPosluzitelja = statusOdgovoraJedan(odgovorPosluzitelj);
    var gson = new Gson();
    var jsonNadzor = gson.toJson(odgovorPosluzitelja);
    var odgovor = Response.ok(200).entity(jsonNadzor).build();
    return odgovor;
  }

  /**
   * Šalje zahtjev poslužitelju i dobije natrag odgovor za INFO DA ili NE
   * 
   * @param vrsta može biti da ili ne
   * @return response Odgovor poslužitelja u JSON formatu
   */
  @Path("/INFO/{vrsta}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajInfo(@PathParam("vrsta") String vrsta) {
    String komanda = "INFO " + vrsta;
    String odgovorPosluzitelj = KomunikacijaPosluzitelj.posaljiZahtjev(komanda);
    PosluziteljOdgovor odgovorPosluzitelja = statusOdgovoraJedan(odgovorPosluzitelj);
    var gson = new Gson();
    var jsonNadzor = gson.toJson(odgovorPosluzitelja);
    var odgovor = Response.ok(200).entity(jsonNadzor).build();
    return odgovor;
  }

  /**
   * Status odgovora.
   *
   * @param odgovorPosluzitelj odgovor poslužitelja u string obliku ako je više od jednog znaka
   * @return posluzitelj odgovor u za zapis u JSON format
   */
  private PosluziteljOdgovor statusOdgovora(String[] odgovorPosluzitelj) {
    if (odgovorPosluzitelj[0].contains("OK")) {
      return new PosluziteljOdgovor("200", odgovorPosluzitelj[1]);
    } else {
      return new PosluziteljOdgovor("400", odgovorPosluzitelj[2]);
    }
  }

  /**
   * Status odgovora jedan.
   *
   * @param odgovorPosluzitelj odgovor poslužitelja u string obliku
   * @return posluzitelj odgovor u za zapis u JSON format
   */
  private PosluziteljOdgovor statusOdgovoraJedan(String odgovorPosluzitelj) {
    if (odgovorPosluzitelj.contains("OK")) {
      return new PosluziteljOdgovor("200", odgovorPosluzitelj);
    } else {
      return new PosluziteljOdgovor("400", odgovorPosluzitelj);
    }
  }

}
