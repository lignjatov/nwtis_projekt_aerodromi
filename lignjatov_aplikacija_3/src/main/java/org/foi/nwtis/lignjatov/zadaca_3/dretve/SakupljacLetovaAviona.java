package org.foi.nwtis.lignjatov.zadaca_3.dretve;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.foi.nwtis.PostavkeBazaPodataka;
import org.foi.nwtis.lignjatov.zadaca_3.zrna.JmsPosiljatelj;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijentBP;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.servlet.ServletContext;

/**
 * The Class SakupljacLetovaAviona.
 */
public class SakupljacLetovaAviona extends Thread {

  /** Korisnicko ime za OpenSkyNetwork */
  private String korisnickoIme;

  /** Lozinka za OpenSkyNetwork */
  private String lozinka;

  /** Aerodromi za sakupljanje */
  private String aerodromiSakupljanje;

  /** Broj sekundi ciklusa */
  int ciklus;

  /** Provjera prekida */
  boolean prekid = false;

  /** Lista letova */
  private List<LetAviona> letovi;

  /** Od datuma mjerenja */
  private String odDatuma;

  /** Do datuma mjerenja */
  private String doDatuma;

  /** Posiljatelj JMS poruka */
  private JmsPosiljatelj posiljatelj;

  /** DataSource za bazu podataka */
  private DataSource ds;



  // Razlog za implementaciju JMS posiljatelja ovako:
  // U slučaju da inject napravimo na thread, javlja da je jmsPosiljatelj prazan
  // Umjesto toga ga injectamo u ContextListener klasu te ga proslijedimo kao referencu dretvi
  /**
   * Instantiates a new sakupljac letova aviona.
   *
   * @param posiljatelj Posiljatelj JMS poruka
   * @param sce Kontekst aplikacije
   * @param ds DataSource za bazu podataka
   */
  // Time je omogućen da se u točnom kontekstu koristi JmsPosiljatelj
  public SakupljacLetovaAviona(JmsPosiljatelj posiljatelj, ServletContext sce, DataSource ds) {
    PostavkeBazaPodataka postavke = (PostavkeBazaPodataka) sce.getAttribute("konfig");
    ciklus = Integer.parseInt(postavke.dajPostavku("ciklus.trajanje"));
    // korisnickoIme = postavke.dajPostavku("OpenSkyNetwork.korisnik");
    // lozinka = postavke.dajPostavku("OpenSkyNetwork.lozinka");
    korisnickoIme = postavke.dajPostavku("OpenSkyNetworkBP.korisnik");
    lozinka = postavke.dajPostavku("OpenSkyNetworkBP.lozinka");
    odDatuma = postavke.dajPostavku("preuzimanje.od");
    doDatuma = postavke.dajPostavku("preuzimanje.do");
    this.posiljatelj = posiljatelj;
    letovi = new ArrayList<LetAviona>();
    this.ds = ds;
  }

  /**
   * Početak dretve
   */
  @Override
  public synchronized void start() {
    super.start();
  }

  /**
   * Rad dretve
   */
  @Override
  public void run() {
    // Inicijalizacija
    var formatter = new SimpleDateFormat("dd.MM.yyyy");
    Date preuzimanjeOd = null;
    Date preuzimanjeDo = null;
    // OSKlijent os = new OSKlijent(korisnickoIme, lozinka);
    OSKlijentBP os = new OSKlijentBP(korisnickoIme, lozinka);
    long vrijeme = 0;
    long vrijemeDo = 0;
    long krajDana = 0;
    int brojLetova = 0;
    // String[] aerodromi = aerodromiSakupljanje.split(" ");
    var aerodromi = new ArrayList<String>();



    // pronalaženje početnog datuma
    try {
      preuzimanjeOd = formatter.parse(odDatuma);
      preuzimanjeDo = formatter.parse(doDatuma);
    } catch (ParseException e1) {
      System.out.println(e1.getMessage());
    }



    vrijeme = vratiPocetnoVrijeme(preuzimanjeOd, preuzimanjeDo, formatter);

    // Oduzimam sa 1 prema uputama sa foruma koji kaže da interval vremena mora biti kraći od 24
    // sata. 86400 određuje broj skeundi u danu
    krajDana = vrijeme + 86400;
    vrijemeDo = preuzimanjeDo.getTime() / 1000;


    // U CIKLUSU ODAVDE
    // manje ili jednako postavljeno u slučaju da je vrijeme u danu različito od 00:00:00
    while (!prekid && vrijeme <= vrijemeDo) {
      aerodromi = dohvatiAerodrome();
      brojLetova = 0;
      for (var aerodrom : aerodromi) {
        try {
          if (!prekid) {
            letovi = os.getDepartures(aerodrom, vrijeme, krajDana);
            spremiLetove(letovi, new Date(vrijeme * 1000));
            brojLetova += letovi.size();
            System.out.println(aerodrom + ":" + letovi.size());
          } else {
            break;
          }

        } catch (NwtisRestIznimka e) {
          System.out.println(e.getMessage());
        }
      }
      if (!prekid) {
        posiljatelj.saljiPoruku("Na dan: " + formatter.format(new Date(vrijeme * 1000))
            + " preuzeto ukupno " + brojLetova + " letova aviona");
      }

      // sljedeći dan
      vrijeme += 86400;
      krajDana += 86400;

      try {
        Thread.sleep(ciklus * 1000);
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
    }
    // KRAJ CIKLUSA


    System.out.println("Normalni kraj rada dretve");
    super.run();
  }


  private ArrayList<String> dohvatiAerodrome() {
    PreparedStatement pstmt = null;
    ArrayList<String> letovi = new ArrayList<String>();
    String query = "SELECT NAZIV FROM PUBLIC.AERODROMI_LETOVI WHERE AKTIVAN=1;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        letovi.add(rs.getString("NAZIV"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return letovi;
  }

  /**
   * Prekid dretve
   */
  @Override
  public void interrupt() {
    prekid = true;
    System.out.println("Kraj rada dretve");
    super.interrupt();
  }

  /**
   * Dohvati zadnji unos unutar baze podataka
   *
   * @return datum Datum zadnjeg unosa u bazu podataka
   */
  public Date dohvatiZadnjiUnos() {
    PreparedStatement pstmt = null;
    Timestamp datum = null;
    String query = "SELECT * FROM PUBLIC.LETOVI_POLASCI ORDER BY ID DESC LIMIT 1;";
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        datum = rs.getTimestamp("STORED");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }

    return datum;
  }

