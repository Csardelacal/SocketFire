/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import socketfire.Client;

/**
 *
 * @author cesar
 */
public class AuthMessage extends Message {

	public AuthMessage(Client client, Object payload) {
		super(1, payload, client);
	}
	
}
