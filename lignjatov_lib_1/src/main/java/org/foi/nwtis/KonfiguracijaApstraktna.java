package org.foi.nwtis;

import java.util.Properties;
import org.foi.nwtis.lignjatov.konfiguracije.KonfiguracijaBin;
import org.foi.nwtis.lignjatov.konfiguracije.KonfiguracijaJson;
import org.foi.nwtis.lignjatov.konfiguracije.KonfiguracijaTxt;
import org.foi.nwtis.lignjatov.konfiguracije.KonfiguracijaXml;
import org.foi.nwtis.lignjatov.konfiguracije.KonfiguracijaYaml;


/**
 * Apstraktna klasa za rad s postavkama iz konfiguracijske datoteke Implementira dio mentoda iz
 * sučelja Konfiguracija.
 */
public abstract class KonfiguracijaApstraktna implements Konfiguracija {

  /** naziv datoteke konfiguracije. */
  protected String nazivDatoteke;

  /** kolekcija postavki. */
  protected Properties postavke;

  /**
   * Konstruktor klase.
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaApstraktna(String nazivDatoteke) {
    this.nazivDatoteke = nazivDatoteke;
    this.postavke = new Properties();
  }

  /**
   * Daj sve postavke.
   *
   * @return Objekt klase Properties
   */
  @Override
  public Properties dajSvePostavke() {
    return this.postavke;
  }

  /**
   * Obriši sve postavke.
   *
   * @return true, ako postoje postavke, inače false
   */
  @Override
  public boolean obrisiSvePostavke() {
    if (this.postavke.isEmpty()) {
      return false;
    } else {
      this.postavke.clear();
      return false;
    }
  }

  /**
   * Daj postavku.
   *
   * @param kljuc Kljuc postavke
   * @return Vrijednost postavke
   */
  @Override
  public String dajPostavku(String kljuc) {
    return this.postavke.getProperty(kljuc);
  }

  /**
   * Spremi postavku.
   *
   * @param kljuc Kljuc postavke
   * @param vrijednost Vrijednost postavke
   * @return true, ako ne postoji kljuc unutar postavki, inače false
   */
  @Override
  public boolean spremiPostavku(String kljuc, String vrijednost) {
    if (this.postavke.containsKey(kljuc)) {
      return false;
    } else {
      this.postavke.setProperty(kljuc, vrijednost);
      return true;
    }
  }

  /**
   * Azuriraj postavku.
   *
   * @param kljuc Kljuc postavke
   * @param vrijednost Vrijednost postavke
   * @return true, ako postoji postavka i postavljena je na vrijednost, inače flase
   */
  @Override
  public boolean azurirajPostavku(String kljuc, String vrijednost) {
    if (!this.postavke.containsKey(kljuc)) {
      return false;
    } else {
      this.postavke.setProperty(kljuc, vrijednost);
      return true;
    }
  }

  /**
   * Postoji postavka.
   *
   * @param kljuc Kljuc
   * @return true, ako postoji postavka u dokumentu, inače false
   */
  @Override
  public boolean postojiPostavka(String kljuc) {
    return this.postavke.containsKey(kljuc);
  }

  /**
   * Obrisi postavku.
   *
   * @param kljuc Kljuc
   * @return true, ako je izbrisana postavka, inače false
   */
  @Override
  public boolean obrisiPostavku(String kljuc) {
    if (!this.postavke.containsKey(kljuc)) {
      return false;
    } else {
      this.postavke.remove(kljuc);
      return true;
    }
  }

  /**
   * Spremi konfiguraciju.
   *
   * @param datoteka the datoteka
   * @throws NeispravnaKonfiguracija ako tip nije podržan ili se javi problem kod spremanja datoteke
   *         konfiguracije
   */
  @Override
  public abstract void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija;

  /**
   * Sprema konfiguraciju.
   *
   * @throws NeispravnaKonfiguracija ako se javi problem kod spremanja datoteke konfiguracije
   */
  @Override
  public abstract void ucitajKonfiguraciju() throws NeispravnaKonfiguracija;

  /**
   * Sprema konfiguraciju pod danim nazivom datoteke.
   *
   * @throws NeispravnaKonfiguracija ako se javi problem kod spremanja datoteke konfiguracije
   */
  @Override
  public void spremiKonfiguraciju() throws NeispravnaKonfiguracija {
    this.spremiKonfiguraciju(this.nazivDatoteke);
  }

  /**
   * Kreira objekt konfiguracije i sprema u datoteku pod zadanim nazivom.
   *
   * @param nazivDatoteke the naziv datoteke
   * @return objekt konfiguracije bez postavki
   * @throws NeispravnaKonfiguracija ako tip konfiguracije nije podržan ili je došlo do pogreške kod
   *         spremanja u datoteku
   */
  public static Konfiguracija kreirajKonfiguraciju(String nazivDatoteke)
      throws NeispravnaKonfiguracija {
    Konfiguracija konfig = dajKonfiguraciju(nazivDatoteke);
    konfig.spremiKonfiguraciju();
    return konfig;
  }

  /**
   * Kreira objekt konfiguraciju i u njega učitava datoteku postavki zadanog naziva.
   *
   * @param nazivDatoteke naziv datoteke
   * @return objekt konfiguracije s postavkama
   * @throws NeispravnaKonfiguracija ako tip konfiguracije nije podržan ili datoteka zadanog naziva
   *         ne postoji ili je došlo do pogreške kod čitanja datoteke
   */
  public static Konfiguracija preuzmiKonfiguraciju(String nazivDatoteke)
      throws NeispravnaKonfiguracija {
    Konfiguracija konfig = dajKonfiguraciju(nazivDatoteke);
    konfig.ucitajKonfiguraciju();
    return konfig;
  }

  /**
   * Vraća objekt konfiguracije.
   *
   * @param nazivDatoteke naziv datoteke
   * @return objekt konfiguracije
   * @throws NeispravnaKonfiguracija ako tip konfiguracije nije podržan
   */
  public static Konfiguracija dajKonfiguraciju(String nazivDatoteke)
      throws NeispravnaKonfiguracija {
    String tip = Konfiguracija.dajTipKonfiguracije(nazivDatoteke);
    return switch (tip) {
      case KonfiguracijaTxt.TIP -> new KonfiguracijaTxt(nazivDatoteke);
      case KonfiguracijaXml.TIP -> new KonfiguracijaXml(nazivDatoteke);
      case KonfiguracijaBin.TIP -> new KonfiguracijaBin(nazivDatoteke);
      case KonfiguracijaJson.TIP -> new KonfiguracijaJson(nazivDatoteke);
      case KonfiguracijaYaml.TIP -> new KonfiguracijaYaml(nazivDatoteke);
      default -> throw new NeispravnaKonfiguracija(
          "Datoteka: '" + nazivDatoteke + "' nema podržani tip datoteke.");
    };
  }

}

