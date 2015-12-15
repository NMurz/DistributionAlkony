package kg.ut.distributionalkony.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class GetSendDataHelper {
	public static String GetData(HttpClient httpclient, String url){		
		HttpGet httpget = new HttpGet(url);
		if (UserStateData.getInstance().GetCredentials().length()>0){
			httpget.addHeader("Authorization", "Basic " + UserStateData.getInstance().GetCredentials());
		}
				
		try {
			HttpParams params = new BasicHttpParams();
		    HttpConnectionParams.setSoTimeout(params, 180000); // 3 minute
		    httpget.setParams(params);
		      
			HttpResponse response = httpclient.execute(httpget);			
			
			
			InputStream inputStream = response.getEntity().getContent();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }

            return stringBuilder.toString();
            
		}catch (ClientProtocolException e) {           
        } catch (IOException e) {} 
		Log.w(">>>>>>>>","tut null");
		return null;		
	}
	
	public static String PostJsonData(HttpClient httpclient, String json, String url){    	
        HttpPost httppost = new HttpPost(url);
		if (UserStateData.getInstance().GetCredentials().length()>0){
			httppost.addHeader("Authorization", "Basic " + UserStateData.getInstance().GetCredentials());
		}
		
        try {
        	String data = json;
            // Add your data
        	StringEntity se = new StringEntity(data, HTTP.UTF_8);
        	se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);
            // Execute HTTP Post GetOfflineDataRequest
            
            HttpResponse response = httpclient.execute(httppost);
            InputStream inputStream = response.getEntity().getContent();
            
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }
            return stringBuilder.toString();
            
        } 
        catch (ClientProtocolException e) {
        } catch (IOException e) {} 
       
		return null;
	}	
}
