/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.lignjatov.projekt.mvc;

import org.foi.nwtis.lignjatov.projekt.zrna.SakupljacJmsPoruka;
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
 * Klasa KontrolerAerodroma.
 *
 * @author NWTiS
 */
@Controller
@Path("poruke")
@RequestScoped
public class KontrolerPoruke {

  /** Kontekst servleta. */
  @Context
  private ServletContext sc;

  /** Injektirani model. */
  @Inject
  private Models model;

  /** Sakupljac. */
  @Inject
  SakupljacJmsPoruka sakupljac;

  /**
   * Pocetak.
   */
  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  /**
   * Postavlja u model poruke iz klase SakupuljacJmsPoruka
   */
  @GET
  @View("jms/porukeLetovi.jsp")
  public void dajPoruke(@QueryParam("obrisi") String obrisi) {
    if (obrisi != null) {
      sakupljac.obrisiPoruke();
    }
    model.put("poruke", sakupljac.dajSvePoruke());
  }
}

