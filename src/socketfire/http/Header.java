/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.http;

import socketfire.handshake.LocationHeader;
import socketfire.handshake.SecureWebSocketKeyHeader;
import socketfire.handshake.SpecialHeaderException;

/**
 *
 * @author cesar
 */
public class Header {
	
	private String name;
	private String value;
	
	public Header() {}
	
	public Header(String name, String value) {
		this.name  = name.trim();
		this.value = value.trim();
	}
	
	public Header(String rawHeader) throws SpecialHeaderException {
		String[] data = rawHeader.split(":");
		
		if (data.length == 1){
			throw new SpecialHeaderException(LocationHeader.class);
		}
		
		if (data[0].equals("Sec-WebSocket-Key")) {
			throw new SpecialHeaderException(SecureWebSocketKeyHeader.class);
		}
		
		this.name  = data[0].trim();
		this.value = data[1].trim();
	}
	
	public String getKey() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}
	
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value.trim();
	}
	
	@Override
	public String toString() {
		return this.name + ":" + this.value;
	}
	
}
