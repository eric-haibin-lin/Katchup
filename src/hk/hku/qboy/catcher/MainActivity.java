package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;

	private Button startButton;
	private TextView timerValue;
	private Timer timer;
	private Task task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);

		setListAdapter();
		addTaskButtonListener();
		addTimerButtonListener();
		addFinishButtonListener();
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

	private void addTimerButtonListener() {
		timerValue = (TextView) findViewById(R.id.timerValue);
		startButton = (Button) findViewById(R.id.startButton);
		timer = new Timer(timerValue);
		timer.createRunnable();

		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timer.isCountingTime) {
					timer.finish();
					if (task != null) {
						String newTimerRecord = timer.makeRecord();
						task.addTrackRecord(newTimerRecord);
						task.update();
					}
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
		TextView taskText = (TextView) findViewById(R.id.taskText);
		taskText.setText(currentTitle);
		task = new Task(this, currentTitle);

	}

}
