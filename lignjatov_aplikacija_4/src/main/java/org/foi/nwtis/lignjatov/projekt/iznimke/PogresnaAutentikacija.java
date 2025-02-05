package org.foi.nwtis.lignjatov.projekt.iznimke;

/**
 * Klasa PogresnaAutentikacija.
 */
public class PogresnaAutentikacija extends Exception {

  /** Serijski kod verzije */
  private static final long serialVersionUID = -9139950015773032489L;

  /**
   * Instanciranje nove iznimke
   */
  public PogresnaAutentikacija() {

  }

  /**
   * Instanciranje nove iznimke uz poruku
   *
   * @param msg poruka
   */
  public PogresnaAutentikacija(String msg) {
    super(msg);
  }
}
