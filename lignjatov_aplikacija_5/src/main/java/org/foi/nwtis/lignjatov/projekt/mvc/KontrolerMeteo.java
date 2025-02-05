package org.foi.nwtis.lignjatov.projekt.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa KontrolerAerodroma.
 *
 * @author NWTiS
 */
@Controller
@Path("meteo")
@RequestScoped
public class KontrolerMeteo {

  /** Servis meteo */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/lignjatov_aplikacija_4/meteo?wsdl")
  // private Meteo service;

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** Injektirani model. */
  @Inject
  private Models model;

  /**
   * Pocetak.
   */
  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  /**
   * Daj meteorološke podatke aerodroma
   *
   * @param icao Icao
   */
  @GET
  @Path("{icao}")
  @View("meteoIcao.jsp")
  public void dajMeteo(@PathParam("icao") String icao) {
    try {

      // var port = service.getWsMeteoPort();
      // var meteo = port.dajMeteo(icao);
      int meteo = 0;
      model.put("meteo", meteo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

