package kg.ut.distributionalkony.REST;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

/**
 * Created by Nurs on 15.12.2015.
 */
public class RetrofitSpiceService extends RetrofitGsonSpiceService{

    public static final String BASE_URL = "http://nrskdm-001-site1.btempurl.com/distribution.api/api";

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
