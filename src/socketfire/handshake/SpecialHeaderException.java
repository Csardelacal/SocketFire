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
public class SpecialHeaderException extends Exception {
	
	private Class correctType;
	
	public SpecialHeaderException(Class correct) {
		super("Invalid type used, should be " + correct.getName());
		this.correctType = correct;
	}
	
	public Class getCorrectType() {
		return this.correctType;
	}
	
}
