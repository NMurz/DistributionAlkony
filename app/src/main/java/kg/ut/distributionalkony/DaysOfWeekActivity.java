package kg.ut.distributionalkony;

import java.net.ConnectException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.HttpClient;

import kg.ut.distributionalkony.Dto.DiscountDto;
import kg.ut.distributionalkony.Dto.OutletDto;
import kg.ut.distributionalkony.Dto.OutletVisit;
import kg.ut.distributionalkony.Dto.PriceListElementDto;
import kg.ut.distributionalkony.Dto.RouteListDto;
import kg.ut.distributionalkony.Dto.RouteListElementDto;
import kg.ut.distributionalkony.Dto.RouteListIdsToSend;
import kg.ut.distributionalkony.Dto.StorageDto;
import kg.ut.distributionalkony.Dto.StorageItemDto;
import kg.ut.distributionalkony.Helpers.GPSTracker;
import kg.ut.distributionalkony.Helpers.HelperFactory;
import kg.ut.distributionalkony.Helpers.PublicData;
import kg.ut.distributionalkony.Helpers.UserStateData;
import kg.ut.distributionalkony.REST.API;
import kg.ut.distributionalkony.Models.DayOfWeek;
import kg.ut.distributionalkony.Models.OfflineDataResponse;
import kg.ut.distributionalkony.Models.OrderCoordinate;
import kg.ut.distributionalkony.Models.UpdateRouteListDetail;
import kg.ut.distributionalkony.Models.UserData;
import kg.ut.distributionalkony.REST.Adapter;
import kg.ut.distributionalkony.REST.Requests.GetOfflineDataRequest;
import kg.ut.distributionalkony.REST.Requests.SaveDataRequest;
import kg.ut.distributionalkony.REST.RetrofitSpiceService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.squareup.okhttp.OkHttpClient;

public class DaysOfWeekActivity extends ActionBarActivity{
	LatLngReceiver latLngReceiver;
	
	UserData user;
	HttpClient httpClient;
	Dao<RouteListDto, Integer> routeListDao;
	Dao<RouteListElementDto, Integer> routeListElementDao;
	Dao<PriceListElementDto, Integer> priceListElementDao;
	Dao<StorageDto, Integer> storageDao;
	Dao<StorageItemDto, Integer> storageItemDao;
	Dao<DiscountDto, Integer> discountDao;
	Dao<OutletDto, Integer> outletDao;
	Dao<DayOfWeek, Integer> dayOfWeekDao;
	Dao<OutletVisit, Integer> outletVisitDao;
	Dao<RouteListIdsToSend, Integer> routeListIdsToSendDao;
	Dao<OrderCoordinate, Integer> orderCoordinateDao;
	List<OrderCoordinate> orderCoordinates;
	List<DayOfWeek> daysOfWeek;
	List<RouteListIdsToSend> routeListIdsToSendList;
	ProgressDialog mProgressDialog;
	SharedPreferences prefs;
	SharedPreferences.Editor ed;
	boolean isAuthenticated;
	boolean isOffline;
	Intent serviceIntent;
	GPSTracker gps;
	String credentials;
	private SpiceManager spiceManager = new SpiceManager(RetrofitSpiceService.class);
	private GetOfflineDataRequest getOfflineDataRequest;
	private SaveDataRequest saveDataRequest;

	public final static String DAYOFWEEKID = "DAYOFWEEKID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		userAuthenticated();
		setContentView(R.layout.activity_days_of_week);
		setTitle(R.string.title_activity_days_of_week);

        httpClient = PublicData.getInstance().getHttpClient();
        user = PublicData.getInstance().getUserData();
		isOffline = prefs.getBoolean("offlineMode", false);
		getOfflineDataRequest = new GetOfflineDataRequest("Basic " + credentials);
		mProgressDialog = new ProgressDialog(this);


