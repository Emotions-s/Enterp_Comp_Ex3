package jmsprimeclient;

import java.io.IOException;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Main {

    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue queue;

    public static void main(String[] args) throws IOException {
        Connection connection = null;
        Session session = null;
        TextListener listener = null;
        TextMessage message = null;
        
        Scanner in = null;
        String text;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            listener = new TextListener();

            Queue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(listener);
            MessageProducer producer = session.createProducer(queue);
            
            connection.start();
            System.out.println("Enter two numbers. Use ',' to seperate each number. To end the program press enter.");
            in = new Scanner(System.in);
            
            while (true) {
                System.out.print("Enter: ");
                text = in.nextLine();
                if (text.equals("")) {
                    break;
                }
                System.out.print("Sending message: ");
                System.out.println(text);
                
                message = session.createTextMessage();
                message.setText(text);
                message.setJMSReplyTo(tempDest);
                message.setJMSCorrelationID("9483");
                producer.send(message);
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

}
