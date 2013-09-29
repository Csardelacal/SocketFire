/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.handshake;

/**
 *
 * @author cesar
 */
public class MalformedHeaderException extends Exception {
	
	public MalformedHeaderException(String error) {
		super(error);
	}
	
}
