package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class MainActivity extends FragmentActivity {
	String tabMonth = "Calendar";

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
		FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

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

	}

	public void changeMonth(String mon) {
		tabMonth = mon;
	}
}