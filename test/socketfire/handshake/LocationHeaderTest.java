/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.handshake;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cesar
 */
public class LocationHeaderTest {
	
	public LocationHeaderTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of getChannel method, of class LocationHeader.
	 */
	@Test
	public void testGetChannel() {
		System.out.println("getChannel");
		LocationHeader instance;
		
		//Test with well-formed header
		String expResult = "channel";
		try {
			instance = new LocationHeader("GET /channel HTTP/1.1");
			String result = instance.getChannel();
			assertEquals(expResult, result);
		} catch (MalformedHeaderException ex) {
			fail("Wellformed header was not accepted");
		}
		
		//Test with purposely malformed header
		try {
			instance = new LocationHeader("GhjET /channel HTTP/1.1");
			fail("Malformed header was accepted");
		} catch (MalformedHeaderException ex) {
		}
	}
	
}
