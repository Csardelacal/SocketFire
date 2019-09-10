/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.gateway;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthenticationException;
import org.json.JSONObject;
import socketfire.PropertyLoader;
import socketfire.http.Header;
import socketfire.message.Message;
import socketfire.sso.AppValidator;
import socketfire.webserver.Listener;
import socketfire.webserver.Request;
import socketfire.webserver.Response;
import socketfire.websocket.Server;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class SocketFire implements Listener {
	
	private final Server broadcast;
	
	public SocketFire(Server broadcast) {
		this.broadcast = broadcast;
	}
	
	@Override
	public Response answer(Request request) throws IOException {
		
		
		System.out.println(request.getPath());
		String signature = null;
		
		JSONObject json = request.getBody() != null? new JSONObject(request.getBody()) : new JSONObject();
		
		if (!json.has("signature")) {
			throw new IOException("No signature");
		}
		
		signature = json.getString("signature");
		
		try {
			Properties properties = PropertyLoader.make();

			AppValidator v = new AppValidator(properties.getProperty("socketfire.sso.endpoint"), properties.getProperty("socketfire.sso.app.id"), properties.getProperty("socketfire.sso.app.secret"));
			if (!v.validate(signature)) { throw new AuthenticationException(); } 
		} 
		catch (UnsupportedEncodingException ex) {
			return new Response(new ArrayList<Header>(), json.has("hello")? json.getString("hello") : "Invalid encoding");
		} 
		catch (MalformedURLException ex) {
			Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (AuthenticationException ex) {
			return new Response(new ArrayList<Header>(), json.has("hello")? json.getString("hello") : "Invalid signature");
		}
		
		/*
		 * Send the broadcast to the broadcasting server so that all the websockets
		 * can connect to it and read the appropriate messages.
		 */
		this.broadcast.getChannel(request.getPath()).dispatch(new Message(json.has("body")? json.getString("body") : "", null));
		
		return new Response(new ArrayList<Header>(), json.has("body")? "Queued" : "Skipped empty message");
		
		
		
	}
	
}
