package kg.ut.distributionalkony.Helpers;

import android.content.Context;

import kg.ut.distributionalkony.Models.UserData;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class PublicData{
	private static PublicData publicHttpClient = null;
	Context context;
	private HttpClient httpClient;
	private UserData userData;
	private double Latitude = 0;
	private double Longitude = 0;
	private boolean timerRunning;
	
	public PublicData() {
		httpClient = new DefaultHttpClient();
		userData = new UserData();
		timerRunning = false;

		Latitude = 0;
		Longitude = 0;
	}
	
	public static PublicData getInstance(){
		if (publicHttpClient==null){
			publicHttpClient = new PublicData();
		}
					
		return publicHttpClient;
	}


	public boolean isTimerRunning() {
		return timerRunning;
	}

	public void setTimerRunning(boolean timerRunning) {
		this.timerRunning = timerRunning;
	}

	public HttpClient getHttpClient(){
		return this.httpClient;
	}
	
	public void setHttpClient(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	public UserData getUserData(){
		return this.userData;
	}
	
	public void setUserData(UserData userData){
		this.userData = userData;
	}
	
	public void SetLatitude(double latitude){
		Latitude = latitude;
	}
	
	public double GetLatitude (){
		return Latitude;
	}
	
	public void SetLongitude(double longitude){
		Longitude = longitude;
	}
	
	public double GetLongitude(){
		return Longitude;
	}
}
