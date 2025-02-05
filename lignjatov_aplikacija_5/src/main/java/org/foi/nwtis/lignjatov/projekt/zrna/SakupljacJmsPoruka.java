package org.foi.nwtis.lignjatov.projekt.zrna;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Singleton;

/**
 * Singleton Klasa SakupljacJmsPoruka.
 */
@Singleton
public class SakupljacJmsPoruka {

  /** kolekcija JMS poruki. */
  private List<String> jmsPoruke;

  /**
   * Instancira novi sakupljac jms poruka.
   */
  public SakupljacJmsPoruka() {
    jmsPoruke = new ArrayList<String>();
  }

  /**
   * Spremi poruku.
   *
   * @param poruka Poruka
   */
  public void spremiPoruku(String poruka) {
    jmsPoruke.add(poruka);
  }

  /**
   * Daj sve poruke.
   *
   * @return list Lista svih poruka
   */
  public List<String> dajSvePoruke() {
    return jmsPoruke;
  }

  public void obrisiPoruke() {
    jmsPoruke.clear();
  }

}
