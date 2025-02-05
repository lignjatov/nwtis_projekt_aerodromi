package org.foi.nwtis.lignjatov.projekt;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;

/**
 * Klasa GlavniPosluzitelj.
 */
public class GlavniPosluzitelj {

  /** Konfiguracija. */
  protected Konfiguracija konf;

  /** Mrežan vrata */
  private int mreznaVrata = 8000;

  /** Broj čekaća */
  private int brojCekaca = 10;

  /** The broj aktivnih dretvi. */
  protected static volatile int brojAktivnihDretvi = 0;
  protected static volatile int status = 0;
  protected static volatile boolean init = false;
  protected static volatile boolean info = false;
  protected static volatile int brojIzracuna = 0;

  /** Kraj rada poslužitelja */
  protected static volatile boolean kraj = false;

  /**
   * Instanciranje glavnog poslužitelja.
   *
   * @param konf Konfiguracija
   */
  public GlavniPosluzitelj(Konfiguracija konf) {
    super();
    this.konf = konf;
    this.mreznaVrata = Integer.parseInt(this.konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(this.konf.dajPostavku("brojCekaca"));
  }

  /**
   * Pokreni posluzitelj.
   */
  public void pokreniPosluzitelj() {
    if (this.provjeriVrata() == false) {
      Logger.getGlobal().log(Level.SEVERE, "Port je već zauzet");
      return;
    }
    this.otvoriMreznaVrata();
  }

  /**
   * Otvaranje mrežnih vrata za spajanje na poslužitelj
   */
  public void otvoriMreznaVrata() {
    try {
      var posluzitelj = new ServerSocket(this.mreznaVrata, this.brojCekaca);
      while (!GlavniPosluzitelj.kraj) {
        if (GlavniPosluzitelj.kraj) {
          while (GlavniPosluzitelj.brojAktivnihDretvi > 0) {
          }
          posluzitelj.close();
          return;
        }

        var uticnica = posluzitelj.accept();

        String naziv_dretve = "lignjatov_" + GlavniPosluzitelj.brojAktivnihDretvi;
        var dretva = new MrezniRadnik(uticnica, this.konf, this);
        dretva.setName(naziv_dretve);
        dretva.start();

        if (GlavniPosluzitelj.kraj == true) {
          posluzitelj.close();
          return;
        }
      }

      while (GlavniPosluzitelj.brojAktivnihDretvi > 0) {
      }
      posluzitelj.close();

    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }

  // Ideja za ovu implementaciju preuzeta sa //
  // https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
  /**
   * Provjeri dostupnost mrežnih vrata
   *
   * @return true ako je moguće otvoriti mrežna vrata, inače false
   */
  public boolean provjeriVrata() {
    try {
      var posluzitelj = new ServerSocket(this.mreznaVrata, this.brojCekaca);
      posluzitelj.close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

}
