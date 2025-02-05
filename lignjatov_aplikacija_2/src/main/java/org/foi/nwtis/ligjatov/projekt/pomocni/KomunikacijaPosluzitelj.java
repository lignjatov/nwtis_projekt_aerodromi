package org.foi.nwtis.ligjatov.projekt.pomocni;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Lokacija;


/**
 * Klasa KomunikacijaPosluzitelj.
 */
public class KomunikacijaPosluzitelj {

  /** adresa. */
  public static String adresa;

  /** mrezna vrata. */
  public static int mreznaVrata;

  /** postavke. */
  public static PostavkeBazaPodataka postavke;

  /**
   * Posalji zahtjev.
   *
   * @param porukaZahtjeva Komanda koja se šalje poslužitelju
   * @return string Odgovor poslužitelja
   */
  public static String posaljiZahtjev(String porukaZahtjeva) {
    var poruka = new StringBuilder();
    try {
      var mreznaUticnica = new Socket(adresa, mreznaVrata);

      // mreznaUticnica.setSoTimeout(Integer.parseInt(this.argumenti.get("cekanje")));
      try {
        var citac = new BufferedReader(
            new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
        var pisac = new BufferedWriter(
            new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

        pisac.write(porukaZahtjeva);
        pisac.flush();
        mreznaUticnica.shutdownOutput();
        while (true) {
          var redak = citac.readLine();
          if (redak == null) {
            break;
          }
          poruka.append(redak);
        }

        mreznaUticnica.shutdownInput();
        mreznaUticnica.close();
      } catch (IOException e) {
        Logger.getGlobal().log(Level.SEVERE, "Vrijeme čekanja dosegnuto");
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
    return poruka.toString();
  }

  /**
   * Slozi zahtjev udaljenost.
   *
   * @param koordinateOd koordinate od aerodroma
   * @param koordinateDo koordinate do aerodroma
   * @return string koji paše poslužitelju aplikacija_1
   */
  public static String sloziZahtjevUdaljenost(Lokacija koordinateOd, Lokacija koordinateDo) {
    String zahtjev = "UDALJENOST ";
    zahtjev += koordinateOd.getLongitude() + " " + koordinateOd.getLatitude() + " ";
    zahtjev += koordinateDo.getLongitude() + " " + koordinateDo.getLatitude();
    return zahtjev;
  }
}
