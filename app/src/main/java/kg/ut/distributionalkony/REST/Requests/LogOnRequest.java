package kg.ut.distributionalkony.REST.Requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import kg.ut.distributionalkony.Models.LogInResponse;
import kg.ut.distributionalkony.Models.UserLogOn;
import kg.ut.distributionalkony.REST.API;
import roboguice.util.temp.Ln;

/**
 * Created by Nurs on 17.12.2015.
 */
public class LogOnRequest extends RetrofitSpiceRequest<LogInResponse, API> {

    private UserLogOn userLogOn;

    public LogOnRequest(UserLogOn userLogOn) {
        super(LogInResponse.class, API.class);
        this.userLogOn = userLogOn;
    }

    @Override
    public LogInResponse loadDataFromNetwork() throws Exception {
        Ln.d("Call LogOnRequest");
        return getService().logOn(userLogOn);
    }
}
