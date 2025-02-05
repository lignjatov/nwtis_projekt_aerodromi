package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Korisnik {

  @Getter
  @Setter
  private String KorisnickoIme;

  @Getter
  @Setter
  private String Lozinka;

  @Getter
  @Setter
  private String Ime;

  @Getter
  @Setter
  private String Prezime;

  @Getter
  @Setter
  private String Email;

  public Korisnik() {

  }
}
