/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.gateway;

import java.io.IOException;
import java.util.ArrayList;
import socketfire.http.Header;
import socketfire.webserver.Listener;
import socketfire.webserver.Request;
import socketfire.webserver.Response;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class Standard implements Listener {

	
	@Override
	public Response answer(Request request) throws IOException {
		
		ArrayList<Header> al = new ArrayList();
		al.add(new Header("X-Powered-By", "Socketfire"));
		
		return new Response(al, "<h1>It works</h1><p>But this endpoint is giving you no results</p>");
		
	}
	
}
