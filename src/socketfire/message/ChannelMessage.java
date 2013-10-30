/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import org.json.JSONException;
import org.json.JSONObject;
import socketfire.Client;

/**
 *
 * @author cesar
 */
public class ChannelMessage extends Message {

	private String action;
	private String[] args;

	public ChannelMessage(Client client, String action, String[] args) {
		super(Message.TYPE_CHANNEL, null, client);
		this.action = action;
		this.args = args;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
	
	@Override
	public Object getPayload() {
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", this.getAction());
			obj.put("args", this.getArgs());
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}
	
}
