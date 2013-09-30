package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import socketfire.handshake.Header;
import socketfire.handshake.LocationHeader;
import socketfire.handshake.MalformedHeaderException;
import socketfire.handshake.SecureWebSocketKeyHeader;
import socketfire.handshake.SpecialHeaderException;

/**
 *
 * @author cesar
 */
public class Handshake {
	
	private HashMap<String, Header> headers = new HashMap<>();
	
	public Handshake(String s) throws MalformedHeaderException {
		//System.out.println(s);
		String[] headers_received = s.split("\n");
		
		for (String header : headers_received) {
			Header h = null;
			try {
				h = new Header(header);
			} catch (SpecialHeaderException ex) {
				Class search = ex.getCorrectType();
				if (search.equals(LocationHeader.class)) {
					h = new LocationHeader(header);
				}
				else if (search.equals(SecureWebSocketKeyHeader.class)) {
					h = new SecureWebSocketKeyHeader(header);
				}
			}
			
			if (h != null) {
				this.headers.put(h.getKey(), h);
			}
		}
	}
	
	public Header getRequestHeader(String name) {
		if (this.headers.containsKey(name)) {
			return this.headers.get(name);
		}
		else {
			return null;
		}
	}
	
	public String getHeaders() {
		return "HTTP/1.1 101 Switching Protocols\r\n" +
			new Header("Upgrade", "WebSocket") + "\r\n" +
			new Header("Connection", "Upgrade") +"\r\n" +
			new Header("Sec-WebSocket-Accept", ((SecureWebSocketKeyHeader)this.headers.get("Sec-WebSocket-Key")).getAcceptKey()) + "\r\n" +
			"\r\n";
	}
	
}
