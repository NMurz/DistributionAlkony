package kg.ut.distributionalkony.Repositories;

import kg.ut.distributionalkony.AsyncTaskManager.AsyncTaskManager;
import kg.ut.distributionalkony.AsyncTaskManager.OnTaskCompleteListener;
import kg.ut.distributionalkony.AsyncTaskManager.Task;
import kg.ut.distributionalkony.Interfaces.OutletDetailInterface;
import kg.ut.distributionalkony.Interfaces.OutletsInterface;
import kg.ut.distributionalkony.Models.OpenOutletModel;
import kg.ut.distributionalkony.Models.OutletDetailResponse;
import kg.ut.distributionalkony.Models.OutletResponse;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class OutletDetailRepository implements OnTaskCompleteListener{
	protected final Resources mResources;
	
	HttpClient httpClient;
	String url;
	Activity activity;
	Context context;
	private AsyncTaskManager mAsyncTaskManager;
	private String response;
	private final OutletDetailInterface _outletsDetailInterface;
	
	public OutletDetailRepository(HttpClient httpClient, 
								  String url, 
								  Activity activity, 
								  OutletDetailInterface outletDetailInterface,
								  Resources resources){
		this.mResources = resources;
		this.httpClient = httpClient;
		this.url = url;
		this.context = activity;
		this.activity = activity;
		this._outletsDetailInterface = outletDetailInterface;	
	}
	
	public void GetDetail(OpenOutletModel openOutletModel){
		Gson gson = new Gson();
		String toSend = gson.toJson(openOutletModel);
		context = activity;
		mAsyncTaskManager = new AsyncTaskManager(context, this);
		mAsyncTaskManager.setupTask(new Task(httpClient, url, "post", toSend));
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
				OutletDetailResponse outletDetailResponse = gson.fromJson(response, OutletDetailResponse.class);
									
				_outletsDetailInterface.onOutletDetailComplete(outletDetailResponse);
			
			} catch (Exception e) {				
				String message = mResources.getString(kg.ut.distributionalkony.R.string.message_convert_error);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				e.printStackTrace();				
			}
		}
	}
}