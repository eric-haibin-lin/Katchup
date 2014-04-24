package hk.hku.qboy.catcher;

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
import android.widget.TextView;

public class TimerActivity extends Activity {
	private Button startButton;
	private TextView timerValue;
	private Task task;

	String title;
	private BroadcastReceiver mReceiver;
	private boolean mIsBound = false;
	public Timer timer;

	public ServiceConnection Scon = new ServiceConnection() {
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
		addTimerButtonListener();
		registerTimerReceiver();
		// addFinishButtonListener();
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
					timer.finish();
					if (task != null) {
						String newTimerRecord = timer.makeRecord();
						task.addTrackRecord(newTimerRecord);
						task.update();
					}
					Intent i = new Intent(TimerActivity.this,
							MainActivity.class);
					startActivity(i);

				} else {
					if (task != null) {
						timer.start();
					}
				}
			}
		});
	}

	private void registerTimerReceiver() {
		IntentFilter intentFilter = new IntentFilter(
				"android.intent.action.TIMER");
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
	//
	// private void addFinishButtonListener() {
	// Button finishButton = (Button) findViewById(R.id.finishButton);
	// finishButton.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View view) {
	// timer.finish();
	// timerValue.setText("0:00");
	// }
	// });
	// };

}
