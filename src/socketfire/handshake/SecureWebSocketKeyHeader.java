/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.handshake;

import socketfire.http.Header;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.Handshake;

/**
 *
 * @author cesar
 */
public class SecureWebSocketKeyHeader extends Header{

	public SecureWebSocketKeyHeader(String key, String value) {
		this.setName(key);
		this.setValue(value);
	}

	public SecureWebSocketKeyHeader(String rawHeader) {
		String[] data = rawHeader.split(":");
		
		this.setName(data[0]);
		this.setValue(data[1]);
	}
	
	public String getAcceptKey() {
		String key = this.getValue() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			key = new String(Base64.getEncoder().encode(md.digest(key.getBytes("UTF-8"))));
			return key;
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Handshake.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
}
