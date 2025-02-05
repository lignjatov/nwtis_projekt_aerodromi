package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.lignjatov.projekt.rest.RestKlijentInfo;
import org.foi.nwtis.podaci.PosluziteljOdgovor;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

/**
 * Klasa KontrolerInfo.
 */
@Controller
@Path("info")
@RequestScoped
public class KontrolerInfo {

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** Injektirani model. */
  @Inject
  private Models model;


  /**
   * Izbornik komandi za poslužitelj
   *
   * @param komanda komanda za poslužitelj
   */
  @GET
  @View("posluzitelj/info.jsp")
  public void izbornik(@QueryParam("komanda") String komanda) {
    if (komanda != null) {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      RestKlijentInfo rk = new RestKlijentInfo(postavke);
      PosluziteljOdgovor odgovor = rk.posaljiKomandu(komanda);
      model.put("odgovorPosluzitelja", odgovor.opis());
    }
  }


}
