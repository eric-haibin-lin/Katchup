package hk.hku.qboy.catcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends Activity {
	private TextView timerValue;
	private Task task;

	int id;
	String title;
	private BroadcastReceiver mReceiver;
	private boolean mIsBound = false;
	public Timer timer;
	IntentFilter intentFilter;
	public ServiceConnection Scon;

	boolean wifiPreference;
	boolean dataPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		readPreference();
		createConnection();
		addTimerButtonListener();
		registerTimerReceiver();
		Intent i = getIntent();
		title = i.getStringExtra("title");
		id = i.getIntExtra("id_key", 0);
		Log.d("Timer Activity", "title is " + title);
		setCurrentTaskText(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timer, menu);
		getActionBar().setTitle("Timer");
		return true;
	}

	private void readPreference() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String wifiPreferenceStr = sharedPreferences.getString(
				getString(R.string.preference_wifi), "false");
		String dataPreferenceStr = sharedPreferences.getString(
				getString(R.string.preference_data), "false");
		if (wifiPreferenceStr.equals("true"))
			wifiPreference = true;
		else
			wifiPreference = false;
		if (dataPreferenceStr.equals("true"))
			dataPreference = true;
		else
			dataPreference = false;
	}

	// create a connection with the service binder
	void createConnection() {
		Scon = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				timer = ((Timer.ServiceBinder) binder).getService();
				Log.d("SERVICE", "On bind, get service;");
				timer.setTitle(title);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				timer = null;
			}
		};
	}

	// bind service with current activity
	void doBindService(Intent i) {
		Log.d("BIND", "doBind");
		bindService(i, Scon, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}

	private void addTimerButtonListener() {
		timerValue = (TextView) findViewById(R.id.timerValue);
		timerValue.setBackgroundResource(R.drawable.red_time);

		Intent timerService = new Intent(this, Timer.class);
		doBindService(timerService);
		startService(timerService);

		timerValue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timer.isCountingTime) {
					destroyTimer();
					finish();
				} else {
					timerValue.setBackgroundResource(R.drawable.green_time);
					task = new Task(TimerActivity.this, id);
					timer.start();
					checkSettings();
				}
			}
		});
	}

	private void registerTimerReceiver() {
		intentFilter = new IntentFilter("android.intent.action.TIMER");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String timeToDisplay = intent.getStringExtra("time");
				String title = intent.getStringExtra("title");
				if (timerValue != null)
					timerValue.setText(timeToDisplay);
				if (task == null) {
					task = new Task(TimerActivity.this, id);
					setCurrentTaskText(title);
				}
			}
		};
		this.registerReceiver(mReceiver, intentFilter);
	}

	private void setCurrentTaskText(String currentTitle) {
		TextView taskText = (TextView) findViewById(R.id.taskText);
		taskText.setText(currentTitle);
	}

	private void checkSettings() {
		if (wifiPreference) {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}
		if (dataPreference) {
			setMobileDataEnabled(false);
		}
	}

	private void resetSettings() {
		if (wifiPreference) {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}
		if (dataPreference) {
			setMobileDataEnabled(true);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setMobileDataEnabled(boolean enabled) {
		final ConnectivityManager conman = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			final Class conmanClass = Class
					.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass
					.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField
					.get(conman);
			final Class iConnectivityManagerClass = Class
					.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void destroyTimer() {
		this.unregisterReceiver(mReceiver);
		String newTimerRecord = timer.makeRecord();
		timer.finish();
		if (task != null) {
			task.addTrackRecord(newTimerRecord);
			task.update();
		}
		doUnbindService();
		resetSettings();
		Log.d("TIMER", "destroyTimer");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		destroyTimer();
		this.finish();
	}

}
