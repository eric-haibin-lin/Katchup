package hk.hku.qboy.catcher;
 
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class MainActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.main);
	    FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

	    tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	

	//1
	tabHost.addTab(tabHost.newTabSpec("Tasks").setIndicator("Tasks"),taskFragment.class,null);
	//2
    tabHost.addTab(tabHost.newTabSpec("Calendar").setIndicator("Calendar"),DayFragment.class,null);

	}
}