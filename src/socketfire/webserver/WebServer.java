/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import socketfire.handshake.SpecialHeaderException;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class WebServer implements Runnable {
	
	private final int port;
	private final ServerSocket socket;
	
	private final ArrayList<Listener> listeners = new ArrayList<>();

	public WebServer(int port) throws IOException {
		
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		
		this.port = port;
		this.socket = ssf.createServerSocket(this.port);
	}
	
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void run() {
		/*
		 * First of all we ninitialize the socket. And start accepting connections,
		 * please note that the webserver is the part of this application that is 
		 * only used by the applications sending data through the server
		 */
		Socket client;
		RequestFactory rf = new RequestFactory();
		
		do { 
			try {
				/*
				 * First we deal with the incoming connection and reply to it, so the
				 * other server can continue working while we deal with the broadcasting
				 * of the message.
				 */
				client = this.socket.accept();
				
				/*
				 * Receive and handle the connection from the server (remember, this
				 * will always be an application server), and once we have reviewed
				 * their request, we sent them an acknoledgment and close the connection.
				 */
				Request request = rf.receive(client);
				
				for (int i = 0; i < this.listeners.size(); i++) {
					try {
						this.listeners.get(i).answer(request).send(client);
						client.close();
						break;
					}
					catch (IOException ex) { /*Fine exception*/ }
				}
			}
			catch (IOException ex) {
				Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SpecialHeaderException ex) {
				Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}  while (true);
	}
	
	
}