  /**
   * Vrati pocetno vrijeme od kojega mora početi postavljati upite
   *
   * @param preuzimanjeOd Preuzimanje od - Datum zapisan u konfiguracijskoj datoteci
   * @param preuzimanjeDo Preuzimanje do - Datum zapisan u konfiguracijskoj datoteci
   * @param format Format datuma
   * @return vrijeme Početno vrijeme od kojega se gleda
   */
  public long vratiPocetnoVrijeme(Date preuzimanjeOd, Date preuzimanjeDo, SimpleDateFormat format) {
    long vrijeme = 0;
    Date datumZadnjegUnosa;
    try {
      datumZadnjegUnosa = dohvatiZadnjiUnos();
      if (datumZadnjegUnosa != null) {
        if (datumZadnjegUnosa.compareTo(preuzimanjeOd) > 0) {
          // Radi se kako bi datumZadnjegUnosa poprimio oblik dd..yyyy bez vremena
          String datumZadnjegUnosaFormatirani = format.format(datumZadnjegUnosa);
          preuzimanjeOd = format.parse(datumZadnjegUnosaFormatirani);
          vrijeme = preuzimanjeOd.getTime() / 1000;
          vrijeme += 86400;
        } else {
          vrijeme = preuzimanjeOd.getTime() / 1000;
        }
      } else {
        vrijeme = preuzimanjeOd.getTime() / 1000;
      }


    } catch (ParseException e1) {
      System.out.println(e1.getMessage());
    }
    return vrijeme;
  }

  /**
   * Spremi letove u bazu podataka
   *
   * @param listaLetova Lista letova
   * @param datumSlanja Datum slanja
   */
  public void spremiLetove(List<LetAviona> listaLetova, Date datumSlanja) {
    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {

      for (LetAviona let : listaLetova) {
        // if (let.getEstArrivalAirport() == null) {
        // continue;
        // }
        String datumFormatiran = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(datumSlanja);
        String query =
            "INSERT INTO LETOVI_POLASCI(ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, ";
        query +=
            "ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, ESTDEPARTUREAIRPORTVERTDISTANCE, ";
        query += "ESTARRIVALAIRPORTHORIZDISTANCE, ESTARRIVALAIRPORTVERTDISTANCE, ";
        query += "DEPARTUREAIRPORTCANDIDATESCOUNT, ARRIVALAIRPORTCANDIDATESCOUNT, STORED) "
            + "VALUES ('" + let.getIcao24() + "', ";
        query += let.getFirstSeen() + ",'" + let.getEstDepartureAirport() + "', "
            + let.getLastSeen() + ", '" + let.getEstArrivalAirport() + "', ";
        query += " '" + let.getCallsign() + "', " + let.getEstDepartureAirportHorizDistance() + ", "
            + let.getEstDepartureAirportVertDistance() + ", ";
        query += let.getEstArrivalAirportHorizDistance() + ", "
            + let.getEstArrivalAirportVertDistance() + ", ";
        query += let.getDepartureAirportCandidatesCount() + ", "
            + let.getArrivalAirportCandidatesCount() + ", ";
        query += "'" + datumFormatiran + "'); ";

        pstmt = con.prepareStatement(query);
        pstmt.executeUpdate();
        pstmt.close();
      }
      con.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed()) {
          pstmt.close();
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
