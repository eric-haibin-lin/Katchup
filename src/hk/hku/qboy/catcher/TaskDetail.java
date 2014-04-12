package hk.hku.qboy.catcher;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TaskDetail extends Activity {

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);

		final EditText title_edit = (EditText) findViewById(R.id.eventTitleInput);
		
		final EditText color_edit = (EditText) findViewById(R.id.editText2);
		final EditText ddl_edit = (EditText) findViewById(R.id.editText3);

		// update button
		View.OnClickListener update_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				String title = title_edit.getText().toString();
				String color = color_edit.getText().toString();
				String ddl = ddl_edit.getText().toString();

				Task task = new Task(TaskDetail.this, title, color, ddl);
				int num_rows_updated = task.update();
				Toast.makeText(getBaseContext(),
						num_rows_updated + " rows updated", Toast.LENGTH_SHORT)
						.show();
			}
		};

		Button update_button = (Button) findViewById(R.id.button1);
		update_button.setOnClickListener(update_button_on_click_listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_detail, menu);
		return true;
	}

}
