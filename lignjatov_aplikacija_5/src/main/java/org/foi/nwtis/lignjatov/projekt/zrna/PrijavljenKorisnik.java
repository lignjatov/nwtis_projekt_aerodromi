package org.foi.nwtis.lignjatov.projekt.zrna;

import org.foi.nwtis.lignjatov.projekt.ws.WsKorisnici.endpoint.Korisnik;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.SessionScoped;

/**
 * Klasa PrijavljenKorisnik.
 */
@Stateful
@SessionScoped
public class PrijavljenKorisnik {

  /** Korisnik. */
  private Korisnik korisnik;

  /** Prijavljen */
  private boolean prijavljen = false;


  /**
   * Prijava korisnika
   *
   * @param prijavljeniKorisnik prijavljeni korisnik
   */
  public void postaviKorisnika(Korisnik prijavljeniKorisnik) {
    korisnik = prijavljeniKorisnik;
    prijavljen = true;
  }

  /**
   * Vraća korisnika
   *
   * @return Korisnik
   */
  public Korisnik getKorisnik() {
    return korisnik;
  }

  /**
   * Vraća status je li se netko prijavio ili ne
   *
   * @return true ako je prijavljen, inače false
   */
  public boolean isPrijavljen() {
    return prijavljen;
  }
}
