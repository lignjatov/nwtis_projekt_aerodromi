package org.foi.nwtis.lignjatov.projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;

/**
 * Klasa MrezniRadnik.
 */
public class MrezniRadnik extends Thread {

  /** Mrezna uticnica. */
  protected Socket mreznaUticnica;

  /** Konfiguracija. */
  protected Konfiguracija konfig;

  /** Uzorak. */
  Pattern uzorak;

  /** Posluzitelj. */
  GlavniPosluzitelj posluzitelj;


  /**
   * Instanciranje mrežnog radnika.
   *
   * @param mreznaUticnica Mrezna uticnica
   * @param konfig Konfiguracija
   * @param posluzitelj Posluzitelj
   */
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig, GlavniPosluzitelj posluzitelj) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
    String sintaksa =
        "^(?<komanda>STATUS|KRAJ|INIT|PAUZA|INFO (?<infoStatus>DA|NE)|UDALJENOST (?<podaci>([+|-]?\\d{1,2}.\\d+ ?){4}))$";
    uzorak = Pattern.compile(sintaksa);
    this.posluzitelj = posluzitelj;
  }

  /**
   * Početak.
   */
  @Override
  public synchronized void start() {
    super.start();
  }

  /**
   * Pokretanje.
   */
  @Override
  public void run() {
    try {
      GlavniPosluzitelj.brojAktivnihDretvi++;
      var citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      var poruka = new StringBuilder();
      while (true) {
        var redak = citac.readLine();
        if (redak == null) {
          break;
        } else {
          if (GlavniPosluzitelj.info == true) {
            System.out.println(redak);
          }
          poruka.append(redak);
        }
      }
      this.mreznaUticnica.shutdownInput();
      var odgovor = this.obradiZahtjev(poruka.toString());
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
      GlavniPosluzitelj.brojAktivnihDretvi--;
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }

    super.run();
  }

  /**
   * Obrađivanje zahtjeva, provjera komande i koja se metoda poziva.
   *
   * @param komanda Komanda
   * @return the string
   */
  public String obradiZahtjev(String komanda) {
    Matcher m = uzorak.matcher(komanda);
    if (!m.matches()) {
      return "ERROR 05: Krivi format poruke";
    }

    String odgovor = m.group("komanda").trim().split(" ")[0];
    switch (odgovor) {
      case "KRAJ":
        return this.ugasiServer();
      case "STATUS":
        return this.vratiStatus();
    }
    if (GlavniPosluzitelj.status == 1) {
      switch (odgovor) {
        case "PAUZA":
          return this.postaviPauzu();
        case "INFO":
          String info = m.group("infoStatus");
          return this.promjeniInfo(info);
        case "UDALJENOST":
          return this.komandaUdaljenost(m);
        case "INIT":
          return "ERROR 02: SERVER VEĆ INICIJALIZIRAN";
        default:
          return "ERROR 05: KOMANDA NIJE POZNATA";
      }
    } else {
      if (odgovor.compareTo("INIT") == 0)
        return this.inicijalizirajServer();
    }
    return "ERROR 01: SERVER JE PAUZIRAN";
  }

  /**
   * Postavi pauzu za poslužitelj
   *
   * @return string uspjeha
   */
  private String postaviPauzu() {
    GlavniPosluzitelj.status = 0;
    return "OK " + GlavniPosluzitelj.brojIzracuna;
  }

  /**
   * Inicijaliziraj server.
   *
   * @return string uspjeh
   */
  private String inicijalizirajServer() {
    GlavniPosluzitelj.status = 1;
    GlavniPosluzitelj.brojIzracuna = 0;
    return "OK";
  }

  /**
   * Promjeni info. U slučaju da je postavka već postavljena, vraća grešku
   *
   * @param info info ima format INFO DA|NE i ovisno o tome pali ili gasi info
   * @return string uspjeha ili greške
   */
  private String promjeniInfo(String info) {
    if (info.contains("DA") || info.contains("NE")) {
      if (info.compareTo("DA") == 0 && GlavniPosluzitelj.info == true) {
        return "ERROR 03: POSTAVKA INFO VEĆ POSTAVLJENA NA DA";
      }
      if (info.compareTo("DA") == 0) {
        GlavniPosluzitelj.info = true;
        return "OK";
      }

      if (info.compareTo("NE") == 0 && GlavniPosluzitelj.info == false) {
        return "ERROR 04: POSTAVKA INFO VEĆ POSTAVLJENA NA NE";
      }

      if (info.compareTo("NE") == 0) {
        GlavniPosluzitelj.info = false;
        return "OK";
      }
    }

    return "ERROR 05: Format poruke mora biti INFO DA|NE";
  }

  /**
   * Vrati status.
   *
   * @return
   */
  private String vratiStatus() {
    return "OK " + String.valueOf(GlavniPosluzitelj.status);
  }

  /**
   * Ugasi server. Postavlja varijablu kraj na true
   *
   * @return string uspjeha
   */
  private String ugasiServer() {
    GlavniPosluzitelj.kraj = true;
    return "OK";
  }

  /**
   * Komanda udaljenost.
   *
   * @param m Matcher od dozvoljenog izraza kako bi se pristupilo grupama
   * @return string uspjeha i izračunata udaljenost
   */
  public String komandaUdaljenost(Matcher m) {
    String[] podaciKoordinate = m.group("podaci").split(" ");
    double broj = izracunajUdaljenost(podaciKoordinate[0], podaciKoordinate[1], podaciKoordinate[2],
        podaciKoordinate[3]);
    GlavniPosluzitelj.brojIzracuna++;
    return "OK " + String.valueOf(broj);
  }

  /**
   * Izracunaj udaljenost.
   *
   * @param gpsSirina1 gps sirina prvog
   * @param gpsDuzina1 gps duzina prvog
   * @param gpsSirina2 gps sirina drugog
   * @param gpsDuzina2 gps duzina drugog
   * @return double Vrijednost udaljenosti
   */
  public double izracunajUdaljenost(String gpsSirina1, String gpsDuzina1, String gpsSirina2,
      String gpsDuzina2) {
    // formula za izračun udaljenosti preuzeta sa stranice
    // https://www.movable-type.co.uk/scripts/latlong.html
    var R = 6371000;
    double gpsSirinaV1 = Math.toRadians(Double.parseDouble(gpsSirina1));
    double gpsSirinaV2 = Math.toRadians(Double.parseDouble(gpsSirina2));

    double deltaGpsSirina =
        Math.toRadians(Double.parseDouble(gpsSirina2) - Double.parseDouble(gpsSirina1));
    double deltaGpsDuzina =
        Math.toRadians(Double.parseDouble(gpsDuzina2) - Double.parseDouble(gpsDuzina1));

    double a = Math.sin(deltaGpsSirina / 2) * Math.sin(deltaGpsSirina / 2) + Math.cos(gpsSirinaV1)
        * Math.cos(gpsSirinaV2) * Math.sin(deltaGpsDuzina / 2) * Math.sin(deltaGpsDuzina / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    // udaljenost u metrima
    double udaljenost = R * c;

    // udaljenost u kilometrima
    udaljenost = udaljenost / 1000;

    return udaljenost;
  }


  /**
   * Prekid.
   */
  @Override
  public void interrupt() {
    super.interrupt();
  }
}
