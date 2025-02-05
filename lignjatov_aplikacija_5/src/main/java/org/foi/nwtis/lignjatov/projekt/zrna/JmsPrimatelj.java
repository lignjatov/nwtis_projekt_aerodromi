package org.foi.nwtis.lignjatov.projekt.zrna;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/**
 * Klasa JmsPrimatelj.
 */
@MessageDriven(mappedName = "jms/NWTiS_lignjatov",
    activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue")})
public class JmsPrimatelj implements MessageListener {

  /** Sakupljac poruka */
  @Inject
  SakupljacJmsPoruka sakupljac;

  /**
   * Kada poruka stigne
   *
   * @param message Message
   */
  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        var msg = (TextMessage) message;
        System.out.println(msg.getText());
        sakupljac.spremiPoruku(msg.getText());
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

}
