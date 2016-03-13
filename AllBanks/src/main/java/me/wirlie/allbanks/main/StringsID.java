/**
 * 
 */
package me.wirlie.allbanks.main;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
 *
 */
public enum StringsID {

	ENABLING(1),
	DISABLING(2), 
	SIGN_MORE_ARGUMENTS_NEEDED(3), 
	SIGN_NOT_CONFIGURED(4),
	CLICK_TO_USE(6),
	
	;
	
	int strID;
	
	StringsID(int strID){
		this.strID = strID;
	}
	
	String getPath(){
		
		//por el momento los strings tienen formato numérico.
		return String.valueOf(strID);
	}
	
	@Override
	public String toString(){
		return Translation.get(getPath(), true)[0];
	}
}
