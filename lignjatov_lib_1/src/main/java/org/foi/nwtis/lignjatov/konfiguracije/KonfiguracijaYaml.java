/**
 * 
 */
package org.foi.nwtis.lignjatov.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

//
/**
 * Klasa KonfiguracijaYaml za rad s postavkama konfiguracije u yaml formatu
 *
 * @author Lucas Ignjatov
 */
public class KonfiguracijaYaml extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "yaml";

  /**
   * Instancira klasu KonfiguracijaYaml
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaYaml(String nazivDatoteke) {
    super(nazivDatoteke);

  }

  /**
   * Spremi konfiguraciju.
   *
   * @param datoteka Datoteka
   * @throws NeispravnaKonfiguracija ako nije ispravna konfiguracija
   */
  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {}

  /**
   * Ucitaj konfiguraciju.
   *
   * @throws NeispravnaKonfiguracija ako nije ispravna konfiguracija
   */
  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {}

}
