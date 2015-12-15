package kg.ut.distributionalkony.AsyncTaskManager;

public interface IProgressTracker {
    // Updates progress message
    void onProgress(String message);
    // Notifies about task completeness
    void onComplete();
}
