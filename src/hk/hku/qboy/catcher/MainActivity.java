package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;

	private Button startButton;
	private TextView timerValue;
	private Task task;

	private BroadcastReceiver mReceiver;
	private boolean mIsBound = false;
	public Timer timer;

	public ServiceConnection Scon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			timer = ((Timer.ServiceBinder) binder).getService();
			Log.d("SERVICE", "On bind, get service;");
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
		setContentView(R.layout.main1);

		setListAdapter();
		addTaskButtonListener();
		addTimerButtonListener();
		addFinishButtonListener();
		registerTimerReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void addFinishButtonListener() {
		Button finishButton = (Button) findViewById(R.id.finishButton);
		finishButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				timer.finish();
				timerValue.setText("0:00");
			}
		});
	};

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
					task = new Task(MainActivity.this, title);
					setCurrentTaskText(title);
				}
			}
		};
		this.registerReceiver(mReceiver, intentFilter);
	}

	private void addTimerButtonListener() {
		timerValue = (TextView) findViewById(R.id.timerValue);
		startButton = (Button) findViewById(R.id.startButton);

		Intent newIntent = new Intent(this, Timer.class);

		doBindService(newIntent);
		startService(newIntent);

		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timer.isCountingTime) {
					timer.finish();
					if (task != null) {
						String newTimerRecord = timer.makeRecord();
						task.addTrackRecord(newTimerRecord);
						task.update();
					}
					timerValue.setText("0:00");
				} else {
					if (task != null) {
						timer.start();
					}
				}
			}
		});
	}

	private void addTaskButtonListener() {
		final Button add_button = (Button) findViewById(R.id.button1);
		// Add task button
		View.OnClickListener add_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TaskDetail.class);
				startActivity(intent);
			}
		};
		add_button.setOnClickListener(add_button_on_click_listener);
	}

	private void setListAdapter() {
		Cursor cursor = managedQuery(tasks_provider, null, null, null, null);
		TaskCursorAdapter taskAdapter = new TaskCursorAdapter(this, cursor);
		this.setListAdapter(taskAdapter);
	}

	public void taskStart(String currentTitle) {
		setCurrentTaskText(currentTitle);
		task = new Task(this, currentTitle);
		timer.setTitle(currentTitle);

	}

	private void setCurrentTaskText(String currentTitle) {
		TextView taskText = (TextView) findViewById(R.id.taskText);
		taskText.setText(currentTitle);
	}

}
