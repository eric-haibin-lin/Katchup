package hk.hku.qboy.catcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	String tabMonth = "Calendar";
	private FragmentTabHost tabHost;

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			newTask();
			return true;
		case R.id.action_setting:
			openSetting();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void newTask() {
		Intent intent = new Intent(this, CreateTask.class);
		startActivity(intent);
	}

	private void openSetting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		// setStripEnabled(false);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		tabHost.getTabWidget().setStripEnabled(true);
		// tabHost.getTabWidget().setRightStripDrawable(R.drawable.redline);
		// tabHost.getTabWidget().setLeftStripDrawable(R.drawable.redline);

		// 1
		tabHost.addTab(tabHost.newTabSpec("Tasks").setIndicator("Tasks"),
				taskFragment.class, null);
		// 2
		tabHost.addTab(tabHost.newTabSpec("Calendar").setIndicator(tabMonth),
				DayFragment.class, null);
		//3
		tabHost.addTab(tabHost.newTabSpec("Demo").setIndicator("demo"),ListGplayCardFragment.class,null);
		TabWidget widget = tabHost.getTabWidget();
		for (int i = 0; i < widget.getChildCount(); i++) {
			View v = widget.getChildAt(i);

			// Look for the title view to ensure this is an indicator and not a
			// divider.
			TextView tv = (TextView) v.findViewById(android.R.id.title);
			if (tv == null) {
				continue;
			}
			v.setBackgroundResource(R.drawable.tab_selector);
		}
	}

	public void changeMonth(String mon) {
		TextView text = (TextView) tabHost.getTabWidget().getChildTabViewAt(1)
				.findViewById(android.R.id.title);
		text.setText("Calendar(" + mon + ")");
	}

}