        //serviceIntent = new Intent(this, GpsLocationService.class);
        //startService(serviceIntent);
		gps = new GPSTracker(DaysOfWeekActivity.this);
		if(gps.canGetLocation()){
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
		} else {
			gps.showSettingsAlert();
		}
		try{
			dayOfWeekDao = HelperFactory.getHelper().getDayOfWeekDao();
			routeListDao = HelperFactory.getHelper().getRouteListDao();
			routeListElementDao = HelperFactory.getHelper().getRouteListElementDao();
			priceListElementDao = HelperFactory.getHelper().getPriceListElementDao();
			storageDao = HelperFactory.getHelper().getStorageDao();
			orderCoordinateDao = HelperFactory.getHelper().getOrderCoordinateDao();
			storageItemDao = HelperFactory.getHelper().getStorageItemDao();
			discountDao = HelperFactory.getHelper().getDiscountDao();
			outletDao = HelperFactory.getHelper().getOutletDao();
			outletVisitDao = HelperFactory.getHelper().getOutletVisitDao();
			routeListIdsToSendDao = HelperFactory.getHelper().getRouteListIdsToSendDao();
			routeListIdsToSendList = routeListIdsToSendDao.queryForAll();
			if(isAuthenticated) {
				if(isOffline) {
					List<DayOfWeek> dayOfWeeks = dayOfWeekDao.queryForAll();
					ShowDayOfWeeks(dayOfWeeks);
				} else  {
					if (isOnline()) {
						if(routeListIdsToSendList.isEmpty()){
							routeListIdsToSendList = routeListIdsToSendDao.queryForAll();
						}
						if (!routeListIdsToSendList.isEmpty()) {
							AlertDialog.Builder builder = new AlertDialog.Builder(DaysOfWeekActivity.this);
							builder.setMessage("В базе данных есть неотправленные данные!");
							builder.setTitle("Подключение");
							builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

								}
							});

							builder.create().show();
							List<DayOfWeek> dayOfWeeks = dayOfWeekDao.queryForAll();
							ShowDayOfWeeks(dayOfWeeks);
						} else {

							mProgressDialog.setIndeterminate(true);
							mProgressDialog.setMessage("Загрузка данных.");
							mProgressDialog.setCancelable(false);
							mProgressDialog.setCanceledOnTouchOutside(false);
							mProgressDialog.show();

							spiceManager.execute(getOfflineDataRequest, "offlineDataRequest", DurationInMillis.ONE_MINUTE, new OfflineDataRequestListener());
							//getOfflineData();
						}

					} else {
						List<DayOfWeek> dayOfWeeks = dayOfWeekDao.queryForAll();
						ShowDayOfWeeks(dayOfWeeks);

					}
				}

		}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public boolean isOnline(){
		ConnectivityManager connMngr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMngr.getActiveNetworkInfo();

		if(networkInfo != null && (networkInfo.isConnected())) {
			return true;
		}
		return false;

	}

	public void userAuthenticated(){

		isAuthenticated = prefs.getBoolean("isAuthenticated", false);
		if(!isAuthenticated) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		} else {
			credentials = prefs.getString("Credentials","");
			UserStateData.getInstance().SetCredentials(prefs.getString("Credentials", ""));
			UserStateData.getInstance().setUserId(prefs.getString("UserId",""));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);
		if(isOffline) menu.findItem(R.id.offline_check).setChecked(true);
		return true;
	}


	/*public void getOfflineData(){

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setMessage("Загрузка данных.");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();


		API api = Adapter.RestAdapter().create(API.class);
		api.getOfflineData("Basic " + credentials, new Callback<OfflineDataResponse>() {
			@Override
			public void success(OfflineDataResponse offlineDataResponse, Response response) {
				if (response.getStatus() == 200) {
					try {

						HelperFactory.getHelper().clearTables();

						if (!offlineDataResponse.RouteLists.isEmpty()) {
							List<RouteListDto> routeLists = offlineDataResponse.RouteLists;
							for (RouteListDto routeList : routeLists)
								routeListDao.create(routeList);
						}
						if (!offlineDataResponse.RouteListElements.isEmpty()) {
							List<RouteListElementDto> routeListElements = offlineDataResponse.RouteListElements;
							for (RouteListElementDto routeListElementDto : routeListElements)
								routeListElementDao.create(routeListElementDto);
						}
						if (!offlineDataResponse.PriceListElements.isEmpty()) {
							List<PriceListElementDto> priceListElements = offlineDataResponse.PriceListElements;
							for (PriceListElementDto priceListElement : priceListElements)
								priceListElementDao.create(priceListElement);
						}
						if (!offlineDataResponse.Storages.isEmpty()) {
							List<StorageDto> storages = offlineDataResponse.Storages;
							for (StorageDto storage : storages) storageDao.create(storage);
						}
						if (!offlineDataResponse.StorageItems.isEmpty()) {
							List<StorageItemDto> storageItems = offlineDataResponse.StorageItems;
							for (StorageItemDto storageItem : storageItems)
								storageItemDao.create(storageItem);
						}
						if (!offlineDataResponse.Discounts.isEmpty()) {
							List<DiscountDto> discounts = offlineDataResponse.Discounts;
							for (DiscountDto discount : discounts) discountDao.create(discount);
						}
						if (!offlineDataResponse.Outlets.isEmpty()) {
							List<OutletDto> outlets = offlineDataResponse.Outlets;
							for (OutletDto outlet : outlets) outletDao.create(outlet);
						}
						daysOfWeek = new ArrayList<DayOfWeek>();
						if (!offlineDataResponse.DaysOfWeek.isEmpty()) {
							daysOfWeek.addAll(offlineDataResponse.DaysOfWeek);
							for (DayOfWeek dayOfWeek : daysOfWeek) dayOfWeekDao.create(dayOfWeek);
						}
						if (!offlineDataResponse.OutletVisits.isEmpty()) {
							List<OutletVisit> outletVisits = offlineDataResponse.OutletVisits;
							for (OutletVisit outletVisit : outletVisits)
								outletVisitDao.create(outletVisit);
						}

						if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

					} catch (SQLException e) {
						e.printStackTrace();
					}

					if (!daysOfWeek.isEmpty()) {
						ShowDayOfWeeks(daysOfWeek);
					} else {
						AlertDialog.Builder dialog = new AlertDialog.Builder(DaysOfWeekActivity.this);
						dialog.setTitle("Дни посещения")
								.setMessage("Дни посещения не закреплены за агентом")
								.setCancelable(false)
								.setPositiveButton("Выход", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
										SharedPreferences.Editor ed = prefs.edit();
										ed.putBoolean("isAuthenticated", false);
										ed.putString("Credentials", "");
										ed.commit();
										Intent intent = new Intent(Intent.ACTION_MAIN);
										intent.addCategory(Intent.CATEGORY_HOME);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										stopService(serviceIntent);
										gps.stopUsingGPS();
										System.exit(0);
									}
								});
						dialog.create().show();
					}
					if (gps.canGetLocation()) {
						double latitude = gps.getLatitude();
						double longitude = gps.getLongitude();
					} else {
						gps.showSettingsAlert();
					}

				}
			}

			@Override
			public void failure(RetrofitError error) {
				if (error.isNetworkError()) {
					if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
					AlertDialog.Builder dialog = new AlertDialog.Builder(DaysOfWeekActivity.this);
					dialog.setTitle("Подключение");
					dialog.setMessage("Отсутствует подключение к сети! Повторить запрос для получения данных?");
					dialog.setCancelable(false);
					dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							getOfflineData();
						}
					});
					dialog.setNegativeButton("Выход", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							System.exit(0);
						}
					});
					dialog.create().show();
				}
			}
		});
	}*/

	private void ShowDayOfWeeks(List<DayOfWeek> dayOfWeeks){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
		LinearLayout rl = (LinearLayout)findViewById(R.id.contentLayout);
		for (int i = 0; i < dayOfWeeks.size(); i++) {
			DayOfWeek dayOfWeek = dayOfWeeks.get(i);
            						
                        	
            String title = dayOfWeek.DayName;
           
            Calendar cal = Calendar.getInstance();
            int currentDayIndex = cal.get(Calendar.DAY_OF_WEEK);
            
            if (currentDayIndex==1){
            	currentDayIndex = 7;
            } else {
            	currentDayIndex = currentDayIndex-1;
            }
            
            //Log.e("Alkony", dayOfWeek.DayName + " " + String.valueOf(dayOfWeek.DayNumber) + " " + String.valueOf(currentDayIndex));
            
            Calendar bufCal = Calendar.getInstance();
            if (dayOfWeek.DayNumber<currentDayIndex){
            	int bufIndex = 0;
            	do {
            		bufCal.add(Calendar.DATE, 1);
            		bufIndex = bufCal.get(Calendar.DAY_OF_WEEK);
            		
	            	if (bufIndex==1){
	            		bufIndex = 7;
	                } else {
	                	bufIndex = bufIndex-1;
	                }
            	}
            	while (bufIndex != dayOfWeek.DayNumber); 
            	
            	
            }  else {
            	bufCal.add(Calendar.DATE, dayOfWeek.DayNumber - currentDayIndex);
            }

            title = title + "(" + dateFormat.format(bufCal.getTime()) + ")";
            
            TextView myButton = new TextView(this);
            
            myButton.setText(title);           
            myButton.setTag(String.valueOf(dayOfWeek.DayNumber)); 
            
            myButton.setOnClickListener(getOnClick(myButton));            
            myButton.setBackgroundColor(Color.parseColor(this.getString(kg.ut.distributionalkony.R.color.MenuBackgroundColor)));
            myButton.setTextColor(Color.parseColor(this.getString(kg.ut.distributionalkony.R.color.Menu_Color)));            

            int tmargin = dip(this, 5);            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.setMargins(0, tmargin, 0, 0);
            myButton.setLayoutParams(params);
            myButton.setMinHeight(dip(this,40));
            myButton.setGravity(Gravity.CENTER);

            rl.addView(myButton);
			
        }  	
	}

	public static int dip(Context context, int pixels) {
		   float scale = context.getResources().getDisplayMetrics().density;
		   return (int) (pixels * scale + 0.5f);
	}
	
	public View.OnClickListener getOnClick(final TextView button)  {
	    return new View.OnClickListener() {
	    	@Override
	        public void onClick(View v) {
	            final String dayIndex = button.getTag().toString();
	            
	            Intent intent = new Intent(DaysOfWeekActivity.this, OutletsActivity.class);
	            intent.putExtra(DAYOFWEEKID, dayIndex);
	            startActivity(intent);
	        }
	    };
	}	



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.offline_check) {
			ed = prefs.edit();
			if (isOffline) {
				item.setChecked(false);
				ed.putBoolean("offlineMode", false);
				isOffline = false;
			}
			else {
				item.setChecked(true);
				ed.putBoolean("offlineMode", true);
				isOffline = true;

			}
			ed.commit();
		}

		if (id == R.id.action_exit) {
			ExitClick();
		}
		if(id == R.id.send_data) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(DaysOfWeekActivity.this);
			builder.setTitle("Данные");
			if(routeListIdsToSendList.isEmpty()) {
				try {
					routeListIdsToSendList = routeListIdsToSendDao.queryForAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(!routeListIdsToSendList.isEmpty()) {
				builder.setMessage("Неотправленных заказов: " + routeListIdsToSendList.size());
				builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(isOnline()) {
							sendRouteListsData();
						} else {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(DaysOfWeekActivity.this);
							builder2.setTitle("Подключение");
							builder2.setMessage("Нету подключения к интернету!");
							builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							});
							builder2.create().show();
						}
					}
				});
				builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
				builder.create().show();
			} else {
				builder.setMessage("В локальной базе нет заказов.");
				builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				});
				builder.create().show();
			}


		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ExitClick(){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
					//prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					ed = prefs.edit();
					ed.putBoolean("offlineMode", false);
					ed.putBoolean("isAuthenticated", false);
					ed.putString("Credentials", "");
					ed.putString("UserId","");
					ed.commit();
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//stopService(serviceIntent);
					gps.stopUsingGPS();
    	        	System.exit(0);

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(DaysOfWeekActivity.this);
    	String message = DaysOfWeekActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit);
    	String yes = DaysOfWeekActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit_yes);
    	String no = DaysOfWeekActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit_no);
    	
    	builder.setMessage(message).setPositiveButton(yes, dialogClickListener)
    	    .setNegativeButton(no, dialogClickListener).show();
	}

	@Override
	protected void onResume(){
		super.onResume();
		latLngReceiver = new LatLngReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GpsLocationService.UPDATE_LATLNG);
		registerReceiver(latLngReceiver, intentFilter);
	}

	@Override
	protected void onPause(){
		super.onPause();
		unregisterReceiver(latLngReceiver);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onStart(){
		spiceManager.start(this);
		super.onStart();
	}

	@Override
	protected void onStop(){
		spiceManager.shouldStop();
		super.onStop();
	}

	private class LatLngReceiver extends BroadcastReceiver{
    	 
    	 @Override
    	 public void onReceive(Context arg0, Intent arg1) {
    	  // TODO Auto-generated method stub
    	  
    	  Bundle b = arg1.getBundleExtra("Location");
          Location lastKnownLoc = (Location) b.getParcelable("Location");
          if (lastKnownLoc != null) {
        	  PublicData.getInstance().SetLatitude(lastKnownLoc.getLatitude());
        	  PublicData.getInstance().SetLongitude(lastKnownLoc.getLongitude());
          }
    	 }
    	 
	}

	public final class OfflineDataRequestListener implements RequestListener<OfflineDataResponse>{

		@Override
		public void onRequestFailure(SpiceException spiceException) {

			if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
			AlertDialog.Builder dialog = new AlertDialog.Builder(DaysOfWeekActivity.this);
			dialog.setTitle("Подключение");
			dialog.setMessage("Отсутствует подключение к сети! Повторить запрос для получения данных?");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					spiceManager.execute(getOfflineDataRequest, "offlineDataRequest", DurationInMillis.ONE_MINUTE, new OfflineDataRequestListener());
				}
			});
			dialog.setNegativeButton("Выход", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					System.exit(0);
				}
			});
			dialog.create().show();

		}

		@Override
		public void onRequestSuccess(OfflineDataResponse offlineDataResponse) {

			try {

				HelperFactory.getHelper().clearTables();
				mProgressDialog.setMessage("Добавление данных в базу");

				if (!offlineDataResponse.RouteLists.isEmpty()) {
					List<RouteListDto> routeLists = offlineDataResponse.RouteLists;
					for (RouteListDto routeList : routeLists)
						routeListDao.create(routeList);
				}
				if (!offlineDataResponse.RouteListElements.isEmpty()) {
					List<RouteListElementDto> routeListElements = offlineDataResponse.RouteListElements;
					for (RouteListElementDto routeListElementDto : routeListElements)
						routeListElementDao.create(routeListElementDto);
				}
				if (!offlineDataResponse.PriceListElements.isEmpty()) {
					List<PriceListElementDto> priceListElements = offlineDataResponse.PriceListElements;
					for (PriceListElementDto priceListElement : priceListElements)
						priceListElementDao.create(priceListElement);
				}
				if (!offlineDataResponse.Storages.isEmpty()) {
					List<StorageDto> storages = offlineDataResponse.Storages;
					for (StorageDto storage : storages) storageDao.create(storage);
				}
				if (!offlineDataResponse.StorageItems.isEmpty()) {
					List<StorageItemDto> storageItems = offlineDataResponse.StorageItems;
					for (StorageItemDto storageItem : storageItems)
						storageItemDao.create(storageItem);
				}
				if (!offlineDataResponse.Discounts.isEmpty()) {
					List<DiscountDto> discounts = offlineDataResponse.Discounts;
					for (DiscountDto discount : discounts) discountDao.create(discount);
				}
				if (!offlineDataResponse.Outlets.isEmpty()) {
					List<OutletDto> outlets = offlineDataResponse.Outlets;
					for (OutletDto outlet : outlets) outletDao.create(outlet);
				}
				daysOfWeek = new ArrayList<DayOfWeek>();
				if (!offlineDataResponse.DaysOfWeek.isEmpty()) {
					daysOfWeek.addAll(offlineDataResponse.DaysOfWeek);
					for (DayOfWeek dayOfWeek : daysOfWeek) dayOfWeekDao.create(dayOfWeek);
				}
				if (!offlineDataResponse.OutletVisits.isEmpty()) {
					List<OutletVisit> outletVisits = offlineDataResponse.OutletVisits;
					for (OutletVisit outletVisit : outletVisits)
						outletVisitDao.create(outletVisit);
				}

				if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (!daysOfWeek.isEmpty()) {
				ShowDayOfWeeks(daysOfWeek);
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(DaysOfWeekActivity.this);
				dialog.setTitle("Дни посещения")
						.setMessage("Дни посещения не закреплены за агентом")
						.setCancelable(false)
						.setPositiveButton("Выход", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								SharedPreferences.Editor ed = prefs.edit();
								ed.putBoolean("isAuthenticated", false);
								ed.putString("Credentials", "");
								ed.commit();
								Intent intent = new Intent(Intent.ACTION_MAIN);
								intent.addCategory(Intent.CATEGORY_HOME);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								stopService(serviceIntent);
								gps.stopUsingGPS();
								System.exit(0);
							}
						});
				dialog.create().show();
			}
			if (gps.canGetLocation()) {
				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();
			} else {
				gps.showSettingsAlert();
			}

		}
	}

	public void sendRouteListsData(){

		if(isOnline()){
			try{
				List<RouteListDto> routeLists = new ArrayList<RouteListDto>();
				Set<RouteListDto> routeListDtoSet = new HashSet<RouteListDto>();
				List<RouteListElementDto> routeListElementToSend = new ArrayList<RouteListElementDto>();
				List<RouteListElementDto> routeListElements;
				orderCoordinates = orderCoordinateDao.queryForAll();
				if(routeListIdsToSendList.isEmpty()) {
					try {
						routeListIdsToSendList = routeListIdsToSendDao.queryForAll();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (!routeListIdsToSendList.isEmpty()) {
					for (RouteListIdsToSend routeId : routeListIdsToSendList) {
						QueryBuilder<RouteListDto, Integer> qbRouteList = routeListDao.queryBuilder();
						qbRouteList.where().eq("Id", routeId.RouteListId);
						RouteListDto routeListDto = qbRouteList.queryForFirst();
						routeListDtoSet.add(routeListDto);
						QueryBuilder<RouteListElementDto, Integer> qbRouteListElement = routeListElementDao.queryBuilder();
						qbRouteListElement.where().eq("RouteListId", routeListDto.Id).and().eq("OutletId", routeId.OutletId);
						routeListElements = qbRouteListElement.query();
						if (!routeListElements.isEmpty()) {
							routeListElementToSend.addAll(routeListElements);
						}
					}

					routeLists.addAll(routeListDtoSet);

					mProgressDialog.setIndeterminate(true);
					mProgressDialog.setMessage("Отправка данных. Пожалуйста подождите.");
					mProgressDialog.show();


					UpdateRouteListDetail updateDetail = new UpdateRouteListDetail(routeLists, routeListElementToSend, orderCoordinates);
					saveDataRequest = new SaveDataRequest("Basic " + credentials, updateDetail);
					spiceManager.execute(saveDataRequest, "saveDataAllRequest", DurationInMillis.ONE_MINUTE, new SaveOfflineDataListener());

						/*API api = Adapter.RestAdapter().create(API.class);
						api.saveRouteListDetails("Basic " + credentials, updateDetail, new Callback<UpdateRouteListDetail>() {
							@Override
							public void success(UpdateRouteListDetail updateRouteListDetail, Response response) {
									if (mProgressDialog.isShowing())
										mProgressDialog.dismiss();
									try {
										routeListIdsToSendDao.delete(routeIds);
										orderCoordinateDaOnline.delete(orderCoordinates);
									} catch (SQLException e) {
										e.printStackTrace();
									}

									Toast.makeText(DaysOfWeekActivity.this, "Данные успешно отправлены!", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void failure(RetrofitError error) {

									if (mProgressDialog.isShowing())
										mProgressDialog.dismiss();
									AlertDialog.Builder builder = new AlertDialog.Builder(DaysOfWeekActivity.this);
									builder.setMessage("Ошибка подключения к сети!");
									builder.setTitle("Подключение");
									builder.setPositiveButton("Повторить отправку", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											sendRouteListsData();
										}
									});
									builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {

										}
									});
									builder.create().show();

							}
						});*/
					}
				//}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public final class SaveOfflineDataListener implements RequestListener<UpdateRouteListDetail> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {

			spiceException.printStackTrace();


			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(DaysOfWeekActivity.this);
			builder.setMessage("Ошибка подключения к сети!");
			builder.setTitle("Подключение");
			builder.setPositiveButton("Повторить отправку", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					sendRouteListsData();
				}
			});
			builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {

				}
			});
			builder.create().show();
		}

		@Override
		public void onRequestSuccess(UpdateRouteListDetail updateRouteListDetail) {
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			try {
				routeListIdsToSendDao.delete(routeListIdsToSendList);
				orderCoordinateDao.delete(orderCoordinates);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Toast.makeText(DaysOfWeekActivity.this, "Данные успешно отправлены!", Toast.LENGTH_SHORT).show();
		}

	}
    
}
