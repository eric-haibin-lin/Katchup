package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);

		Cursor cursor = managedQuery(tasks_provider, null, null, null, null);

		String[] columns = new String[] { TaskProvider.TITLE,
				TaskProvider.COLOR, TaskProvider.DDL };
		int[] views = new int[] { R.id.table_string, R.id.table_isbn, R.id.table_ddl };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.list, cursor, columns, views);
		this.setListAdapter(adapter);

		final Button detail_button = (Button) findViewById(R.id.button1);

		// detail button
		View.OnClickListener detail_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TaskDetail.class);
				startActivity(intent);
			}
		};

		detail_button.setOnClickListener(detail_button_on_click_listener);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
