/**
 * 
 */
package org.foi.nwtis.lignjatov.konfiguracije;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Klasa KonfiguracijaBin za rad s postavkama konfiguracije u bin formatu
 *
 * @author Lucas Ignjatov
 */
public class KonfiguracijaBin extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "bin";

  /**
   * Instancira klasu KonfiguracijaBin
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaBin(String nazivDatoteke) {
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
    if (tip == null || tip.compareTo(KonfiguracijaBin.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nema tip " + KonfiguracijaBin.TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isWritable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + datoteka + "' nije datoteka ili nije moguće upisati u nju");
    }

    try {
      var pisac = new ObjectOutputStream(Files.newOutputStream(putanja));
      pisac.writeObject(this.postavke);
      pisac.close();
      // this.postavke.store(new ObjectOutputStream(Files.newOutputStream(putanja)), datoteka);
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
    if (tip == null || tip.compareTo(KonfiguracijaBin.TIP) != 0) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nema tip " + KonfiguracijaBin.TIP);
    } else if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new NeispravnaKonfiguracija(
          "Datoteka: '" + this.nazivDatoteke + "' nije datoteka ili nije moguće čitati");
    }

    try {
      var reader = new ObjectInputStream(Files.newInputStream(putanja));
      this.postavke = (Properties) reader.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new NeispravnaKonfiguracija("Datoteka: '" + this.nazivDatoteke
          + "' nije datoteka ili nije moguće čitati. " + e.getMessage());
    }
  }
}
