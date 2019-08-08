/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.IOException;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public interface Listener {
	
	
	public Response answer(Request request) throws IOException;
	
}
