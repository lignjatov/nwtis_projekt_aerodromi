package org.foi.nwtis.lignjatov.projekt.ws;

import java.io.IOException;
import java.sql.Timestamp;
import jakarta.annotation.Resource;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Klasa WsInfo.
 */
@ServerEndpoint(value = "/info")
public class WsInfo {

  /** Izvor podataka */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /** broj korisnika. */
  public static int brojKorisnika;

  /** broj aktivnih aerodroma. */
  public static int brojAktivnihAerodroma;

  /**
   * U trenutku uspostave veze sa korisnikom
   *
   * @param session sesija
   */
  @OnOpen
  public void onOpen(Session session) {}

  /**
   * Pri dobivenoj poruci gleda se šalje li se poruka svmia ili pojedincu
   *
   * @param session sesija
   * @param msg poruka
   */
  @OnMessage
  public void onMessage(Session session, String msg) {
    System.out.println(msg);
    String povratnaPoruka = "";
    // pošalji samo jednom korisniku
    if (msg.contains("dohvati")) {
      var trenutnoVrijeme = new Timestamp(System.currentTimeMillis());
      povratnaPoruka = trenutnoVrijeme + ";" + brojKorisnika + ";" + brojAktivnihAerodroma;
      try {
        session.getBasicRemote().sendText(povratnaPoruka);
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }

    // pošalji svim korisnicima
    if (msg.contains("posalji")) {
      for (Session sess : session.getOpenSessions()) {
        if (sess.isOpen()) {
          try {
            var trenutnoVrijeme = new Timestamp(System.currentTimeMillis());
            povratnaPoruka = trenutnoVrijeme + ";" + brojKorisnika + ";" + brojAktivnihAerodroma;
            sess.getBasicRemote().sendText(povratnaPoruka);
          } catch (IOException e) {
            System.out.println(e.getMessage());
          }
        }
      }
    }
  }
}
