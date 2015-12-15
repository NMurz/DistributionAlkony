package kg.ut.distributionalkony.AsyncTaskManager;

import java.util.ArrayList;
import java.util.List;

import kg.ut.distributionalkony.Helpers.GetSendDataHelper;
import kg.ut.distributionalkony.Models.ErrorResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class Task extends AsyncTask<Void, Void, String>{
	HttpClient httpClient;
	String url;
	String type;
	String sendData;
	private Boolean mResult;
    private IProgressTracker mProgressTracker;
    private String mProgressMessage;
		
	public Task(HttpClient httpClient, String url, String type, String data){
		this.httpClient = httpClient;
		this.url = url;
		this.mProgressMessage = "Пожалуйста, подождите.";
		this.type = type;
		this.sendData = data;
	}
	
    /* UI Thread */
    public void setProgressTracker(IProgressTracker progressTracker) {
		// Attach to progress tracker
		mProgressTracker = progressTracker;
		if (mProgressTracker != null) {
		    mProgressTracker.onProgress(mProgressMessage);
		    if (mResult != null) {
		    	mProgressTracker.onComplete();
		    }
		}
    }
    
 	@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
    	kg.ut.distributionalkony.Models.Status status = new kg.ut.distributionalkony.Models.Status();
    	status.Code = 500;
    	status.Message="ServerError";
    	
    	ErrorResponse errorResponse = new ErrorResponse();
    	errorResponse.DataList = new ArrayList<String>();
    	errorResponse.Status = status;
    	
    	Gson gson = new Gson();
		String response = gson.toJson(errorResponse);
    	try {
    		if (type.equals("get")){
    			response = GetSendDataHelper.GetData(httpClient, url);
    		} else {
    			if (type.equals("post")){
    				response = GetSendDataHelper.PostJsonData(httpClient, sendData, url);
    			}
    		}
		} catch (Exception e) {
		}
    	
    	return response;
    }

    @Override
    protected void onPostExecute(String response) {
    	super.onPostExecute(response);
    	if (mProgressTracker != null) {
    	    mProgressTracker.onComplete();
    	}
    	// Detach from progress tracker
    	mProgressTracker = null;
    }
}
