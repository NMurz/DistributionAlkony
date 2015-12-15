package kg.ut.distributionalkony.REST;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

/**
 * Created by Nurs on 15.12.2015.
 */
public class RetrofitSpiceService extends RetrofitGsonSpiceService{

    public static final String BASE_URL = "http://10.0.3.2:4455/api";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(API.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}
