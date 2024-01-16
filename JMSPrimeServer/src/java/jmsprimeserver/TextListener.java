package jmsprimeserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import utils.Helper;

public class TextListener implements MessageListener {

    private MessageProducer replyProducer;
    private Session session;

    public TextListener(Session session) {

        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            String text = msg.getText();

            if (!Helper.isValidateInput(text)) {
                TextMessage response = session.createTextMessage(String.format("Your input %s is invalid", text));
                response.setJMSCorrelationID(message.getJMSCorrelationID());
                System.out.println("Sending message: " + response.getText());
                replyProducer.send(message.getJMSReplyTo(), response);
                return;
            }

            String[] texts = text.split(",");
            int startNum = Integer.parseInt(texts[0]);
            int endNum = Integer.parseInt(texts[1]);
            int answer = Helper.numberOfPrimeInRange(startNum, endNum);

            TextMessage response = session.createTextMessage(String.format("The number of primes between %d and %d is %d", startNum, endNum, answer));
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            System.out.println("sending message " + response.getText());
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
    }

}
