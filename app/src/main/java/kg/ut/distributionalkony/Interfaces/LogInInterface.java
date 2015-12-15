package kg.ut.distributionalkony.Interfaces;

import kg.ut.distributionalkony.Helpers.ApiUrlsHelper;
import kg.ut.distributionalkony.Models.LogInResponse;
import kg.ut.distributionalkony.Models.LogInResult;
import kg.ut.distributionalkony.Models.UserLogOn;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface LogInInterface {
	void onLogInComplete(LogInResult logInResult);


}
