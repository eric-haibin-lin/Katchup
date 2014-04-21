package hk.hku.qboy.catcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class TaskDetail extends Activity {

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";
	static final int DATE_DIALOG_ID = 999;

	EditText title_edit;
	EditText color_edit;

	Task currentTask;

	int newYear;
	int newMonth;
	int newDay;

	int isUrgent;
	String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		title_edit = (EditText) findViewById(R.id.eventTitleInput);
		color_edit = (EditText) findViewById(R.id.tagColorButton);

		Intent intent = getIntent();
		if (intent.hasExtra("title_key")) {
			title = intent.getStringExtra("title_key");
			fillInCurrentTaskData();
		}

		addListenerOnUpdateButton();
		addListenerOnSwitchButton();
		addListenerOnDeadlineButton();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_detail, menu);
		return true;
	}

	private void fillInCurrentTaskData() {
		title_edit.setText(title);
		currentTask = new Task(this, title);
		String ddl = currentTask.getDeadline();
		currentTask.printDebugInfo();
		parseDeadlineString(ddl);
		
	}

	private void parseDeadlineString(String ddl) {
		//TODO: parse the deadline string to get day, month, year.
	}

	private String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String formattedDate = df.format(c.getTime());
		Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
		return formattedDate;
	}

	private String getCurrentYear() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String formattedDate = df.format(c.getTime());
		Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
		return formattedDate;
	}

	private String getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM");
		String formattedDate = df.format(c.getTime());
		Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
		return formattedDate;
	}

	private String getCurrentDay() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd");
		String formattedDate = df.format(c.getTime());
		Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
		return formattedDate;
	}

	// switch button
	private void addListenerOnSwitchButton() {
		final Switch urgent_switch = (Switch) findViewById(R.id.urgentSwitch);

		Switch.OnCheckedChangeListener switchListerner = new Switch.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					isUrgent = 1;
				else
					isUrgent = 0;
			}
		};

		if (urgent_switch != null)
			urgent_switch.setOnCheckedChangeListener(switchListerner);

	}

	// update button
	private void addListenerOnUpdateButton() {

		View.OnClickListener update_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				title = title_edit.getText().toString();
				String color = color_edit.getText().toString();

				String ddl = newYear + "-" + newMonth + "-" + newDay ;

				currentTask = new Task(TaskDetail.this, title);

				currentTask.setUrgent(isUrgent);
				currentTask.setDeadline(ddl);
				currentTask.setColor(color);

				int num_rows_updated = currentTask.update();
				Toast.makeText(getBaseContext(),
						num_rows_updated + " rows updated", Toast.LENGTH_SHORT)
						.show();
			}
		};

		Button update_button = (Button) findViewById(R.id.markCompleteButton);
		update_button.setOnClickListener(update_button_on_click_listener);
	}

	//
	public void addListenerOnDeadlineButton() {
		Button btnChangeDate = (Button) findViewById(R.id.deadlineDateSelector);
		btnChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			newYear = Integer.valueOf(getCurrentYear());
			newMonth = Integer.valueOf(getCurrentMonth()) - 1;
			newDay = Integer.valueOf(getCurrentDay());
			return new DatePickerDialog(this, datePickerListener, newYear,
					newMonth, newDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			newYear = selectedYear;
			newMonth = selectedMonth + 1;
			newDay = selectedDay;
		}
	};

}
