package obe;

import java.io.Serializable;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


@ManagedBean( name = "Consumer")
@SessionScoped
public class MessageConsumerBean implements Serializable{
    
    private static final long serialVersionUID = 5671761649767605303L;
    
    public final static String CONNECTION_FACTORY="jms/RemoteConnectionFactory";
    public final static String QUEUE_DESTINATION="jms/queue/testqueue";
    public final static String JMS_USERNAME="guest";       //  The role for this user is "guest" in ApplicationRealm
    public final static String JMS_PASSWORD="guest";
    public final static String PROVIDER_URL="http-remoting://localhost:8080";
    private final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    
    private String msgText;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

 

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }
    
    public void start() throws Exception
    {
        Context namingContext = null;
        JMSContext context = null;
        
        // Set up the namingContext for the JNDI lookup
        // Make sure you create an application user in Wildfly that matches the 
        // username and password below.  Usually a bad practice to have passwords
        // in code, but this is just a simple example.
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
	        text = consumer.receiveBodyNoWait(String.class);
	        System.out.println("Received message....: " + text );
        } finally {
        	if (namingContext != null) {
        		namingContext.close();
        	}
        	if (context != null) {
        		context.close();
        	}
        }
    }

  
}