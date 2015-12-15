package kg.ut.distributionalkony.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

/**
 * Created by Nurs on 20.08.2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    public NetworkChangeReceiver() {
        super();
    }
    private static boolean firstConnect = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
            ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = connMngr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = connMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = false;

        NetworkInfo networkInfo = connMngr.getActiveNetworkInfo();

        if(mobile.isConnected() || wifi.isConnected())
        {
            isConnected = true;
        }

        /*if(isConnected){
            if(firstConnect){
                try {
                    final Dao<RouteListIdsToSend, Integer> routeIdsDao = HelperFactory.getHelper().getRouteListIdsToSendDao();
                    Dao<RouteListDto, Integer> routeListDao = HelperFactory.getHelper().getRouteListDao();
                    Dao<RouteListElementDto, Integer> routeListElementDao = HelperFactory.getHelper().getRouteListElementDao();
                    final Dao<OrderCoordinate, Integer> orderCoordinateDao = HelperFactory.getHelper().getOrderCoordinateDao();

                    List<RouteListDto> routeLists = new ArrayList<RouteListDto>();
                    List<RouteListElementDto> routeListElementToSend = new ArrayList<RouteListElementDto>();
                    List<RouteListElementDto> routeListElements;
                    final List<OrderCoordinate> orderCoordinates = orderCoordinateDao.queryForAll();

                    final List<RouteListIdsToSend> routeIds = routeIdsDao.queryForAll();
                    if (!routeIds.isEmpty()) {
                        for (RouteListIdsToSend routeId : routeIds) {
                            QueryBuilder<RouteListDto, Integer> qbRouteList = routeListDao.queryBuilder();
                            qbRouteList.where().eq("Id", routeId.RouteListId);
                            RouteListDto routeListDto = qbRouteList.queryForFirst();
                            routeLists.add(routeListDto);
                            routeListElements = routeListElementDao.queryForEq("RouteListId", routeListDto.Id);
                            if (!routeListElements.isEmpty()) {
                                routeListElementToSend.addAll(routeListElements);
                            }
                        }


                        UpdateRouteListDetail updateDetail = new UpdateRouteListDetail(routeLists, routeListElementToSend, orderCoordinates);
                        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiUrlsHelper.Domain).setLogLevel(RestAdapter.LogLevel.FULL).build();
                        API api = restAdapter.create(API.class);
                        api.saveRouteListDetails("Basic " + UserStateData.getInstance().GetCredentials(), updateDetail, new Callback<UpdateRouteListDetail>() {
                            @Override
                            public void success(UpdateRouteListDetail updateRouteListDetail, Response response) {
                                if (response.getStatus() == 200)
                                    try {
                                        routeIdsDao.delete(routeIds);
                                        orderCoordinateDao.delete(orderCoordinates);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                Toast.makeText(context.getApplicationContext(), "Данные успешно отправлены!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                if (error.isNetworkError()) {
                                    Toast.makeText(context.getApplicationContext(), "Нету подключения к сети!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

            } catch (SQLException e) {
                e.printStackTrace();
            }
                firstConnect = false;
            }

        } else {
            firstConnect = true;
        }*/
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
