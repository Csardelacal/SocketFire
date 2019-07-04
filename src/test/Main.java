/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.Server;
import socketfire.sso.AppValidator;
import socketfire.webserver.WebServer;

/**
 *
 * @author cesar
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		/*
		 * For the application to work, we need to load in a few basic settings that
		 * will allow the application to run without having to constantly reload
		 */
		Properties properties = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream("app.config");
		} 
		catch (FileNotFoundException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		try {
			properties.load(input);
		} 
		catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		
		try {
			AppValidator v = new AppValidator(properties.getProperty("socketfire.sso.endpoint"), properties.getProperty("socketfire.sso.app.id"), properties.getProperty("socketfire.sso.app.secret"));
			System.out.println(v.validate("sha512%3A1638362706%3A390035134%3A1529947125%3AB%2BsELaaKUOiT%2B0a2A%2FRXM4BZ65%2BK4hdai7%2BhHeAn5S4%2FL8Qj7y%3A8142e6ac0efc299d7cc493c7dccc2ecf405606b05783219257ecc83b7a512726ad89d57b81c492e8715b24d3c72bbfb37eec8a842a2370631ff0e1029fc92392"));
			
		} catch (NoSuchAlgorithmException | MalformedURLException | UnsupportedEncodingException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		} 
		
		
		Server s = new Server(1337);
		new Thread(s).start();
		
		WebServer ws = new WebServer(8080, s);
		new Thread(ws).start();
	}
	
}
