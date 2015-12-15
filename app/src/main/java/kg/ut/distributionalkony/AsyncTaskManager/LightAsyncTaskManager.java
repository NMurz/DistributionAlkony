package kg.ut.distributionalkony.AsyncTaskManager;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class LightAsyncTaskManager implements IProgressTracker, OnCancelListener {
    
    private final OnTaskCompleteListener mTaskCompleteListener;
    private Task mAsyncTask;

    public LightAsyncTaskManager(OnTaskCompleteListener taskCompleteListener) {
	// 	Save reference to complete listener (activity)
    	mTaskCompleteListener = taskCompleteListener;
    }

    public void setupTask(Task asyncTask) {
		// Keep task
		mAsyncTask = asyncTask;
		// Wire task to tracker (this)
		mAsyncTask.setProgressTracker(this);
		// Start task
		mAsyncTask.execute();
    }

    @Override
    public void onProgress(String message) {
    	// Show dialog if it wasn't shown yet or was removed on configuration (rotation) change
    }
    
    @Override
    public void onCancel(DialogInterface dialog) {
		// Cancel task
		mAsyncTask.cancel(true);
		// Notify activity about completion
		mTaskCompleteListener.onTaskComplete(mAsyncTask);
		// Reset task
		mAsyncTask = null;
    }
    
    @Override
    public void onComplete() {
    	// Notify activity about completion
    	mTaskCompleteListener.onTaskComplete(mAsyncTask);
    	// Reset task
    	mAsyncTask = null;
    }

    public Object retainTask() {
		// Detach task from tracker (this) before retain
		if (mAsyncTask != null) {
		    mAsyncTask.setProgressTracker(null);
		}
		// Retain task
		return mAsyncTask;
    }

    public void handleRetainedTask(Object instance) {
		// Restore retained task and attach it to tracker (this)
		if (instance instanceof Task) {
		    mAsyncTask = (Task) instance;
		    mAsyncTask.setProgressTracker(this);
		}
    }

    public boolean isWorking() {
	// Track current status
    	return mAsyncTask != null;
    }
}
