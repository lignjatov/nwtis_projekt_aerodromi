package org.foi.nwtis.lignjatov.projekt;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Klasa pokretač poslužitelja
 */
public class PokretacPosluzitelja {

  /**
   * Instancira novog pokretača poslužitelja.
   */
  public PokretacPosluzitelja() {}

  /**
   * Glavna metoda
   *
   * @param args Argumenti
   */
  public static void main(String[] args) {
    var pokretacPosluzitelja = new PokretacPosluzitelja();
    if (pokretacPosluzitelja.provjeriArgumente(args)) {
      try {
        Konfiguracija konf = pokretacPosluzitelja.ucitajPostavke(args[0]);
        var glavniPosluzitelj = new GlavniPosluzitelj(konf);
        glavniPosluzitelj.pokreniPosluzitelj();
      } catch (NeispravnaKonfiguracija e) {
        Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
            "Greška sa čitanjem postavki poslužitelja");
      }
    }
  }

  /**
   * Provjerava broj argumenata
   *
   * @param argumenti
   * @return true ako je potreban broj argumenata predan, inače false
   */
  boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      return true;
    }
    return false;
  }

  /**
   * Ucitaj postavke.
   *
   * @param nazivDatoteke Naziv datoteke
   * @return Konfiguracija
   * @throws NeispravnaKonfiguracija Neispravna konfiguracija
   */
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }
}
