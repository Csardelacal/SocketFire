/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import java.util.ArrayList;
import socketfire.message.Message;

/**
 *
 * @author cesar
 * @param <T> The type of messages the handlers this manager contains will handle
 */
public class EventManager<T extends Message> {
	
	private ArrayList<MessageEventListener<T>> listeners = new ArrayList<>();
	
	public void addEventListener(MessageEventListener<T> listener) {
		this.listeners.add(listener);
	}
	
	public void trigger(T message) {
		int l = this.listeners.size();
		for (int i = l - 1; i >= 0; i--) {
			this.listeners.get(i).onMessage(message);
			if (!message.doesBubble()) return;
		}
	}
	
}
