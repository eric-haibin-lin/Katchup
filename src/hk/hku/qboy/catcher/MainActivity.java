package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ListActivity {
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);
		
		setListAdapter();
		addTaskButtonListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		Intent timerIntent = new Intent(this, TimerActivity.class);
		timerIntent.putExtra("title", currentTitle);
		startActivity(timerIntent);
	}

}
