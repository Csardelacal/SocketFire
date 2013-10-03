/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.handshake;

import java.util.regex.Pattern;

/**
 *
 * @author cesar
 */
public class LocationHeader extends Header{
	
	private String channel;

	public LocationHeader(String rawHeader) throws MalformedHeaderException {
		String[] data = rawHeader.split(" ");
		
		if (!data[0].equals("GET")) {
			throw new MalformedHeaderException("Expected get header, received " + data[0]);
		}
		
		if (!data[2].matches("HTTP/1.[0-1]")) {
			throw new MalformedHeaderException("Expected get header, received " + data[0]);
		}
		
		String[] location = data[1].split("/");
		if (location.length > 1) {
			this.channel = location[1];
		}
		else throw new MalformedHeaderException("Invalid channel name");
		
	}

	public String getChannel() {
		return this.channel;
	}
	
	@Override
	public String getValue() {
		return this.channel;
	}
	
	@Override
	public String getKey() {
		return "_location";
	}
}
