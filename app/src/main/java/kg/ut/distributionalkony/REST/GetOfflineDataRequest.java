package kg.ut.distributionalkony.REST;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import kg.ut.distributionalkony.Models.OfflineDataResponse;
import roboguice.util.temp.Ln;

/**
 * Created by Nurs on 15.12.2015.
 */
public class GetOfflineDataRequest extends RetrofitSpiceRequest<OfflineDataResponse, API> {

    private String token;

    public GetOfflineDataRequest(String token) {
        super(OfflineDataResponse.class, API.class);
        this.token = token;
    }

    @Override
    public OfflineDataResponse loadDataFromNetwork() throws Exception {
        Ln.d("Call OfflinaDataRequest");
        return getService().getOfflineData(token);
    }
}
