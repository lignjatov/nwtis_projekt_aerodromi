/**
 * 
 */
package org.foi.nwtis.lignjatov.konfiguracije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import com.google.gson.Gson;

//
/**
 * Klasa KonfiguracijaJson za rad s postavkama konfiguracije u json formatu
 *
 * @author Lucas Ignjatov
 */
public class KonfiguracijaJson extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "json";

  /**
   * Instancira klasu KonfiguracijaJson.
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaJson(String nazivDatoteke) {
    super(nazivDatoteke);
    //
  }

  /**
   * Spremi konfiguraciju.
   *
   * @param datoteka Datoteka
   * @throws NeispravnaKonfiguracija ako nije ispravna konfiguracija
   */
  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);
    if (tip == null || tip.compareTo(KonfiguracijaJson.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nema tip " + KonfiguracijaJson.TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isWritable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nije datoteka ili nije moguće upisati u nju");
    }

    try {
      var gson = new Gson();
      var e = Files.newBufferedWriter(putanja);
      var i = gson.toJson(this.postavke);
      Logger.getGlobal().log(Level.INFO, i);
      e.write(i);
      e.close();


    } catch (IOException e) {
      throw new NeispravnaKonfiguracija("Datoteka: '" + datoteka
          + "' nije datoteka ili nije moguće upisati u nju. " + e.getMessage());
    }
  }

  /**
   * Ucitaj konfiguraciju.
   *
   * @throws NeispravnaKonfiguracija ako nije ispravna konfiguracija
   */
  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    var putanja = Path.of(this.nazivDatoteke);
    var tip = Konfiguracija.dajTipKonfiguracije(this.nazivDatoteke);
    if (tip == null || tip.compareTo(KonfiguracijaJson.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nema tip " + KonfiguracijaJson.TIP);
    } else if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nije datoteka ili nije moguće čitati");
    }

    try {
      Gson gson = new Gson();
      var citac = Files.newBufferedReader(putanja);
      this.postavke = gson.fromJson(citac, this.postavke.getClass());
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija("Datoteka: '" + this.nazivDatoteke
          + "' nije datoteka ili nije moguće čitati. " + e.getMessage());
    }
  }

}
