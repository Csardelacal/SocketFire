/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import socketfire.PropertyLoader;
import socketfire.http.Header;
import socketfire.sso.AppValidator;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class Request {
	
	private final String path;
	private final ArrayList<Header> headers;
	private final String body;
	
	public Request(String path, ArrayList<Header> headers, String body) {
		this.path = path;
		this.headers = headers;
		this.body = body;
	}
	
	public Response handle() {
		System.out.println(this.path);
		String signature = null;
		
		for (int i = 0; i < this.headers.size(); i++) {
			
			System.out.println(this.headers.get(i).getKey() + ":" + this.headers.get(i).getValue());
		}
		
		JSONObject json = this.body != null? new JSONObject(this.body) : new JSONObject();
		signature = json.getString("signature");
		
		if (signature == null) {
			System.out.println("Signature was not received");
		}
		else {
			try {
				Properties properties = PropertyLoader.make();
				
				AppValidator v = new AppValidator(properties.getProperty("socketfire.sso.endpoint"), properties.getProperty("socketfire.sso.app.id"), properties.getProperty("socketfire.sso.app.secret"));
				System.out.println(v.validate(signature));
			} 
			catch (UnsupportedEncodingException ex) {
				Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MalformedURLException ex) {
				Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		}
		
		return new Response(new ArrayList<Header>(), json.has("hello")? json.getString("hello") : "Nothing received");
	}

	public String getBody() {
		return this.body;
	}
	
	public String getPath() {
		return this.path;
	}
}
