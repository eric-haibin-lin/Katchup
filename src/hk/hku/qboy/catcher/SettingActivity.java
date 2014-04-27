package hk.hku.qboy.catcher;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingActivity extends Activity {

	CheckBox wifiBox;
	CheckBox cellularBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViews();
		readPreference();
		addConfirmListener();
		addCancelListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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

}
