package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.lignjatov.projekt.rest.RestKlijentDnevnik;
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
 * Klasa KontrolerDnevnik.
 */
@Controller
@Path("dnevnik")
@RequestScoped
public class KontrolerDnevnik {
  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** Injektirani model. */
  @Inject
  private Models model;

  /**
   * Postavlja u model poruke iz klase SakupuljacJmsPoruka.
   *
   * @param vrsta the vrsta
   * @param odBroja the od broja
   * @param broj the broj
   */
  @GET
  @View("dnevnik/dnevnikPodaci.jsp")
  public void dajZapise(@QueryParam("vrsta") String vrsta, @QueryParam("odBroja") int odBroja,
      @QueryParam("broj") int broj) {
    if (vrsta != null) {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      RestKlijentDnevnik rk = new RestKlijentDnevnik(postavke);
      var podaci = rk.dajDnevnik(vrsta, odBroja, broj);
      model.put("dnevnikRada", podaci);
    }
  }



}
