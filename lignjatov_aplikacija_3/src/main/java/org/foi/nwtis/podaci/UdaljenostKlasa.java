package org.foi.nwtis.podaci;

public class UdaljenostKlasa {
  private String drzava;
  private float km;

  public UdaljenostKlasa(String drzava, float km) {
    super();
    this.drzava = drzava;
    this.km = km;
  }

  /**
   * @return the drzava
   */
  public String getDrzava() {
    return drzava;
  }

  /**
   * @param drzava the drzava to set
   */
  public void setDrzava(String drzava) {
    this.drzava = drzava;
  }

  /**
   * @return the km
   */
  public float getKm() {
    return km;
  }

  /**
   * @param km the km to set
   */
  public void setKm(float km) {
    this.km = km;
  }


}
