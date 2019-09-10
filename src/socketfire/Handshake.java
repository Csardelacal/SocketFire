package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import socketfire.http.Header;
import socketfire.handshake.SecureWebSocketKeyHeader;

/**
 *
 * @author cesar
 */
public class Handshake {
	
	private String sec;
	
	public Handshake(ArrayList<Header> h) throws IOException {
		
		Header sec = null;
		
		for (int i = 0; i < h.size(); i++) {
			if (h.get(i).getKey().equals("Sec-WebSocket-Key")) {
				sec = h.get(i);
			}
		}
		
		if (sec == null) {
			throw new IOException("Handshake failed");
		}
		
		String key = sec.getValue() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			this.sec = new String(Base64.getEncoder().encode(md.digest(key.getBytes("UTF-8"))));
		}
		catch(NoSuchAlgorithmException | UnsupportedEncodingException ex) {
			throw new IOException(ex);
		}
	}
	
	public String getHeaders() {
		return "HTTP/1.1 101 Switching Protocols\r\n" +
			new Header("Upgrade", "WebSocket") + "\r\n" +
			new Header("Connection", "Upgrade") +"\r\n" +
			new Header("Sec-WebSocket-Accept", sec) + "\r\n" +
			"\r\n";
	}
	
}
