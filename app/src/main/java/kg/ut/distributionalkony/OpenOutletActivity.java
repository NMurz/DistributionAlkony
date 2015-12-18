package kg.ut.distributionalkony;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import kg.ut.distributionalkony.Dto.DiscountDto;
import kg.ut.distributionalkony.Dto.OutletDto;
import kg.ut.distributionalkony.Dto.PriceListElementDto;
import kg.ut.distributionalkony.Dto.RouteListDto;
import kg.ut.distributionalkony.Dto.RouteListElementDto;
import kg.ut.distributionalkony.Dto.RouteListIdsToSend;
import kg.ut.distributionalkony.Dto.StorageDto;
import kg.ut.distributionalkony.Dto.StorageItemDto;
import kg.ut.distributionalkony.Helpers.ApiUrlsHelper;
import kg.ut.distributionalkony.Helpers.GPSTracker;
import kg.ut.distributionalkony.Helpers.HelperFactory;
import kg.ut.distributionalkony.Helpers.PublicData;
import kg.ut.distributionalkony.Helpers.UserStateData;
import kg.ut.distributionalkony.Interfaces.OutletDetailInterface;
import kg.ut.distributionalkony.REST.API;
import kg.ut.distributionalkony.Models.OrderCoordinate;
import kg.ut.distributionalkony.Models.OutletDetail;
import kg.ut.distributionalkony.Models.OutletDetailResponse;
import kg.ut.distributionalkony.Models.Status;
import kg.ut.distributionalkony.Models.UpdateOutletModel;
import kg.ut.distributionalkony.Models.UpdateRouteListDetail;
import kg.ut.distributionalkony.Models.UpdatedElementModel;
import kg.ut.distributionalkony.Models.UserData;
import kg.ut.distributionalkony.REST.Adapter;
import kg.ut.distributionalkony.REST.Requests.SaveDataRequest;
import kg.ut.distributionalkony.REST.RetrofitSpiceService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import org.apache.http.client.HttpClient;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class OpenOutletActivity extends ActionBarActivity implements OutletDetailInterface{

	public final static String DAYOFWEEKID = "DAYOFWEEKID";
	
	UserData user;
	HttpClient httpClient;
	int dayOfWeekIndex;
	int outletId;
	LatLngReceiver latLngReceiver;
	OutletDetail _outletDetail;
	Dao<RouteListDto, Integer> routeListDao;
	Dao<RouteListIdsToSend, Integer> routeListIdsDao;
	Dao<RouteListElementDto, Integer> routeListElementDao;
	Dao<DiscountDto, Integer> discountDao;
	Dao<StorageDto, Integer> storageDao;
	Dao<StorageItemDto, Integer> storageItemDao;
	Dao<OutletDto, Integer> outletDao;
	Dao<PriceListElementDto, Integer> priceListElementDao;
	Dao<OrderCoordinate, Integer> orderCoordinateDao;
	QueryBuilder<RouteListElementDto, Integer> qbRouteListElement;
	List<RouteListElementDto> routeListElements = new ArrayList<RouteListElementDto>();
	List<RouteListElementDto> routeListElementsToSend;
	List<RouteListIdsToSend> routeListIdsToSend;
	List<StorageItemDto> storageItems;
	List<PriceListElementDto> priceListElements;
	List<DiscountDto> discounts;
	DiscountDto discountDto;
	RouteListDto routeListDtos;
	RouteListElementDto routeListElementDto;
	OrderCoordinate orderCoordinate;
	SharedPreferences prefs;
	Calendar calendar;
	GPSTracker gps;
	double discount = 0;
	int routeListId = 0;
	String dateToday;
	String credentials;
	String userId;
	boolean isOffline;
	private SpiceManager spiceManager = new SpiceManager(RetrofitSpiceService.class);
	private SaveDataRequest saveDataRequest;
	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_outlet);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		isOffline = prefs.getBoolean("offlineMode", false);
		credentials = prefs.getString("Credentials", "");
		userId = prefs.getString("UserId", "");


        httpClient = PublicData.getInstance().getHttpClient();
        user = PublicData.getInstance().getUserData();

        Intent intent = getIntent();
        dayOfWeekIndex = Integer.parseInt(intent.getStringExtra(OutletsActivity.DAYOFWEEKID));
        outletId = Integer.parseInt(intent.getStringExtra(OutletsActivity.OUTLETID));

		calendar = Calendar.getInstance();

		gps = new GPSTracker(OpenOutletActivity.this);
		orderCoordinate = new OrderCoordinate();
		orderCoordinate.OutletId = outletId;
		orderCoordinate.LatitudeStart = gps.getLatitude();
		orderCoordinate.LongitudeStart = gps.getLongitude();
		orderCoordinate.ProviderStart = "GPS";
		orderCoordinate.UserId = userId;
		orderCoordinate.DateTimeStart = calendar.getTimeInMillis();

		int today = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		if (today == 0) today = 7;
		if (dayOfWeekIndex < today) {
			calendar.add(Calendar.DATE, (7 - today + dayOfWeekIndex));
		} else if (dayOfWeekIndex > today) {
			calendar.add(Calendar.DATE, dayOfWeekIndex - today);
		}

		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = Calendar.getInstance().getTime();
		dateToday = df.format(date);

		try {
			routeListDao = HelperFactory.getHelper().getRouteListDao();
			routeListElementDao = HelperFactory.getHelper().getRouteListElementDao();
			outletDao = HelperFactory.getHelper().getOutletDao();
			discountDao = HelperFactory.getHelper().getDiscountDao();
			storageDao = HelperFactory.getHelper().getStorageDao();
			storageItemDao = HelperFactory.getHelper().getStorageItemDao();
			priceListElementDao = HelperFactory.getHelper().getPriceListElementDao();
			routeListIdsDao = HelperFactory.getHelper().getRouteListIdsToSendDao();
			orderCoordinateDao = HelperFactory.getHelper().getOrderCoordinateDao();

			//Getting current route list
			QueryBuilder<RouteListDto, Integer> qbRouteList = routeListDao.queryBuilder();
			qbRouteList.where().eq("ListDate", dateToday).and().eq("DayOfWeek", dayOfWeekIndex);
			PreparedQuery<RouteListDto> preparedQuery = qbRouteList.prepare();
			routeListDtos = routeListDao.queryForFirst(preparedQuery);
			qbRouteListElement = routeListElementDao.queryBuilder();
            //getting route list elements by route list and creating routelist if not exists
			if(routeListDtos != null){
				routeListId = routeListDtos.getId();
				qbRouteListElement.where().eq("RouteListId", routeListDtos.Id).and().eq("OutletId", outletId);
				routeListElements = qbRouteListElement.query();
				//routeListElementDao.delete(routeListElements);
			} else {
                //routeListToSendDao = HelperFactory.getHelper().getRouteListToSendDao();
				routeListDtos = new RouteListDto();
				routeListDtos.DayOfWeek = dayOfWeekIndex;
				routeListDtos.ListDate = dateToday;
				routeListDtos.DealerId = prefs.getString("UserId", "");
				try {
					routeListDao.create(routeListDtos);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				routeListId = routeListDtos.getId();
			}

            //getting current outlet
			QueryBuilder<OutletDto, Integer> qb = outletDao.queryBuilder();
			qb.where().eq("Id", outletId);
			OutletDto outletDto = qb.queryForFirst();

            //getting outlet discounts
			QueryBuilder<DiscountDto, Integer> qbDiscount = discountDao.queryBuilder();
			qbDiscount.where().eq("OutletId", outletDto.Id);
			discounts = qbDiscount.query();

            //getting product count in storage
			QueryBuilder<StorageDto, Integer> qbStorage = storageDao.queryBuilder();
			qbStorage.where().eq("DistributorId", outletDto.DistributorId);
			StorageDto storage = qbStorage.queryForFirst();
			storageItems = storageItemDao.queryForEq("StorageId", storage.Id);

            //getting price list elements
			priceListElements = priceListElementDao.queryForAll();
			if(!priceListElements.isEmpty()) {
				ShowOutletDetail(discounts, routeListElements, storageItems, priceListElements, outletDto);
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(OpenOutletActivity.this);
				dialog.setTitle("Прайслист")
						.setMessage("За агентом не закреплены категории продуктов")
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
								System.exit(0);
							}
						});
				dialog.create().show();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void onOutletDetailComplete(OutletDetailResponse outletDetailResponse) {
		if (outletDetailResponse.Status==null || outletDetailResponse.Status.Code==200){    				    				
			List<OutletDetail> outletDetails = outletDetailResponse.DataList;
			if (outletDetails.size()>0){
				Log.e("Alkony",String.valueOf(outletDetails.size()));
				//ShowOutletDetail(outletDetails.get(0));
			} else {
				String message = this.getString(kg.ut.distributionalkony.R.string.message_no_outletroutelist);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				
				Intent newIntent = new Intent(this, MainActivity.class);      	  			
	    		startActivity(newIntent);				
			}
		} else {
			Status status = outletDetailResponse.Status;
			String message = this.getString(kg.ut.distributionalkony.R.string.message_convert_error) +" ("+status.Message+")";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			
			Intent newIntent = new Intent(this, MainActivity.class);      	  			
    		startActivity(newIntent);
		}
		
	}
	
	private void ShowOutletDetail(List<DiscountDto> discounts, List<RouteListElementDto> routeListElements, List<StorageItemDto> storageItems, List<PriceListElementDto> priceListElements, OutletDto outlet){

		String title = this.getString(kg.ut.distributionalkony.R.string.title_activity_open_outlet) + " : " + outlet.Name;
		super.setTitle(title);
		float totalCostValue = 0;
		Collections.sort(priceListElements);

		//Get and show products to order
    	for (int i = 0; i < priceListElements.size() ; i++) {
    		String color = "#ffffff";
    		if (i%2!=0){ color = "#F2F2F2";}

			PriceListElementDto priceListElement = priceListElements.get(i);

            double costPackValue = 0;
            double costSingleValue = 0;
            double discountPack = 0;
            double discountSingle = 0;
            double discountAll = 0;
			int countPackValue = 0;
			int countSingleValue= 0;
			int countInStorage = 0;

			if (!routeListElements.isEmpty()) {
				for (RouteListElementDto routeListElementDto : routeListElements){
					if(priceListElement.Id == routeListElementDto.PriceListElementId) {
						countPackValue = routeListElementDto.CountPack;
						countSingleValue = routeListElementDto.CountSingle;
						break;
					}
				}
			}

			if(!storageItems.isEmpty()){
				for (StorageItemDto storageItemDto : storageItems) {
					if(priceListElement.Id == storageItemDto.ProductId){
						countInStorage = storageItemDto.Quantity;
						costPackValue = storageItemDto.PackPrice;
						costSingleValue = costPackValue / priceListElement.CountInPack;
						break;
					}
				}
			}

			if (!discounts.isEmpty()) {
				for (DiscountDto discountDto : discounts) {
					if (priceListElement.Id == discountDto.PriceListElementId) {
						if (discountDto.Percent != 0) {
							discountPack = (discountDto.Percent * costPackValue) / 100;
							discountSingle = discountPack / priceListElement.CountInPack;
							discountAll = (discountPack * countPackValue) + (discountSingle * countSingleValue);
							discount = discount + discountAll;
						} else if (discountDto.Sum != 0) {
							discountSingle = discountDto.Sum / priceListElement.CountInPack;
							discountAll = (discountDto.Sum * countPackValue) + (discountSingle * countSingleValue);
							discount = discount + discountAll;
						}
					}
				}
			}
                
            //Log.e("Alkony", String.valueOf(costPackValue) + " " + String.valueOf(costSingleValue) + " " + String.valueOf(countPackValue) + " " +String.valueOf(countSingleValue));
            
            //Add new row to table
            int tmargin = dip(this, 2);
            TableLayout productsTable = (TableLayout)findViewById(R.id.ProductsLayout);
            TableRow tr = new TableRow(this);
            LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(tmargin, tmargin, tmargin, tmargin);
            tr.setBackgroundColor(Color.parseColor("#787878"));
            tr.setLayoutParams(lp);

            //Add product name
            TextView productName = new TextView(this);
            TableRow.LayoutParams productNameParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1);
            //productNameParams.setMargins(tmargin, tmargin, tmargin, tmargin);
            productName.setText(priceListElement.Name + " (" + priceListElement.Gross + ")");
            productName.setLayoutParams(productNameParams);
			productName.setGravity(Gravity.CENTER_VERTICAL);
			productName.setPadding(7,0,7,0);
			productName.setBackgroundColor(Color.parseColor(color));

            LayoutParams editParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
            //editParams.setMargins(tmargin, tmargin, tmargin, tmargin);

			LayoutParams editParams2 = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			editParams2.setMargins(tmargin, 0, 0, 0);

			TextView countInStorageView = new TextView(this);
			countInStorageView.setText(String.valueOf(countInStorage));
			countInStorageView.setTextSize(12);
			countInStorageView.setWidth(dip(this, 40));
			countInStorageView.setBackgroundColor(Color.parseColor(color));
			countInStorageView.setGravity(Gravity.CENTER_VERTICAL);
			countInStorageView.setPadding(2,0,2,0);
			countInStorageView.setLayoutParams(editParams);
                
            //Add ordered product pack count
            EditText countPack = new EditText(this);
            countPack.setBackgroundColor(Color.parseColor(color));
            countPack.setInputType(InputType.TYPE_CLASS_NUMBER);
            countPack.setWidth(dip(this, 50));
            countPack.setTextSize(14);
            countPack.setText(String.valueOf(countPackValue));
            countPack.setTag(String.valueOf(costPackValue));
            countPack.setId(priceListElement.Id);
            countPack.setGravity(Gravity.CENTER_VERTICAL);
            countPack.setOnFocusChangeListener(focusChange(countPack));
			countPack.setBackgroundColor(Color.parseColor(color));
            countPack.setLayoutParams(editParams2);
                
            //Add ordered product single count
            EditText countSingle = new EditText(this);
            countSingle.setBackgroundColor(Color.parseColor(color));
            countSingle.setInputType(InputType.TYPE_CLASS_NUMBER);
            countSingle.setWidth(dip(this, 50));
            countSingle.setTextSize(14);
            countSingle.setText(String.valueOf(countSingleValue));
            countSingle.setTag(String.valueOf(costSingleValue));
            countSingle.setId(priceListElement.Id);
            countSingle.setGravity(Gravity.CENTER_VERTICAL);
			countSingle.setBackgroundColor(Color.parseColor(color));
            countSingle.setOnFocusChangeListener(focusChange(countSingle));
            countSingle.setLayoutParams(editParams);
                
            tr.addView(productName);
			tr.addView(countInStorageView);
            tr.addView(countPack);
            tr.addView(countSingle);
            productsTable.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                //Log.d("Name>>>>>>>", currentRouteListElement.getElementsByTagName("Name").item(0).getTextContent());
                //Log.d("CountPack>>>>>>>", currentRouteListElement.getElementsByTagName("CountPack").item(0).getTextContent());
                //Log.d("CountSingle>>>>>>>", currentRouteListElement.getElementsByTagName("CountSingle").item(0).getTextContent());
                
                //Submit total cost
            if(countPackValue != 0) totalCostValue += (costPackValue * countPackValue);
            if(countSingleValue != 0) totalCostValue += (costSingleValue * countSingleValue);
        }            
        
    	//Add cost elements
    	TableLayout productsTable = (TableLayout)findViewById(R.id.ProductsLayout);
    	int tmargin = dip(this, 2);
        TableRow.LayoutParams displayParams = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        displayParams.setMargins(tmargin, tmargin, tmargin, tmargin);    	
    	
        //Total Cost
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(Color.parseColor("#fcfcbc"));
        tr.setLayoutParams(displayParams);        
        
        TextView totalCost = new TextView(this);               
        totalCost.setText(getString(R.string.TotalCost) + " " + String.valueOf(totalCostValue));
        totalCost.setLayoutParams(displayParams);

        tr.addView(totalCost);       
        productsTable.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


		//Discount
		tr = new TableRow(this);
		tr.setBackgroundColor(Color.parseColor("#fcfcbc"));
		tr.setLayoutParams(displayParams);

		TextView moneyDiscount = new TextView(this);
		moneyDiscount.setText(String.format("Скидка в сомах: %.1f", discount));
		moneyDiscount.setLayoutParams(displayParams);

		tr.addView(moneyDiscount);
		productsTable.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        //Total Cost With Discount
        tr = new TableRow(this);
        tr.setBackgroundColor(Color.parseColor("#fcfcbc"));
        tr.setLayoutParams(displayParams);
        
        double costWithDiscountValue = totalCostValue - discount;
        TextView costVsDiscount = new TextView(this);                       
        costVsDiscount.setText(String.format("Итого со скидкой: %.1f", costWithDiscountValue));
        costVsDiscount.setLayoutParams(displayParams);        
        
        tr.addView(costVsDiscount);                
        productsTable.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	public View.OnFocusChangeListener focusChange(final EditText editText)  {
		return new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					if (editText.getText().toString().length()==0){
						editText.setText("0");
					}
					//Update total costs
					float totalCostValue =  0;
					double discount = 0;
					TableLayout tableLayout = (TableLayout)findViewById( R.id.ProductsLayout );
					
					for( int i = 0; i < tableLayout.getChildCount()-3; i++ ) {
					  if( tableLayout.getChildAt(i) instanceof TableRow ) {
					    TableRow currentTableRow =  (TableRow)tableLayout.getChildAt(i);
				    	for (int rowChildIndex = 0; rowChildIndex<currentTableRow.getChildCount(); rowChildIndex++ ){
				    		if( currentTableRow.getChildAt(rowChildIndex) instanceof EditText ){
				    			EditText productEdit = (EditText)currentTableRow.getChildAt(rowChildIndex);
								int countInPack = 0;
								double costPack = 0;
								int countValue = 0;
								for (PriceListElementDto priceListElementDto : priceListElements) {
									if(productEdit.getId() == priceListElementDto.Id) {
										countInPack = priceListElementDto.CountInPack;
										for(StorageItemDto storageItemDto : storageItems){
											if(productEdit.getId() == storageItemDto.ProductId) costPack = storageItemDto.PackPrice;
										}

									}
								}
								if(currentTableRow.getChildCount() - 1 == rowChildIndex + 1) {
									if (!discounts.isEmpty()) {
										for (DiscountDto discountDto : discounts) {
											if (productEdit.getId() == discountDto.PriceListElementId) {
												if (discountDto.Percent != 0) {
													double discountPack = (discountDto.Percent * costPack) / 100;
													if (productEdit.getText().toString().length()!=0){
														countValue = Integer.parseInt(productEdit.getText().toString());
													}
													double discountAll = (discountPack * countValue);
													discount = discount + discountAll;
												} else if (discountDto.Sum != 0) {
													if (productEdit.getText().toString().length()!=0){
														countValue = Integer.parseInt(productEdit.getText().toString());
													}
													double discountAll = (discountDto.Sum * countValue);
													discount = discount + discountAll;
												}
											}
										}
									}
								}

                                if(currentTableRow.getChildCount() == rowChildIndex + 1) {
									if (!discounts.isEmpty()) {
										for (DiscountDto discountDto : discounts) {
											if (productEdit.getId() == discountDto.PriceListElementId) {
												if (discountDto.Percent != 0) {
													double discountPack = (discountDto.Percent * costPack) / 100;
													double discountSingle = discountPack / countInPack;
													if (productEdit.getText().toString().length()!=0){
														countValue = Integer.parseInt(productEdit.getText().toString());
													}
													double discountAll = (discountSingle * countValue);
													discount = discount + discountAll;
												} else if (discountDto.Sum != 0) {
													if (productEdit.getText().toString().length()!=0){
														countValue = Integer.parseInt(productEdit.getText().toString());
													}
													double discountSingle = discountDto.Sum / countInPack;
													double discountAll = (discountSingle * countValue);
													discount = discount + discountAll;
												}
											}
										}
									}
								}

								int count = 0;
				    			if (productEdit.getText().toString().length()!=0){
				    				count = Integer.parseInt(productEdit.getText().toString());
				    			}
				    			totalCostValue = totalCostValue + count*Float.valueOf(productEdit.getTag().toString());
				    		}
				    	}
					  }
					}

					//Update total cost
	    			TableRow totalCostTableRow =  (TableRow)tableLayout.getChildAt(tableLayout.getChildCount()-3);
	    			TextView totalCostView = (TextView)totalCostTableRow.getChildAt(0);
	    			totalCostView.setText(String.format("Итого: %.1f", totalCostValue));

					//float costWithDiscountValue = totalCostValue - ((float)discount*totalCostValue)/100;

					TableRow discountMoneyRow = (TableRow) tableLayout.getChildAt(tableLayout.getChildCount()-2);
					TextView discountMoneyView = (TextView) discountMoneyRow.getChildAt(0);
					discountMoneyView.setText(String.format("Скидка в сомах: %.1f", discount));

	    			//Update total cost with discount
	    			TableRow costWithDiscoutTableRow =  (TableRow)tableLayout.getChildAt(tableLayout.getChildCount()-1);
	    			TextView costWithDiscountView = (TextView)costWithDiscoutTableRow.getChildAt(0);
	    			costWithDiscountView.setText(String.format("Итого со скидкой: %.1f", totalCostValue - discount));
				} else {
					if (editText.getText().toString().equals("0")){
						editText.setText("");
					}
				}
			}
		};
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

	public static int dip(Context context, int pixels) {
		   float scale = context.getResources().getDisplayMetrics().density;
		   return (int) (pixels * scale + 0.5f);
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

	public void FinishSession(View view){

		Calendar cal = Calendar.getInstance();

		orderCoordinate.LatitudeFinish = gps.getLatitude();
		orderCoordinate.LongitudeFinish = gps.getLongitude();
		orderCoordinate.DateTimeFinish = cal.getTimeInMillis();
		orderCoordinate.ProviderFinish = "GPS";


			if(isOffline) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Заказ добавлен в локальную базу.");
				builder.setTitle("Данные");
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						try {
							orderCoordinateDao.create(orderCoordinate);
							QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
							qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
							RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
							if (routeId == null) {
								RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
								routeIdTmp.RouteListId = routeListId;
								routeIdTmp.OutletId = outletId;
								routeListIdsDao.create(routeIdTmp);
							} else {
								routeId.RouteListId = routeListId;
								routeId.OutletId = outletId;
								routeListIdsDao.update(routeId);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
				/*if (!PublicData.getInstance().isTimerRunning()) {
					mTimer = new Timer();
					sendDataTimerTask = new SendDataTimerTask();
					mTimer.schedule(sendDataTimerTask, 1000, 5000);
					PublicData.getInstance().setTimerRunning(true);
				}*/

						finish();
					}
				});
				builder.create().show();
			} else {
				if(isOnline()) {
					SendFinishSession();
				} else {
					DataAddedAlert();
				}
		}
	}

	public void SendFinishSession(){

		List<RouteListDto> routeListDtoList = new ArrayList<RouteListDto>();
		routeListDtoList.add(routeListDtos);
		List<OrderCoordinate> orderCoordinateList = new ArrayList<OrderCoordinate>();
		orderCoordinateList.add(orderCoordinate);
		List<RouteListElementDto> routeListElementDtoList = new ArrayList<RouteListElementDto>();

		UpdateRouteListDetail updateDetail = new UpdateRouteListDetail(routeListDtoList, routeListElementDtoList, orderCoordinateList);

		final ProgressDialog mProgressDialog = new ProgressDialog(OpenOutletActivity.this);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setMessage("Отправка данных. Пожалуйста подождите.");
		mProgressDialog.show();

		saveDataRequest = new SaveDataRequest("Basic " + credentials, updateDetail);
		spiceManager.execute(saveDataRequest, "saveDataRequest", DurationInMillis.ONE_MINUTE, new SaveDataRequestListener());

		/*API api = Adapter.RestAdapter().create(API.class);
		api.saveRouteListDetails("Basic " + credentials, updateDetail, new Callback<UpdateRouteListDetail>() {
			@Override
			public void success(UpdateRouteListDetail updateRouteListDetail, Response response) {
				if (response.getStatus() == 200)
					if (mProgressDialog.isShowing())
						mProgressDialog.dismiss();

					Toast.makeText(OpenOutletActivity.this, "Данные успешно отправлены!", Toast.LENGTH_SHORT).show();
					finish();
			}

			@Override
			public void failure(RetrofitError error) {
				if (error.isNetworkError()) {
					if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(OpenOutletActivity.this);
					builder.setMessage("Ошибка подключения к сети! При отмене данные будут сохранены в локальную базу");
					builder.setTitle("Подключение");
					builder.setCancelable(false);
					builder.setPositiveButton("Повторить отправку", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

							SendFinishSession();
						}
					});
					builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							try {
								orderCoordinateDao.create(orderCoordinate);
								QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
								qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
								RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
								if (routeId == null) {
									RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
									routeIdTmp.RouteListId = routeListId;
									routeIdTmp.OutletId = outletId;
									routeListIdsDao.create(routeIdTmp);
								} else {
									routeId.RouteListId = routeListId;
									routeId.OutletId = outletId;
									routeListIdsDao.update(routeId);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
							//Toast.makeText(OpenOutletActivity.this, "Данные сохранены в локальной базе!", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
					builder.create().show();
				}
			}
		});*/

	}
	
	public void ConfirmClick(View view){
		TableLayout tableLayout = (TableLayout)findViewById( R.id.ProductsLayout );
        UpdateOutletModel updateOutletModel = new UpdateOutletModel();
        updateOutletModel.Latitude = PublicData.getInstance().GetLatitude();
        updateOutletModel.Longitude = PublicData.getInstance().GetLongitude();
        updateOutletModel.OutletId = outletId;

        updateOutletModel.UpdatedElementItems = new ArrayList<UpdatedElementModel>();
		routeListElementsToSend = new ArrayList<RouteListElementDto>();

		for( int i = 0; i < tableLayout.getChildCount()-3; i++ ){
		  if( tableLayout.getChildAt(i) instanceof TableRow ){
		    TableRow currentTableRow = (TableRow)tableLayout.getChildAt(i);
		    boolean ifPack = true;
		    boolean isFindeEdits = false;
		    EditText countPackEdit = new EditText(this);
		    EditText countSingleEdit = new EditText(this);
	    	for (int rowChildIndex = 0; rowChildIndex<currentTableRow.getChildCount(); rowChildIndex++ ){
	    		if( currentTableRow.getChildAt(rowChildIndex) instanceof EditText ){
	    			if (ifPack){
	    				countPackEdit = (EditText)currentTableRow.getChildAt(rowChildIndex);
	    				ifPack = false;
	    			} else {
	    				countSingleEdit = (EditText)currentTableRow.getChildAt(rowChildIndex);
	    			}	    	
	    			isFindeEdits = true;
	    		}
	    	}
	    	if (isFindeEdits){
    			
		        int countPackValue = 0;
		        int countSingleValue = 0;
		        if (countPackEdit.getText().toString().length()!=0){
		        	countPackValue = Integer.parseInt(countPackEdit.getText().toString());
		        }
		        if (countSingleEdit.getText().toString().length()!=0){
		        	countSingleValue = Integer.parseInt(countSingleEdit.getText().toString());
		        }

				if(countPackValue > 0 || countSingleValue > 0){
					routeListElementDto = new RouteListElementDto();
					routeListElementDto.PriceListElementId = countPackEdit.getId();
					routeListElementDto.CountPack = countPackValue;
					routeListElementDto.CountSingle = countSingleValue;
					routeListElementDto.OutletId = outletId;
					routeListElementDto.RouteListId = routeListId;

					routeListElementsToSend.add(routeListElementDto);
				}
	    	}
		  }
		}
		if(routeListElements.isEmpty()){
			for (RouteListElementDto routeListElement : routeListElementsToSend){
				try {
					routeListElementDao.create(routeListElement);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if(!routeListElements.isEmpty()) {
			try {
				routeListElementDao.delete(routeListElements);
				for (RouteListElementDto routeListElement : routeListElementsToSend){
					routeListElementDao.create(routeListElement);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Calendar cal = Calendar.getInstance();

		orderCoordinate.LatitudeFinish = gps.getLatitude();
		orderCoordinate.LongitudeFinish = gps.getLongitude();
		orderCoordinate.DateTimeFinish = cal.getTimeInMillis();
		orderCoordinate.ProviderFinish = "GPS";


			if(isOffline) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Заказ добавлен в локальную базу.");
				builder.setTitle("Данные");
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						try {
							orderCoordinateDao.create(orderCoordinate);
							QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
							qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
							RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
							if (routeId == null) {
								RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
								routeIdTmp.RouteListId = routeListId;
								routeIdTmp.OutletId = outletId;
								routeListIdsDao.create(routeIdTmp);
							} else {
								routeId.RouteListId = routeListId;
								routeId.OutletId = outletId;
								routeListIdsDao.update(routeId);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
				/*if (!PublicData.getInstance().isTimerRunning()) {
					mTimer = new Timer();
					sendDataTimerTask = new SendDataTimerTask();
					mTimer.schedule(sendDataTimerTask, 1000, 5000);
					PublicData.getInstance().setTimerRunning(true);
				}*/

						finish();
					}
				});
				builder.create().show();
			} else {
				if(isOnline()){
					SendDataOnline();
				} else {
					DataAddedAlert();
				}
        	}
	}

	public void DataAddedAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Отсутствует подключение к интернету! Данные добавлены в локальную базу.");
		builder.setTitle("Подключение");
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				try {
					orderCoordinateDao.create(orderCoordinate);
					QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
					qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
					RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
					if (routeId == null) {
						RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
						routeIdTmp.RouteListId = routeListId;
						routeIdTmp.OutletId = outletId;
						routeListIdsDao.create(routeIdTmp);
					} else {
						routeId.RouteListId = routeListId;
						routeId.OutletId = outletId;
						routeListIdsDao.update(routeId);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				/*if (!PublicData.getInstance().isTimerRunning()) {
					mTimer = new Timer();
					sendDataTimerTask = new SendDataTimerTask();
					mTimer.schedule(sendDataTimerTask, 1000, 5000);
					PublicData.getInstance().setTimerRunning(true);
				}*/

				finish();
			}
		});
		builder.create().show();
	}

	public void SendDataOnline() {

		List<RouteListDto> routeListDtoList = new ArrayList<RouteListDto>();
		routeListDtoList.add(routeListDtos);
		List<OrderCoordinate> orderCoordinateList = new ArrayList<OrderCoordinate>();
		orderCoordinateList.add(orderCoordinate);

			UpdateRouteListDetail updateDetail = new UpdateRouteListDetail(routeListDtoList, routeListElementsToSend, orderCoordinateList);


			mProgressDialog = new ProgressDialog(OpenOutletActivity.this);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("Отправка данных. Пожалуйста подождите.");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();

		saveDataRequest = new SaveDataRequest("Basic " + credentials, updateDetail);
		spiceManager.execute(saveDataRequest, "saveDataRequest", DurationInMillis.ONE_MINUTE, new SaveDataRequestListener());

			/*API api = Adapter.RestAdapter().create(API.class);
			api.saveRouteListDetails("Basic " + credentials, updateDetail, new Callback<UpdateRouteListDetail>() {
				@Override
				public void success(UpdateRouteListDetail updateRouteListDetail, Response response) {
						if (mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						Toast.makeText(OpenOutletActivity.this, "Данные успешно отправлены!", Toast.LENGTH_SHORT).show();
						finish();
				}

				@Override
				public void failure(RetrofitError error) {
						if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
						AlertDialog.Builder builder = new AlertDialog.Builder(OpenOutletActivity.this);
						builder.setMessage("Ошибка подключения к сети! При отмене данные будут сохранены в локальную базу");
						builder.setTitle("Подключение");
						builder.setCancelable(false);
						builder.setPositiveButton("Повторить отправку", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								SendDataOnline();
							}
						});
						builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								try {
									orderCoordinateDao.create(orderCoordinate);
									QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
									qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
									RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
									if (routeId == null) {
										RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
										routeIdTmp.RouteListId = routeListId;
										routeIdTmp.OutletId = outletId;
										routeListIdsDao.create(routeIdTmp);
									} else {
										routeId.RouteListId = routeListId;
										routeId.OutletId = outletId;
										routeListIdsDao.update(routeId);
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								//Toast.makeText(OpenOutletActivity.this, "Данные сохранены в локальной базе!", Toast.LENGTH_SHORT).show();
								finish();
							}
						});
						builder.create().show();
				}
			});*/

		/*}
			}catch (SQLException e) {
				e.printStackTrace();
			}*/
	}

	public final class SaveDataRequestListener implements RequestListener<UpdateRouteListDetail> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			Log.e("Exception Message",spiceException.getMessage());
			Log.e("Cause", spiceException.toString());
			spiceException.printStackTrace();
			if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(OpenOutletActivity.this);
			builder.setMessage("Ошибка подключения к сети! При отмене данные будут сохранены в локальную базу");
			builder.setTitle("Подключение");
			builder.setCancelable(false);
			builder.setPositiveButton("Повторить отправку", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {

					SendDataOnline();
				}
			});
			builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					try {
						orderCoordinateDao.create(orderCoordinate);
						QueryBuilder<RouteListIdsToSend, Integer> qbRouteIds = routeListIdsDao.queryBuilder();
						qbRouteIds.where().eq("RouteListId", routeListId).and().eq("OutletId", outletId);
						RouteListIdsToSend routeId = qbRouteIds.queryForFirst();
						if (routeId == null) {
							RouteListIdsToSend routeIdTmp = new RouteListIdsToSend();
							routeIdTmp.RouteListId = routeListId;
							routeIdTmp.OutletId = outletId;
							routeListIdsDao.create(routeIdTmp);
						} else {
							routeId.RouteListId = routeListId;
							routeId.OutletId = outletId;
							routeListIdsDao.update(routeId);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					Toast.makeText(OpenOutletActivity.this, "Данные сохранены в локальной базе!", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
			builder.create().show();
		}

		@Override
		public void onRequestSuccess(UpdateRouteListDetail updateRouteListDetail) {
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			Toast.makeText(OpenOutletActivity.this, "Данные успешно отправлены!", Toast.LENGTH_SHORT).show();
			finish();
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
}
