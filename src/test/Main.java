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
		
		/*
		 * Initalize the JKS for secure sockets to work.
		 */
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "mytest");
		
		/*
		 * Instance the servers, both web server and websocket server. These are the
		 * core components of socketfire, allowing it to relay messages it received
		 * to the websocket server.
		 * 
		 * Note: Right now, the connection between the two is hard coded into the 
		 * web server. My intention is to separate the and providing the web server
		 * with an "application layer" like CGI that allows the two to be interconnected
		 * without any of them being dependent on the other.
		 */
		Server s = new Server(1337);
		WebServer ws = new WebServer(8080, s);
		
		new Thread(s).start();
		new Thread(ws).start();
	}
	
}
