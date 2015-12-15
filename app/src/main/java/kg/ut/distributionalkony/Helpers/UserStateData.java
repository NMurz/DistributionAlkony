package kg.ut.distributionalkony.Helpers;

import android.util.Base64;

public class UserStateData {
	private static UserStateData userStateData = null;
	private String _userName;
	private String _userPassword;
	private String _userId;
	private String _base64EncodedCredentials;
	
	private UserStateData(){
		_userName = "";
		_userPassword = "";
		_userId = "";
		_base64EncodedCredentials="";
	}
	
	public static UserStateData getInstance(){
		if (userStateData==null){
			userStateData = new UserStateData();
		}
			
		return userStateData;
	}
	
	public String GetUserPassword(){
		return this._userPassword;
	}
	
	public void SetUserPassword(String password){
		this._userPassword =password; 
	}
	
	public String GetUserName(){
		return this._userName;
	}
	
	public void SetUserName(String userName){
		this._userName = userName;
	}
	
	public void InitCredentials(String login, String pass){
		String credentials = login + ":" + pass;  
    	this._base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	}
	
	public void SetCredentials(String credentinals){
		this._base64EncodedCredentials = credentinals;
	}
	
	public String GetCredentials(){
		return this._base64EncodedCredentials;
	}

	public String getUserId() {
		return _userId;
	}

	public void setUserId(String _userId) {
		this._userId = _userId;
	}
}
