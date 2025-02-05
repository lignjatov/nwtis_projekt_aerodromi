package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class AerodromiLetovi {
  @Getter
  @Setter
  private String Naziv;

  @Getter
  @Setter
  private boolean Aktivan;

  public AerodromiLetovi() {}
}
