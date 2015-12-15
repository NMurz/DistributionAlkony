package kg.ut.distributionalkony.Helpers;

import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * Created by Nurs on 10.08.2015.
 */
public class Application extends android.app.Application {

    NetworkChangeReceiver networkChangeReceiver;

    public void initNetworkChangeReceiver(){
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());

    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
        unregisterReceiver(networkChangeReceiver);
    }


}
