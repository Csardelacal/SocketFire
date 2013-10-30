/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import socketfire.message.STDMessage;

/**
 *
 * @author cesar
 */
public class DefaultMessageMirror extends MessageEventListener<STDMessage> {
	
	@Override
	public void onMessage(STDMessage message) {
		message.getSrc().getChannel().dispatch(message);
	}
	
}
