package hk.hku.qboy.catcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TimerActivity extends Activity {
	private Button startButton;
	private TextView timerValue;
	private Task task;

	String title;
	private BroadcastReceiver mReceiver;
	private boolean mIsBound = false;
	public Timer timer;
	IntentFilter intentFilter;
	public ServiceConnection Scon;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);

		createConnection();
		addTimerButtonListener();
		registerTimerReceiver();
		Intent i = getIntent();
		title = i.getStringExtra("title");
		Log.d("Timer Activity", "title is " + title);
		setCurrentTaskText(title);
		task = new Task(this, title);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}

	private void addTimerButtonListener() {
		timerValue = (TextView) findViewById(R.id.timerValue);
		startButton = (Button) findViewById(R.id.startButton);

		Intent timerService = new Intent(this, Timer.class);
		doBindService(timerService);
		startService(timerService);

		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timer.isCountingTime) {
					finish();
				} else {
					if (task != null) {
						timer.start();
						checkSettings();
					}
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
					task = new Task(TimerActivity.this, title);
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
		final CheckBox wifiBox = (CheckBox) findViewById(R.id.wifiBox);
		final CheckBox cellularBox = (CheckBox) findViewById(R.id.cellularBox);

		if (wifiBox.isChecked()) {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}

		if (cellularBox.isChecked()) {
			setMobileDataEnabled(false);
		}

	}

	private void resetSettings() {
		final CheckBox wifiBox = (CheckBox) findViewById(R.id.wifiBox);
		final CheckBox cellularBox = (CheckBox) findViewById(R.id.cellularBox);

		if (wifiBox.isChecked()) {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}
		if (cellularBox.isChecked()) {
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
		timer.finish();
		if (task != null) {
			String newTimerRecord = timer.makeRecord();
			task.addTrackRecord(newTimerRecord);
			task.update();
		}
		doUnbindService();
		resetSettings();
	}

}
