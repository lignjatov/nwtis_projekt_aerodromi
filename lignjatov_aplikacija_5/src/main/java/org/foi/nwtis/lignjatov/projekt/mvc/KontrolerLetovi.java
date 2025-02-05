package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.lignjatov.projekt.ws.WsLetovi.endpoint.Letovi;
import org.foi.nwtis.lignjatov.projekt.zrna.PrijavljenKorisnik;
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
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa KontrolerLetovi.
 */
@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetovi {

  /** Servis letova */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/lignjatov_aplikacija_4/letovi?wsdl")
  private Letovi service;

  /** Prijavljen korisnik. */
  @Inject
  PrijavljenKorisnik prijavljenKorisnik;

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
  @View("letoviIzbornik.jsp")
  public void pocetak() {}

  /**
   * Daj spremljene letove
   *
   * @param icao the icao
   * @param datumOd Datum od
   * @param datumDo Datum do
   * @param odBroja Od broja
   * @param broj Broj podataka
   */
  @GET
  @Path("spremljeni")
  @View("letovi/spremljeniLetovi.jsp")
  public void dajSpremljene(@QueryParam("icao") String icao, @QueryParam("datumOd") String datumOd,
      @QueryParam("datumDo") String datumDo, @QueryParam("odBroja") int odBroja,
      @QueryParam("broj") int broj) {
    model.put("greska", "");
    if (prijavljenKorisnik.isPrijavljen()) {
      if (icao != null && datumOd != null && datumDo != null) {
        var port = service.getWsLetoviPort();
        var korisnik = prijavljenKorisnik.getKorisnik();
        var letovi = port.dajPolaskeInterval(korisnik.getKorisnickoIme(), korisnik.getLozinka(),
            odBroja, broj, icao, datumOd, datumDo);
        model.put("letovi", letovi);
      }

    } else {
      model.put("greska", "Korisnik nije prijavljen!");
    }
  }

  /**
   * Daj letove dana.
   *
   * @param icao Icao
   * @param datum Datum
   * @param odBroja Od broja
   * @param broj Broj
   */
  @GET
  @Path("dan")
  @View("letovi/spremljeniLetoviDan.jsp")
  public void dajLetoveDana(@QueryParam("icao") String icao, @QueryParam("dan") String datum,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    model.put("greska", "");
    if (prijavljenKorisnik.isPrijavljen()) {
      if (icao != null && datum != null) {
        var port = service.getWsLetoviPort();
        var korisnik = prijavljenKorisnik.getKorisnik();
        var letovi = port.dajPolaskeNaDan(korisnik.getKorisnickoIme(), korisnik.getLozinka(),
            odBroja, broj, icao, datum);
        model.put("letovi", letovi);
      }
    } else {
      model.put("greska", "Korisnik nije prijavljen!");
    }
  }


  /**
   * Daj letove dana OS. Ovdje se podaci dobivaju sa OpenSkyNetwork, a ne iz baze
   *
   * @param icao Icao
   * @param datum Datum
   * @param odBroja Od broja
   * @param broj Broj
   */
  @GET
  @Path("os")
  @View("letovi/spremljeniLetoviDan.jsp")
  public void dajLetoveDanaOS(@QueryParam("icao") String icao, @QueryParam("dan") String datum,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    model.put("greska", "");
    if (prijavljenKorisnik.isPrijavljen()) {
      if (icao != null && datum != null) {
        var port = service.getWsLetoviPort();
        var korisnik = prijavljenKorisnik.getKorisnik();
        var letovi =
            port.dajPolaskeNaDanOS(korisnik.getKorisnickoIme(), korisnik.getLozinka(), icao, datum);
        model.put("letovi", letovi);
      }
    } else {
      model.put("greska", "Korisnik nije prijavljen!");
    }
  }
}
