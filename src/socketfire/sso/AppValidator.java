/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.sso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class AppValidator 
{
	
	private String url;
	private String appId;
	private String appSecret;

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();
	
	public AppValidator(String url, String appId, String appSecret) {
		this.url = url;
		this.appId = appId;
		this.appSecret = appSecret;
	}


	String randomString( int len ){
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();
	}


	
	public String makeSignature() throws NoSuchAlgorithmException {
		long timestamp    = (long) (System.currentTimeMillis() / 1000 + 600);
		
		String salt      = this.randomString(20);
		String signature = appId + "." + appSecret + "." + timestamp + "." + salt;
		
		System.out.println(timestamp);
		
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] bytes = md.digest(signature.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i< bytes.length ;i++){
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return "sha512:" + appId + ":" + timestamp + ":" + salt + ":" + sb.toString();
	}
	
	public boolean validate(String remoteSignature) throws NoSuchAlgorithmException, MalformedURLException, UnsupportedEncodingException {
		String urlv = this.url + "/auth/app.json?signature=" + URLEncoder.encode(this.makeSignature(), "UTF-8") + "&remote=" + remoteSignature;
		System.out.println(urlv);
		URL request = new URL(urlv);
		String response = "";
		
		
		try {
			HttpURLConnection connection = (HttpURLConnection)request.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			int code = connection.getResponseCode();
			
			if (code != 200) {
				System.out.println("Code received: " + code);
				return false;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			
			for (String line; (line = reader.readLine()) != null;) {
				 response = response + line;
			}
			
			System.out.println(response);
			JSONObject data = new JSONObject(response);
			return (boolean)data.getBoolean("authenticated");
		} 
		catch (ProtocolException ex) {
			Logger.getLogger(AppValidator.class.getName()).log(Level.SEVERE, null, ex);
		} 
		catch (IOException ex) {
			Logger.getLogger(AppValidator.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return false;

	}
}
