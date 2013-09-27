/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesar
 */
public class Handshake {
	
	private String[] headers;
	
	public Handshake(String s) {
		System.out.println(s);
		this.headers = s.split("\n");
	}
	
	public String getSecWebSocketAccept() {
		int l = this.headers.length;
		for (int i = 0; i < l; i++) {
			String[] info = this.headers[i].split(":");
			if (info.length > 0 && info[0].trim().equals("Sec-WebSocket-Key")) {
				String key = info[1].trim();
				System.out.println("Key: " + key);
				key = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
				try {
					MessageDigest md = MessageDigest.getInstance("SHA-1");
					key = Base64.encode(md.digest(key.getBytes("UTF-8")));
					return key;
				}
				catch(NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(Handshake.class.getName()).log(Level.SEVERE, null, ex);
				} 
			}
		}
		
		return "";
	}
	
	public String getHeaders() {
		return "HTTP/1.1 101 Switching Protocols\r\n" +
			"Upgrade: websocket\r\n" +
			"Connection: Upgrade\r\n" +
			"Sec-WebSocket-Accept: " + this.getSecWebSocketAccept() + "\r\n" +
			//"Sec-WebSocket-Protocol: chat\r\n" +
			"\r\n";
	}
	
}
