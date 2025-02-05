package org.foi.nwtis.lignjatov.projekt.ws;

import java.io.IOException;
import java.net.URI;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Klasa WsKlijent.
 */
@ClientEndpoint
public class WsKlijent {

  /** Sesija. */
  Session session = null;


  /**
   * Instancira klijenta
   */
  public WsKlijent() {

    URI a = URI.create("ws://localhost:8080/lignjatov_aplikacija_4/info");
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    try {
      container.connectToServer(this, a);
    } catch (DeploymentException | IOException e) {
      System.out.println("Greška pri spajanju na websocket!");
    }
  }

  /**
   * On open.
   *
   * @param session the session
   */
  @OnOpen
  public void onOpen(Session session) {
    this.session = session;
  }

  /**
   * Kada stigne poruka
   *
   * @param message Poruka
   */
  @OnMessage
  public void onMessage(String message) {}

  /**
   * Šalje poruku
   *
   * @param message Poruka koja se šalje kranjoj točki
   */
  public void sendMessage(String message) {
    try {
      this.session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      System.out.println("Greška pri slanju poruke preko web servisa!");
    }
    try {
      session.close();
    } catch (IOException e) {
      System.out.println("Greška pri zatvaranju sesije!");
    }
  }
}
