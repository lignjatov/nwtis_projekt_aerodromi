package org.foi.nwtis.podaci;

import java.sql.Timestamp;

public record Dnevnik(String zahtjev, Timestamp vrijemeZahtjeva, String vrsta) {

}
