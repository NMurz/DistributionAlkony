package kg.ut.distributionalkony;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import kg.ut.distributionalkony.Dto.OutletDto;
import kg.ut.distributionalkony.Dto.OutletVisit;
import kg.ut.distributionalkony.Helpers.ApiUrlsHelper;
import kg.ut.distributionalkony.Helpers.HelperFactory;
import kg.ut.distributionalkony.Helpers.PublicData;
import kg.ut.distributionalkony.Interfaces.OutletsInterface;
import kg.ut.distributionalkony.Models.DayOfWeek;
import kg.ut.distributionalkony.Models.DayOfWeekResponse;
import kg.ut.distributionalkony.Models.Outlet;
import kg.ut.distributionalkony.Models.OutletResponse;
import kg.ut.distributionalkony.Models.Status;
import kg.ut.distributionalkony.Models.UserData;
import kg.ut.distributionalkony.Repositories.DayOfWeekRepository;
import kg.ut.distributionalkony.Repositories.OutletsRepository;

import org.apache.http.client.HttpClient;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

public class OutletsActivity extends ActionBarActivity implements OutletsInterface {

	public final static String DAYOFWEEKID = "DAYOFWEEKID";
	public final static String OUTLETID = "OUTLETID";
	
	UserData user;
	HttpClient httpClient;
	String dayOfWeekIndex;
	SharedPreferences prefs;
	LatLngReceiver latLngReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outlets);
		
        httpClient = PublicData.getInstance().getHttpClient();
        user = PublicData.getInstance().getUserData();
        
        Intent intent = getIntent();
        dayOfWeekIndex = intent.getStringExtra(DaysOfWeekActivity.DAYOFWEEKID);
        
//        latLngReceiver = new LatLngReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(GpsLocationService.UPDATE_LATLNG);
//        registerReceiver(latLngReceiver, intentFilter);
        
        //String url =  ApiUrlsHelper.OutletsByDayUrl.replace("{currentDay}", dayOfWeekIndex);
        //OutletsRepository _outletsRepository = new OutletsRepository(httpClient, url , this, this, getResources());
       // _outletsRepository.GetAll();

		try {
			Dao<OutletDto, Integer> outletDao = HelperFactory.getHelper().getOutletDao();
			Dao<OutletVisit, Integer> outletVisitDao = HelperFactory.getHelper().getOutletVisitDao();
			List<OutletVisit> outletVisits = outletVisitDao.queryForEq("DayOfWeek", dayOfWeekIndex);
			List<OutletDto> outlets = new ArrayList<OutletDto>();
			QueryBuilder<OutletDto, Integer> qb = outletDao.queryBuilder();
			for (OutletVisit outletVisit: outletVisits) {
				qb.where().eq("Id", outletVisit.OutletId);
				OutletDto outlet = qb.queryForFirst();
				outlets.add(outlet);
			}
			//List<OutletDto> outlets = outletDao.queryForEq("DayOfWeek", dayOfWeekIndex);
			if(!outlets.isEmpty()) {
				ShowOutlets(outlets);
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(OutletsActivity.this);
				dialog.setTitle("Торговые точки")
						.setMessage("Торговые точки не закреплены за агентом")
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
								System.exit(0);
							}
						});
				dialog.create().show();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

	public void onOutletsComplete(OutletResponse outletResponse){
		if (outletResponse.Status==null || outletResponse.Status.Code==200){    				    				
			List<OutletDto> outlets = outletResponse.DataList;
			if (outlets.size()>0){
				ShowOutlets(outlets);
			} else {
				String message = this.getString(kg.ut.distributionalkony.R.string.message_no_outlets);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				
				Intent newIntent = new Intent(this, MainActivity.class);      	  			
	    		startActivity(newIntent);				
			}
		} else {
			Status status = outletResponse.Status;
			String message = this.getString(kg.ut.distributionalkony.R.string.message_convert_error) +" ("+status.Message+")";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			
			Intent newIntent = new Intent(this, MainActivity.class);      	  			
    		startActivity(newIntent);
		}
	}
	
	private void ShowOutlets(List<OutletDto> outlets){
		LinearLayout rl = (LinearLayout)findViewById(R.id.contentLayout);
		Collections.sort(outlets);
		for (int i = 0; i < outlets.size(); i++) {
			OutletDto outlet = outlets.get(i);

			int k = i + 1;
                        	
            String title = outlet.NumberInRoute + " . " + outlet.Name + " ("+outlet.Address+")";
            
            TextView myButton = new TextView(this);
            
            myButton.setText(title);           
            myButton.setTag(String.valueOf(outlet.Id)); 
            
            myButton.setOnClickListener(getOnClick(myButton));            
            myButton.setBackgroundColor(Color.parseColor(this.getString(kg.ut.distributionalkony.R.color.MenuBackgroundColor)));
            myButton.setTextColor(Color.parseColor(this.getString(kg.ut.distributionalkony.R.color.Menu_Color)));            

            int tmargin = dip(this, 2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.setMargins(tmargin, tmargin, tmargin, tmargin);
            myButton.setLayoutParams(params);
			myButton.setPadding(4,0,0,0);
            myButton.setMinHeight(dip(this,40));
            myButton.setGravity(Gravity.CENTER_VERTICAL);

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
	            final String outletId = button.getTag().toString();
	            
	            //Toast.makeText(OutletsActivity.this, outletId, Toast.LENGTH_LONG).show();
	            Intent intent = new Intent(OutletsActivity.this, OpenOutletActivity.class);
	            intent.putExtra(DAYOFWEEKID, dayOfWeekIndex);
	            intent.putExtra(OUTLETID, outletId);
	            startActivity(intent);
	        }
	    };
	}	
	
	public void ExitClick(){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	Intent intent = new Intent(Intent.ACTION_MAIN);
    	        	intent.addCategory(Intent.CATEGORY_HOME);
    	        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	        	startActivity(intent);            	        	
    	        	System.exit(0);

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(OutletsActivity.this);
    	String message = OutletsActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit);
    	String yes = OutletsActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit_yes);
    	String no = OutletsActivity.this.getString(kg.ut.distributionalkony.R.string.message_exit_no);
    	
    	builder.setMessage(message).setPositiveButton(yes, dialogClickListener)
    	    .setNegativeButton(no, dialogClickListener).show();
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
       	  
          //String lat = String.valueOf(lastKnownLoc.getLatitude());
          //String lng = String.valueOf(lastKnownLoc.getLongitude());
          
       	  //Toast.makeText(OutletsActivity.this, "Triggered by Service!\n" + "Data passed: " + lat + " ; " +lng, Toast.LENGTH_LONG).show();
         }
   	 }
   	 
	}
}
