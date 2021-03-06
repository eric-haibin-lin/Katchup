package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingActivity extends Activity {

	long totalSec = 0;
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;

	CheckBox wifiBox;
	CheckBox cellularBox;
	TextView totalText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViews();
		readPreference();
		addConfirmListener();
		addCancelListener();
		countTotalTimeTracked();
		totalText.setText("Total Katchup: " + getTotalTime(totalSec));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting, menu);
		getActionBar().setTitle("Setting");
//		View v = getActionBar().getCustomView();
//		TextView titleTxtView = (TextView) v.findViewById(R.id.actionbarTitle);
//	    titleTxtView.setText(title);
//		MenuItem menuItem = menu.findItem(R.id.title);
//		menuItem.setTitle("Setting");
		return true;
	}

	private void readPreference() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String wifiPreference = sharedPreferences.getString(
				getString(R.string.preference_wifi), "false");
		String dataPreference = sharedPreferences.getString(
				getString(R.string.preference_data), "false");
		if (wifiPreference.equals("true")) {
			wifiBox.setChecked(true);
			Log.d("PREFERENCE", "Wifi Read : True");
		} else {
			wifiBox.setChecked(false);
			Log.d("PREFERENCE", "Wifi Read : false");
		}
		if (dataPreference.equals("true"))
			cellularBox.setChecked(true);
		else
			cellularBox.setChecked(false);
	}

	private void findViews() {
		wifiBox = (CheckBox) findViewById(R.id.wifiBox);
		cellularBox = (CheckBox) findViewById(R.id.cellularBox);
		totalText = (TextView) findViewById(R.id.totalTime);
	}

	private void addConfirmListener() {
		Button confirm_btn = (Button) findViewById(R.id.confirm_btn);

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if (wifiBox.isChecked()) {
					editor.putString(getString(R.string.preference_wifi),
							"true");
					Log.d("PREFERENCE", "Write : True ");

				} else
					editor.putString(getString(R.string.preference_wifi),
							"false");
				if (cellularBox.isChecked())
					editor.putString(getString(R.string.preference_data),
							"true");
				else
					editor.putString(getString(R.string.preference_data),
							"false");
				editor.commit();
				finish();
			}
		};
		confirm_btn.setOnClickListener(listener);
	}

	private void addCancelListener() {
		Button cancel_btn = (Button) findViewById(R.id.cancel_btn);
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		};
		cancel_btn.setOnClickListener(listener);
	}

	private void countTotalTimeTracked() {

		Cursor cursor = getContentResolver().query(tasks_provider, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			// iterate over rows
			for (int i = 0; i < cursor.getCount(); i++) {

				int id = cursor.getInt(cursor.getColumnIndex(TaskProvider._ID));
				Task task = new Task(SettingActivity.this, id);
				totalSec += task.getTotalSec();
				cursor.moveToNext();
			}
			cursor.close();
		}
	}

	private String getTotalTime(long time) {
		long totalMin = time / 60;
		String totalTime = "";
		if (totalMin >= 60) {
			long totalHour = totalMin / 60;
			long remainingMin = totalMin - totalHour * 60;
			totalTime = String.valueOf(totalHour) + " Hour "
					+ String.valueOf(remainingMin) + " Minutes";
		} else {
			totalTime = String.valueOf(totalMin) + " Minutes";
		}
		return totalTime;
	}
}
