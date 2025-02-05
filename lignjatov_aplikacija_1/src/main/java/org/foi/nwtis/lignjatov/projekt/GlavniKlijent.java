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

public class GlavniKlijent {

  public static void main(String[] args) {
    var glavniKlijent = new GlavniKlijent();
    if (!glavniKlijent.provjeriArgumente(args)) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Nije upisana adresa ili port");
    }
    glavniKlijent.spojiSeNaPosluzitelj(args[0], Integer.parseInt(args[1]), args[2]);
  }

  boolean provjeriArgumente(String[] args) {
    if (args.length == 3) {
      return true;
    }
    return false;
  }

  public void spojiSeNaPosluzitelj(String adresa, int mreznaVrata, String komanda) {
    try {
      var mreznaUticnica = new Socket(adresa, mreznaVrata);

      try {
        var citac = new BufferedReader(
            new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
        var pisac = new BufferedWriter(
            new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
        var poruka = new StringBuilder();

        pisac.write(komanda);
        pisac.flush();
        mreznaUticnica.shutdownOutput();
        while (true) {
          var redak = citac.readLine();
          if (redak == null) {
            break;
          }
          Logger.getGlobal().log(Level.INFO, redak);
          poruka.append(redak);
        }
        mreznaUticnica.shutdownInput();
        mreznaUticnica.close();
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    } catch (IOException e) {
    }
  }
}
