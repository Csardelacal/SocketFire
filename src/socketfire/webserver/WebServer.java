/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import socketfire.Server;
import socketfire.handshake.SpecialHeaderException;
import socketfire.message.STDMessage;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class WebServer implements Runnable {
	
	private final int port;
	private final ServerSocket socket;
	private final Server broadcast;

	public WebServer(int port, Server server) throws IOException {
		
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		
		this.port = port;
		this.socket = ssf.createServerSocket(this.port);
		this.broadcast = server;
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
		
		try {
			do { 
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
				request.handle().send(client);
				client.close();
				
				/*
				 * Send the broadcast to the broadcasting server so that all the websockets
				 * can connect to it and read the appropriate messages.
				 */
				this.broadcast.getChannel(request.getPath()).dispatch(new STDMessage(null, request.getBody()));
			} while (true);
		} 
		catch (IOException ex) {
			Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SpecialHeaderException ex) {
			Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	
}
