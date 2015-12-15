package kg.ut.distributionalkony.Repositories;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import org.apache.http.client.HttpClient;

import java.lang.reflect.Type;
import java.util.Date;

import kg.ut.distributionalkony.AsyncTaskManager.AsyncTaskManager;
import kg.ut.distributionalkony.AsyncTaskManager.OnTaskCompleteListener;
import kg.ut.distributionalkony.AsyncTaskManager.Task;
import kg.ut.distributionalkony.Interfaces.OfflineDataInterface;
import kg.ut.distributionalkony.Models.DayOfWeekResponse;
import kg.ut.distributionalkony.Models.OfflineDataResponse;

/**
 * Created by Nurs on 10.08.2015.
 */
public class OfflineDataRepository implements OnTaskCompleteListener {
    protected final Resources mResources;

    HttpClient httpClient;
    String url;
    Activity activity;
    Context context;
    private AsyncTaskManager mAsyncTaskManager;
    private String response;
    private final OfflineDataInterface offlineDataInterface;

    public OfflineDataRepository(HttpClient httpClient,
                                 String url,
                                 Activity activity,  OfflineDataInterface offlineDataInterface, Resources resources) {
        this.mResources = resources;
        this.httpClient = httpClient;
        this.url = url;
        this.context = activity;
        this.activity = activity;
        this.offlineDataInterface = offlineDataInterface;
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

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                OfflineDataResponse offlineDataResponse = gson.fromJson(response, OfflineDataResponse.class);

                offlineDataInterface.OnOfflineDataComplete(offlineDataResponse);

            } catch (Exception e) {
                e.printStackTrace(System.out);
                String message = mResources.getString(kg.ut.distributionalkony.R.string.message_convert_error);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
