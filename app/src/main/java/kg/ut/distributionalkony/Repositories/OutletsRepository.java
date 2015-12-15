package kg.ut.distributionalkony.Repositories;

import kg.ut.distributionalkony.AsyncTaskManager.AsyncTaskManager;
import kg.ut.distributionalkony.AsyncTaskManager.OnTaskCompleteListener;
import kg.ut.distributionalkony.AsyncTaskManager.Task;
import kg.ut.distributionalkony.Interfaces.DayOfWeekInterface;
import kg.ut.distributionalkony.Interfaces.OutletsInterface;
import kg.ut.distributionalkony.Models.DayOfWeekResponse;
import kg.ut.distributionalkony.Models.OutletResponse;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.google.gson.Gson;

public class OutletsRepository implements OnTaskCompleteListener{
	protected final Resources mResources;
	
	HttpClient httpClient;
	String url;
	Activity activity;
	Context context;
	private AsyncTaskManager mAsyncTaskManager;
	private String response;
	private final OutletsInterface _outletsInterface;
	
	public OutletsRepository(HttpClient httpClient, 
							   String url, 
							   Activity activity, 
							   OutletsInterface outletsInterface,
							   Resources resources){
		this.mResources = resources;
		this.httpClient = httpClient;
		this.url = url;
		this.context = activity;
		this.activity = activity;
		this._outletsInterface = outletsInterface;	
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
				OutletResponse outletResponse = gson.fromJson(response, OutletResponse.class);
									
				_outletsInterface.onOutletsComplete(outletResponse);
			
			} catch (Exception e) {				
				String message = mResources.getString(kg.ut.distributionalkony.R.string.message_convert_error);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				e.printStackTrace();				
			}
		}
	}
}
