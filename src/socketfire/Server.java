package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import socketfire.handshake.MalformedHeaderException;
import socketfire.message.ChannelMessage;
import socketfire.message.Message;
import socketfire.message.STDMessage;
import socketfire.message.ServerMessage;
import socketfire.message.UserMessage;

/**
 *
 * @author cesar
 */
public class Server extends Dispatcher implements Runnable {
	
	private int port = 1337;
	private HashMap<String, Channel> channels = new HashMap<>();
	
	private final EventManager<STDMessage> stdmessageEventManager = new EventManager<>();
	private final EventManager<ChannelMessage> channelmessageEventManager = new EventManager<>();
	private final EventManager<ServerMessage> severmessageEventManager = new EventManager<>();
	private final EventManager<UserMessage> usermessageEventManager = new EventManager<>();

	public Server(int port) {
		this.port = port;
		this.addMessageListener(Message.TYPE_CHAT, new DefaultMessageMirror());
	}
	
	@Override
	public void run() {
		//System.setProperty("javax.net.debug", "ssl");
		
		try {
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket s = ssf.createServerSocket(this.port);
			
			Socket clientsock = null;
			Client client;
		
			while (true) {
				try {
					client = new Client(this, clientsock = s.accept());
					System.out.println("Client connected");
					client.start();
					System.out.println("Client started");
					this.addClient(client);
				} 
				catch (MalformedHeaderException ex) {
					if (clientsock != null && !clientsock.isClosed()) clientsock.close();
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
				} 
				catch (IOException e) {
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Captured IO Exception while initializing the connection", e);
				}
			}
			
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public Channel getChannel(String id) {
		if (this.channels.containsKey(id)) {
			return this.channels.get(id);
		}
		else {
			Channel channel = new Channel(id);
			this.channels.put(id, channel);
			return channel;
		}
	}
	
	/**
	 * 
	 * @deprecated 
	 * @param msg 
	 */
	public void handleMessage(Message msg) {
		switch(msg.getType()) {
			case 0: this.stdmessageEventManager.trigger((STDMessage)msg); break;
			case 2: this.usermessageEventManager.trigger((UserMessage)msg); break;
			case 3: this.channelmessageEventManager.trigger((ChannelMessage)msg); break;
			case 4: this.severmessageEventManager.trigger((ServerMessage)msg); break;
		}
	}
	
	/**
	 * 
	 * @deprecated 
	 * @param type
	 * @param messageEventListener 
	 */
	public void addMessageListener(int type, MessageEventListener messageEventListener) {
		try {
			switch(type) {
				case 0: this.stdmessageEventManager.addEventListener(messageEventListener); break;
				case 2: this.usermessageEventManager.addEventListener(messageEventListener); break;
				case 3: this.channelmessageEventManager.addEventListener(messageEventListener); break;
				case 4: this.severmessageEventManager.addEventListener(messageEventListener); break;
			}
		} catch (Exception e) {
			System.out.println("Could not attach listener - " + e.getMessage());
		}
	}
}
