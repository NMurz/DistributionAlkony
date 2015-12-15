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
    void saveRouteListDetails(@Header("Authorization") String token, @Body UpdateRouteListDetail updateRouteListDetail, Callback<UpdateRouteListDetail> callback);

    @GET("/routes/getofflinedata")
    void getOfflineData(@Header("Authorization") String token, Callback<OfflineDataResponse> callback);

    @POST("/account/logon")
    void logOn(@Body UserLogOn userLogOn, Callback<LogInResponse> logInResponseCallback);

}
