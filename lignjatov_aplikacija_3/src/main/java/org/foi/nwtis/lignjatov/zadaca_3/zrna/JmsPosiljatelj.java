package org.foi.nwtis.lignjatov.zadaca_3.zrna;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

/**
 * The Class JmsPosiljatelj.
 */
@Stateless
public class JmsPosiljatelj {

  /** Brojac poruka. */
  static int brojacPoruka = 0;

  /** Tvornica poveznica */
  @Resource(mappedName = "jms/nwtis_qf_lignjatov")
  private ConnectionFactory connectionFactory;

  /** Red čekanja */
  @Resource(mappedName = "jms/NWTiS_lignjatov")
  private Queue queue;

  /**
   * Salji poruku.
   *
   * @param tekstPoruke tekst poruke
   * @return true ako uspješno pošalje poruku, inače false
   */
  public boolean saljiPoruku(String tekstPoruke) {
    boolean status = true;
    try {
      Connection connection = connectionFactory.createConnection();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer messageProducer = session.createProducer(queue);
      TextMessage message = session.createTextMessage();

      String poruka = tekstPoruke + " Ovo je poruka broj:" + JmsPosiljatelj.brojacPoruka++;

      message.setText(poruka);
      messageProducer.send(message);
      messageProducer.close();
      connection.close();
    } catch (JMSException ex) {
      System.out.println(ex.getMessage());
      status = false;
    }
    return status;
  }
}
