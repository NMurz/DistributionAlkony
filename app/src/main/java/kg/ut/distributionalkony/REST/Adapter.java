package kg.ut.distributionalkony.REST;

import retrofit.RestAdapter;

/**
 * Created by Nurs on 20.11.2015.
 */
public class Adapter {
    //public static final String WEB_URL = "http://10.0.3.2:45761/api";
    public static final String WEB_URL = "http://10.0.3.2:4455/api";
    //public static final String WEB_URL = "http://176.126.167.9:8080/api";

    public static final RestAdapter RestAdapter(){
        return new RestAdapter.Builder()
                .setEndpoint(WEB_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}
