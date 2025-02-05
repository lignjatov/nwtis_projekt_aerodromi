/**
 * 
 */
package org.foi.nwtis.lignjatov.konfiguracije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;


/**
 * TKlasa KonfiguracijaTxt za rad s postavkama konfiguracije u txt formatu.
 *
 * @author Lucas Ignjatov
 */
public class KonfiguracijaTxt extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "txt";

  /**
   * Instancira klasu KonfiguracijaTxt.
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaTxt(String nazivDatoteke) {
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
    if (tip == null || tip.compareTo(KonfiguracijaTxt.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nema tip " + KonfiguracijaTxt.TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isWritable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nije datoteka ili nije moguće upisati u nju");
    }

    try {
      this.postavke.store(Files.newOutputStream(putanja), "NWTiS_lignjatov_2023.");
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
    if (tip == null || tip.compareTo(KonfiguracijaTxt.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nema tip " + KonfiguracijaTxt.TIP);
    } else if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nije datoteka ili nije moguće čitati");
    }

    try {
      this.postavke.load(Files.newInputStream(putanja));
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija("Datoteka: '" + this.nazivDatoteke
          + "' nije datoteka ili nije moguće čitati. " + e.getMessage());
    }
  }

}
