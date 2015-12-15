package kg.ut.distributionalkony.Repositories;

import org.apache.http.client.HttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;
import kg.ut.distributionalkony.AsyncTaskManager.AsyncTaskManager;
import kg.ut.distributionalkony.AsyncTaskManager.OnTaskCompleteListener;
import kg.ut.distributionalkony.AsyncTaskManager.Task;
import kg.ut.distributionalkony.Interfaces.DayOfWeekInterface;
import kg.ut.distributionalkony.Models.DayOfWeekResponse;

public class DayOfWeekRepository implements OnTaskCompleteListener{
	protected final Resources mResources;
	
	HttpClient httpClient;
	String url;
	Activity activity;
	Context context;
	private AsyncTaskManager mAsyncTaskManager;
	private String response;
	private final DayOfWeekInterface _dayOfWeekInterface;
	
	public DayOfWeekRepository(HttpClient httpClient, 
							   String url, 
							   Activity activity, 
							   DayOfWeekInterface dayOfWeekInterface,
							   Resources resources){
		this.mResources = resources;
		this.httpClient = httpClient;
		this.url = url;
		this.context = activity;
		this.activity = activity;
		this._dayOfWeekInterface = dayOfWeekInterface;	
	}
	
	public void GetAll(){   	    	
		mAsyncTaskManager = new AsyncTaskManager(context, this);
		mAsyncTaskManager.setupTask(new Task(httpClient, url, "get", ""));
		// Handle task that can be retained before
		mAsyncTaskManager.handleRetainedTask(activity.getLastNonConfigurationInstance());
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
				DayOfWeekResponse dayOfWeekResponse = gson.fromJson(response, DayOfWeekResponse.class);
									
				_dayOfWeekInterface.onDaysOfWeekComplete(dayOfWeekResponse);
			
			} catch (Exception e) {				
				String message = mResources.getString(kg.ut.distributionalkony.R.string.message_convert_error);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				e.printStackTrace();				
			}
		}
	}}
