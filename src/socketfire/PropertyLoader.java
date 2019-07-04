/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire;

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
import socketfire.sso.AppValidator;
import test.Main;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class PropertyLoader {
	
	public static Properties make() {
		
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
		}
		
		return properties;
	}
}
