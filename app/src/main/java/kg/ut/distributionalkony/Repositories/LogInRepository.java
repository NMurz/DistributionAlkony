package kg.ut.distributionalkony.Repositories;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;
import kg.ut.distributionalkony.AsyncTaskManager.AsyncTaskManager;
import kg.ut.distributionalkony.AsyncTaskManager.OnTaskCompleteListener;
import kg.ut.distributionalkony.AsyncTaskManager.Task;
import kg.ut.distributionalkony.Interfaces.LogInInterface;
import kg.ut.distributionalkony.Models.LogInResponse;
import kg.ut.distributionalkony.Models.LogInResult;

public class LogInRepository  implements OnTaskCompleteListener{
	protected final Resources mResources;
	
	HttpClient httpClient;
	String url;
	Activity activity;
	Context context;
	private AsyncTaskManager mAsyncTaskManager;
	private String response;
	private final LogInInterface _logInInterface;
	
	public LogInRepository(HttpClient httpClient, 
							String url, 
							Activity activity, 
							LogInInterface logInInterface,
							Resources resources){
		this.mResources = resources;
		this.httpClient = httpClient;
		this.url = url;
		this.context = activity;
		this.activity = activity;
		this._logInInterface = logInInterface;
	}
	
	public void LogIn(String userName, String password){
		try{
			JSONObject json = new JSONObject();
			json.put("UserName",userName);
			json.put("Password", password);
						
			String toSend = json.toString();
			context = activity;
			mAsyncTaskManager = new AsyncTaskManager(context, this);			
			mAsyncTaskManager.setupTask(new Task(httpClient, url, "post", toSend));
			// Handle task that can be retained before
			mAsyncTaskManager.handleRetainedTask(activity.getLastNonConfigurationInstance());
		} catch(Exception e){
			String message = mResources.getString( kg.ut.distributionalkony.R.string.message_task_cancelled);
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
    public void onTaskComplete(Task task) {
		if (task.isCancelled()) {
		    // Report about cancel
			String message = mResources.getString(kg.ut.distributionalkony.R.string.message_task_cancelled);
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		} else {
			try {
				response = task.get();
											
				Gson gson = new Gson();
				LogInResponse logInResponse = gson.fromJson(response, LogInResponse.class);
				LogInResult logInResult = new LogInResult();
				logInResult.LogInResponse = logInResponse;
				logInResult.HttpClient = this.httpClient;
				
				_logInInterface.onLogInComplete(logInResult);
    			
			} catch (Exception e) {
				String message = mResources.getString(kg.ut.distributionalkony.R.string.message_convert_error);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
    	
    }		
}
