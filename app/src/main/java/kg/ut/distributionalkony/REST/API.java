package kg.ut.distributionalkony.REST;

import kg.ut.distributionalkony.Helpers.ApiUrlsHelper;
import kg.ut.distributionalkony.Models.LogInResponse;
import kg.ut.distributionalkony.Models.OfflineDataResponse;
import kg.ut.distributionalkony.Models.UpdateRouteListDetail;
import kg.ut.distributionalkony.Models.UserLogOn;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Nurs on 18.08.2015.
 */
public interface API {
    @POST("/routes/saveroutelistdetails")
    UpdateRouteListDetail saveRouteListDetails(@Header("Authorization") String token, @Body UpdateRouteListDetail updateRouteListDetail);

    @GET("/routes/getofflinedata")
    OfflineDataResponse getOfflineData(@Header("Authorization") String token);

    @POST("/account/logon")
    LogInResponse logOn(@Body UserLogOn userLogOn);

}
