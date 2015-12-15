package kg.ut.distributionalkony.REST;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Nurs on 15.12.2015.
 */
public class SaveDataRequest extends RetrofitSpiceRequest {
    public SaveDataRequest(Class clazz, Class retrofitedInterfaceClass) {
        super(clazz, retrofitedInterfaceClass);
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        return null;
    }


}
