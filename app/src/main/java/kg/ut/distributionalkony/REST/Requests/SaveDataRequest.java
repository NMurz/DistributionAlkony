package kg.ut.distributionalkony.REST.Requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import kg.ut.distributionalkony.Models.UpdateRouteListDetail;
import kg.ut.distributionalkony.REST.API;

/**
 * Created by Nurs on 15.12.2015.
 */
public class SaveDataRequest extends RetrofitSpiceRequest<UpdateRouteListDetail, API> {

    private String token;
    private UpdateRouteListDetail updateRouteListDetail;

    public SaveDataRequest(String token, UpdateRouteListDetail updateRouteListDetail) {
        super(UpdateRouteListDetail.class, API.class);
        this.token = token;
        this.updateRouteListDetail = updateRouteListDetail;
    }

    @Override
    public UpdateRouteListDetail loadDataFromNetwork() throws Exception {

        return getService().saveRouteListDetails(token, updateRouteListDetail);
    }


}
