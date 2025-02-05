package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.lignjatov.projekt.ws.WsKorisnici.endpoint.Korisnici;
import org.foi.nwtis.lignjatov.projekt.ws.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.lignjatov.projekt.zrna.PrijavljenKorisnik;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa korisnici
 */
@Controller
@Path("korisnici")
@RequestScoped
public class KontrolerKorisnici {

  /** Servis za korisnike */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/lignjatov_aplikacija_4/korisnici?wsdl")
  private Korisnici service;

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** Injektirani model. */
  @Inject
  private Models model;

  /** prijavljen korisnik. */
  @Inject
  private PrijavljenKorisnik prijavljenKorisnik;

  /**
   * Izbornik.
   */
  @GET
  @View("korisniciIzbornik.jsp")
  public void izbornik() {}


  /**
   * Registriraj korisnika.
   */
  @GET
  @Path("registracija")
  @View("korisnici/registracijaKorisnika.jsp")
  public void registrirajKorisnika() {
    model.put("uspjeh_unosa", false);
  }

  /**
   * Izvedi registraciju.
   *
   * @param korisnickoIme Korisnicko ime
   * @param korisnickaLozinka Korisnicka lozinka
   * @param korisnickiEmail Korisnicki email
   * @param ime Ime
   * @param prezime Prezime
   */
  @POST
  @Path("registracija")
  @View("korisnici/registracijaKorisnika.jsp")
  public void izvediRegistraciju(@FormParam("korisnickoIme") String korisnickoIme,
      @FormParam("korisnickaLozinka") String korisnickaLozinka,
      @FormParam("korisnickiEmail") String korisnickiEmail, @FormParam("ime") String ime,
      @FormParam("prezime") String prezime) {
    var port = service.getWsKorisniciPort();
    Korisnik korisnik = new Korisnik();
    korisnik.setIme(ime);
    korisnik.setPrezime(prezime);
    korisnik.setEmail(korisnickiEmail);
    korisnik.setKorisnickoIme(korisnickoIme);
    korisnik.setLozinka(korisnickaLozinka);
    boolean uspjeh;
    uspjeh = port.dodajKorisnik(korisnik);
    model.put("uspjeh_unosa", uspjeh);
  }


  /**
   * Prijavi korisnika.
   *
   * @param korisnickoIme Korisnicko ime
   * @param lozinka Lozinka
   */
  @GET
  @Path("prijava")
  @View("korisnici/prijavaKorisnika.jsp")
  public void prijaviKorisnika(@QueryParam("korisnickoIme") String korisnickoIme,
      @QueryParam("korisnickaLozinka") String lozinka) {
    var port = service.getWsKorisniciPort();
    if (korisnickoIme != null && lozinka != null) {
      Korisnik pokusajPrijave = port.dajKorisnik(korisnickoIme, lozinka, korisnickoIme);
      if (pokusajPrijave != null) {
        prijavljenKorisnik.postaviKorisnika(pokusajPrijave);
        model.put("stanje", pokusajPrijave.getKorisnickoIme());
        System.out.println("Naziv ulogiranog: " + pokusajPrijave.getKorisnickoIme());
        return;
      } else {
        model.put("stanje", "Korisnik ne postoji!");
      }
    }
  }

  /**
   * Filtriraj po korisnicima
   *
   * @param trazenoIme Trazeno ime
   * @param trazenoPrezime Trazeno prezime
   * @param odBroja Od broja
   * @param broj Broj podataka
   */
  @GET
  @Path("filter")
  @View("korisnici/filterKorisnika.jsp")
  public void filterKorisnika(@QueryParam("trazenoIme") String trazenoIme,
      @QueryParam("trazenoPrezime") String trazenoPrezime, @QueryParam("odBroja") int odBroja,
      @QueryParam("broj") int broj) {
    // model.put("prijavljeniKorisnik", korisnik.getKorisnik().getKorisnickoIme());
    var port = service.getWsKorisniciPort();
    if (prijavljenKorisnik.isPrijavljen()) {
      Korisnik korisnik = prijavljenKorisnik.getKorisnik();
      // var korisnici = port.dajKorisnike("lignjatov", "lignjatov", trazenoIme, trazenoPrezime, 0,
      // 0);
      var korisnici = port.dajKorisnike(korisnik.getKorisnickoIme(), korisnik.getLozinka(),
          trazenoIme, trazenoPrezime, odBroja, broj);
      model.put("korisnici", korisnici);
    }
  }
}
