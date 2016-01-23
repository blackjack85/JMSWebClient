package obe;

import java.io.Serializable;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ManagedBean(name = "Consumer")
@SessionScoped
public class MessageConsumerBean implements MessageListener, Serializable {

    private static final long serialVersionUID = 5671761649767605303L;

    public final static String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    public final static String QUEUE_DESTINATION = "jms/queue/testqueue";
    public final static String JMS_USERNAME = "guest";       //  The role for this user is "guest" in ApplicationRealm
    public final static String JMS_PASSWORD = "guest";
    public final static String PROVIDER_URL = "http-remoting://localhost:8080";
    private final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private boolean quit = false;
    private String msgText;
    private String msgFullText = "";

    public void start() throws Exception {

        Context namingContext = null;
        JMSContext context = null;

        final Properties env = new Properties();

        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
        env.put(Context.SECURITY_PRINCIPAL, "guest");     // username
        env.put(Context.SECURITY_CREDENTIALS, "guest");   // password

        try {
            namingContext = new InitialContext(env);

            // Use JNDI to look up the connection factory and queue
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(CONNECTION_FACTORY);
            // Create a JMS context to use to create consumers
            context = connectionFactory.createContext("guest", "guest"); // again, don't do this in production

            Destination destination = (Destination) namingContext.lookup(QUEUE_DESTINATION);
            // Read a message.  If nothing is there, this will return null
            JMSConsumer consumer = context.createConsumer(destination);

            consumer.setMessageListener(this);
            synchronized (this) {
                while (!this.quit) {
                    try {
                        this.wait();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
            quit = false;
//            msgText = consumer.receiveBody(String.class, 1000);
//            System.out.println("empfangen: "+msgText);
//            if (text != null) {
//                FacesMessage facesMessage
//                        = new FacesMessage("Reading message: " + text);
//                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
//            } else {
//                FacesMessage facesMessage
//                        = new FacesMessage("No message received after 1 second");
//                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
//            }
//                consumer.setMessageListener(this);
        } finally {
            if (namingContext != null) {
                namingContext.close();
            }
            if (context != null) {
                context.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="OnMessage">
    @Override
    public void onMessage(Message msg) {

        try {

            if (msg instanceof TextMessage) {
                msgText = ((TextMessage) msg).getText();
            } else {
                msgText = msg.toString();
            }
            System.out.println("\n<Msg_Receiver> " + msgText);

            if (msgText.equalsIgnoreCase("quit")) {
                synchronized (this) {
                    quit = true;
                    this.notifyAll(); // Notify main thread to quit
                }
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
        msgFullText += msgText + "\\n";
    }

    // </editor-fold>
    public String replace(String s, String alt, String neu) {
        return s.replace(alt, neu);
    }

// <editor-fold defaultstate="collapsed" desc="Properties">
    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }
    // </editor-fold>

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public String getMsgFullText() {
        return msgFullText;
    }

    public void setMsgFullText(String msgFullText) {
        this.msgFullText = msgFullText;
    }
}
