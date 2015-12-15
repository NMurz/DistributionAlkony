package kg.ut.distributionalkony;

import kg.ut.distributionalkony.Helpers.ApiUrlsHelper;
import kg.ut.distributionalkony.Helpers.PublicData;
import kg.ut.distributionalkony.Helpers.UserStateData;
import kg.ut.distributionalkony.Interfaces.LogInInterface;
import kg.ut.distributionalkony.Models.LogInResponse;
import kg.ut.distributionalkony.Models.LogInResult;
import kg.ut.distributionalkony.Models.Status;
import kg.ut.distributionalkony.Models.UserData;
import kg.ut.distributionalkony.Models.UserLogOn;
import kg.ut.distributionalkony.REST.API;
import kg.ut.distributionalkony.REST.Adapter;
import kg.ut.distributionalkony.Repositories.LogInRepository;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	Menu menu;
	public Animation anim;
	public String _userName = "";
	public String _password ="";
	EditText userName;
	EditText password;
	Button login;
	HttpClient httpClient = new DefaultHttpClient();
	ImageView image;
	SharedPreferences prefs;
	boolean isAuthenticated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {   	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
               
        image = (ImageView) findViewById(R.id.Logo);
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
                
        Thread t = new Thread(new Runnable() {
            public void run() {
                image.startAnimation(anim);
            }
          });
          t.start();
		userName = (EditText)findViewById(R.id.userName);
		password = (EditText)findViewById(R.id.password);
		login = (Button) findViewById(R.id.LogIn);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logIn();
			}
		});
    }

    public void logIn(){
        PublicData.getInstance();
        UserStateData.getInstance();
           	

        
        _userName = userName.getText().toString();
        _password = password.getText().toString();
		UserLogOn userLogOnData = new UserLogOn();
		userLogOnData.Password = _password;
		userLogOnData.UserName = _userName;
        
        //LogInRepository _logInRepository = new LogInRepository(httpClient, ApiUrlsHelper.LogOnUrl, this, this, getResources());
        //_logInRepository.LogIn(_userName, _password);

		API api = Adapter.RestAdapter().create(API.class);
		api.logOn(userLogOnData, new Callback<LogInResponse>() {
			@Override
			public void success(LogInResponse logInResponse, Response response) {
				Status status = logInResponse.Status;

				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("Вход");
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
				if (status != null && (status.Code != 201)) {
					if (status != null && (status.Code == 402)) {
						//Toast.makeText(MainActivity.this, "Неверное имя пользователя или пароль! Проверте вводимые данные и попробуйте еще раз!", Toast.LENGTH_SHORT).show();
						dialog.setMessage("Неверное имя пользователя или пароль! Проверте вводимые данные и попробуйте еще раз!");
						dialog.create().show();
					} else {
						//Toast.makeText(MainActivity.this, "Ошибка входа в систему! Проверте вводимые данные и попробуйте еще раз!", Toast.LENGTH_SHORT).show();
						dialog.setMessage("Ошибка входа в систему! Проверте вводимые данные и попробуйте еще раз!");
						dialog.create().show();
					}
				} else {
					UserData user = logInResponse.DataList.get(0);
					if (user != null) {
						if (user.Roles.contains("Dealer")) {


							//String message = this.getString(kg.ut.distributionalkony.R.string.message_login_success);
							Toast.makeText(MainActivity.this, user.UserName + ", Добро пожаловать!", Toast.LENGTH_SHORT).show();

							UserStateData.getInstance().SetUserName(_userName);
							UserStateData.getInstance().SetUserPassword(_password);
							UserStateData.getInstance().setUserId(user.UserId);
							UserStateData.getInstance().InitCredentials(_userName, _password);

							isAuthenticated = true;
							prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
							SharedPreferences.Editor ed = prefs.edit();
							ed.putBoolean("isAuthenticated", isAuthenticated);
							ed.putBoolean("offlineMode", false);
							ed.putString("Credentials", UserStateData.getInstance().GetCredentials());
							ed.putString("UserId", user.UserId);
							ed.commit();

							PublicData.getInstance().setHttpClient(httpClient);
							PublicData.getInstance().setUserData(user);

							Intent intent = new Intent(MainActivity.this, DaysOfWeekActivity.class);
							startActivity(intent);
							finish();
						} else {
							//String message = this.getString(kg.ut.distributionalkony.R.string.message_login_permissionerror);
							Toast.makeText(MainActivity.this, user.UserName + ", У Вас недостаточно прав доступа!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			@Override
			public void failure(RetrofitError error) {
				if(error.isNetworkError()){
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("Подключение");
					dialog.setMessage("Отсутствует подключение к сети! Повторить запрос?");
					dialog.setCancelable(false);
					dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							logIn();
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

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//    	this.menu = menu;
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        MenuItem item_down = menu.findItem(R.id.action_exit);
//        item_down.setVisible(false);
//
//        return true;
//    }
    

    public void onLogInComplete(LogInResult logInResult){
    	
    	LogInResponse logInResponse = logInResult.LogInResponse;
		Status status = logInResponse.Status;			
		httpClient = logInResult.HttpClient;


		
		if (status!=null && (status.Code!=201)){
			if (status!=null && (status.Code==402)){
				String message = this.getString(kg.ut.distributionalkony.R.string.message_login_password_failed);		
	    		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			} else {
				String message = this.getString(kg.ut.distributionalkony.R.string.message_login_error);		
	    		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			}
		} else {
			UserData user = logInResponse.DataList.get(0);
			if (user!=null){
				if (user.Roles.contains("Dealer")){


					String message = this.getString(kg.ut.distributionalkony.R.string.message_login_success);
					Toast.makeText(this, user.UserName + ", " + message, Toast.LENGTH_SHORT).show();
					
					UserStateData.getInstance().SetUserName(_userName);
					UserStateData.getInstance().SetUserPassword(_password);
					UserStateData.getInstance().setUserId(user.UserId);
					UserStateData.getInstance().InitCredentials(_userName, _password);

					isAuthenticated = true;
					prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor ed = prefs.edit();
					ed.putBoolean("isAuthenticated", isAuthenticated);
					ed.putString("Credentials", UserStateData.getInstance().GetCredentials());
					ed.putString("UserId", UserStateData.getInstance().getUserId());
					ed.commit();

					PublicData.getInstance().setHttpClient(httpClient);
					PublicData.getInstance().setUserData(user);
					
			        Intent intent = new Intent(this, DaysOfWeekActivity.class);
			        startActivity(intent);
					finish();
				} else {
					String message = this.getString(kg.ut.distributionalkony.R.string.message_login_permissionerror);
					Toast.makeText(this, user.UserName + ", " + message, Toast.LENGTH_SHORT).show();
				}
			}
		}
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
                
        return super.onOptionsItemSelected(item);
    }
    
}
