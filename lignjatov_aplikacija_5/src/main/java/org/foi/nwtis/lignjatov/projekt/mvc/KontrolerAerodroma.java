/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.lignjatov.projekt.rest.RestKlijentAerodromi;
import org.foi.nwtis.lignjatov.projekt.ws.WsAerodromi.endpoint.Aerodromi;
import org.foi.nwtis.lignjatov.projekt.ws.WsAerodromi.endpoint.WsAerodromi;
import org.foi.nwtis.lignjatov.projekt.ws.WsMeteo.endpoint.Meteo;
import org.foi.nwtis.lignjatov.projekt.zrna.PrijavljenKorisnik;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa KontrolerAerodroma.
 *
 * @author NWTiS
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  /** The service. */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/lignjatov_aplikacija_4/aerodromi?wsdl")
  private Aerodromi service;

  /** The service meteo. */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/lignjatov_aplikacija_4/meteo?wsdl")
  private Meteo serviceMeteo;

  /** The prijavljen korisnik. */
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
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  /**
   * Izbornik aerodrom.
   */
  @GET
  @Path("pocetakAerodromi")
  @View("aerodromiIzbornik.jsp")
  public void izbornikAerodrom() {}

  /**
   * Servis za dohvatiti sve aerodrome.
   *
   * @param odBroja Početak brojanja elemenata
   * @param broj Broj elemenata koji želimo izvući
   * @param trazeniAerodrom the trazeni aerodrom
   * @param drzava the drzava
   * @param icaoSpremi the icao spremi
   */
  @GET
  @Path("svi")
  @View("aerodromi/aerodromi.jsp")
  public void dajAerodrome(@QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj,
      @QueryParam("trazeniAerodrom") String trazeniAerodrom,
      @QueryParam("trazenaDrzava") String drzava, @QueryParam("spremi") String icaoSpremi) {
    model.put("greska", "");
    PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
    RestKlijentAerodromi rc = new RestKlijentAerodromi(postavke);
    var aerodromi = rc.dajAerodromi(trazeniAerodrom, drzava, odBroja, broj);
    if (icaoSpremi != null) {
      var port = service.getWsAerodromiPort();
      if (prijavljenKorisnik.isPrijavljen()) {
        var korisnik = prijavljenKorisnik.getKorisnik();
        port.dodajAerodromZaLetove(korisnik.getKorisnickoIme(), korisnik.getLozinka(), icaoSpremi);
      } else {
        model.put("greska", "Korisnik nije prijavljen");
      }
    }
    model.put("aerodromi", aerodromi);
  }

  /**
   * Daj aerodrom za letove. Ako je postavljen status, može se mijenjati stanje aerodroma
   *
   * @param icao icao
   * @param odBroja od broja
   * @param broj broj
   * @param status status
   */
  @GET
  @Path("sakupljanje")
  @View("aerodromi/sakupljanje.jsp")
  public void dajAerodromZaLetove(@QueryParam("icao") String icao,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj,
      @QueryParam("promjeniStatus") String status) {
    model.put("greska", "");
    var port = service.getWsAerodromiPort();
    if (prijavljenKorisnik.isPrijavljen()) {
      var korisnik = prijavljenKorisnik.getKorisnik();
      if (status != null && icao != null) {
        promjeniStatus(icao, status, port);
      }
      var aerodromi = port.dajAerodromeZaLetove(korisnik.getKorisnickoIme(),
          korisnik.getKorisnickoIme(), odBroja, broj);
      model.put("aerodromi", aerodromi);
    } else {
      model.put("greska", "Korisnik nije prijavljen");
    }
  }

  /**
   * Promjeni status.
   *
   * @param icao aerodrom
   * @param status trenutni status
   * @param port web servis
   */
  private void promjeniStatus(String icao, String status, WsAerodromi port) {
    var korisnik = prijavljenKorisnik.getKorisnik();
    if (status.compareTo("false") == 0) {
      port.aktivirajAerodromeZaLetove(korisnik.getKorisnickoIme(), korisnik.getLozinka(), icao);
    } else {
      port.pauzirajAerodromeZaLetove(korisnik.getKorisnickoIme(), korisnik.getLozinka(), icao);
    }
  }


  /**
   * Daj jedan aerodrom
   *
   * @param icao Traženi aerodrom
   */
  @GET
  @Path("{icao}")
  @View("aerodromi/aerodrom.jsp")
  public void dajAerodrom(@PathParam("icao") String icao) {
    try {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      RestKlijentAerodromi rc = new RestKlijentAerodromi(postavke);
      var aerodrom = rc.dajAerodrom(icao);
      var port = serviceMeteo.getWsMeteoPort();
      var stanje = port.dajMeteo(icao);
      model.put("aerodrom", aerodrom);
      model.put("meteo", stanje);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Daj udaljenost između 2 aerodroma
   *
   * @param icaoOd aerodrom od
   * @param icaoDo aerodrom do
   */
  @GET
  @Path("udaljenosti")
  @View("aerodromi/aerodromiUdaljenost.jsp")
  public void dajUdaljenost(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
    var rc = new RestKlijentAerodromi(postavke);
    var aerodromi = rc.dajAerodromiPut(icaoOd, icaoDo);

    float udaljenosti = 0;
    for (var e : aerodromi) {
      udaljenosti += e.km();
    }
    model.put("aerodromi", aerodromi);
    model.put("ukupnaUdaljenost", udaljenosti);

  }

  /**
   * Daj udaljenost putem posluzitelja koji udaljenost izračuna
   *
   * @param icaoOd aerodrom od
   * @param icaoDo aerodrom do
   */
  @GET
  @Path("udaljenostiPosluzitelj")
  @View("aerodromi/aerodromiUdaljenostPosluzitelj.jsp")
  public void dajUdaljenostPosluzitelj(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {

    if (icaoOd != null && icaoDo != null) {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      var rc = new RestKlijentAerodromi(postavke);
      var aerodromi = rc.dajAerodromiPutPosluzitelj(icaoOd, icaoDo);

      float udaljenosti = 0;
      for (var e : aerodromi) {
        udaljenosti += e.km();
      }
      model.put("aerodromi", aerodromi);
      model.put("ukupnaUdaljenost", udaljenosti);
    }
  }

  /**
   * Daj aerodrome koji imaju udaljenost manju od icaoOd i icaoDo
   *
   * @param icaoOd the icao od
   * @param icaoDo the icao do
   */
  @GET
  @Path("udaljenostiManjiOdDva")
  @View("aerodromi/aerodromiManjiOdDva.jsp")
  public void dajUdaljenostNesto(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    if (icaoOd != null && icaoDo != null) {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      var rc = new RestKlijentAerodromi(postavke);
      var udaljenost = rc.dajAerodromiUdaljenost(icaoOd, icaoDo);
      var aerodrom = rc.dajAerodrom(icaoOd);
      var aerodromi =
          rc.dajUdaljenostiManjeDrzava(aerodrom.getIcao(), aerodrom.getDrzava(), udaljenost.km());
      model.put("aerodromi", aerodromi);
      model.put("manjiOd", udaljenost.km());
    }

  }

  /**
   * Daj udaljenosti koji se nalaze unutar države i manji su od param km
   *
   * @param icao icao
   * @param drzava drzava
   * @param km kilometri
   */
  @GET
  @Path("udaljenostiManji")
  @View("aerodromi/aerodromiUdaljenostManji.jsp")
  public void dajUdaljenostNesto(@QueryParam("icao") String icao,
      @QueryParam("drzava") String drzava, @QueryParam("km") float km) {
    if (icao != null && drzava != null && km != 0) {
      PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sc.getAttribute("konfig");
      var rc = new RestKlijentAerodromi(postavke);
      var aerodromi = rc.dajUdaljenostiManjeDrzava(icao, drzava, km);
      model.put("aerodromi", aerodromi);
    }
  }
}
