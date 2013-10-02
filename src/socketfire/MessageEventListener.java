/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import socketfire.message.Message;

/**
 *
 * @author cesar
 */
public abstract class MessageEventListener<E extends Message> {
	
	public abstract E onMessage(E message);
	
}
