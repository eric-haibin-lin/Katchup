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
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class TaskDetail extends Activity {

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";
	static final int DATE_DIALOG_ID = 999;
	static final int EDIT_PAST_TASK = 1;
	static final int ADD_NEW_TASK = 2;

	EditText title_edit;
	EditText color_edit;
	TextView deadline_text;
	Switch urgent_switch;
	TextView task_list;

	Task currentTask;

	int newYear;
	int newMonth;
	int newDay;

	int isUrgent;
	String title;
	String color;

	String record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		getViewsById();

		Intent intent = getIntent();
		if (intent.hasExtra("title_key")) {
			title = intent.getStringExtra("title_key");
			fillInCurrentTaskData(EDIT_PAST_TASK);
		} else
			fillInCurrentTaskData(ADD_NEW_TASK);

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

	private void getViewsById() {
		title_edit = (EditText) findViewById(R.id.eventTitleInput);
		color_edit = (EditText) findViewById(R.id.tagColorButton);
		deadline_text = (TextView) findViewById(R.id.deadlineText);
		urgent_switch = (Switch) findViewById(R.id.urgentSwitch);
		task_list = (TextView) findViewById(R.id.taskList);
	}

	private void fillInCurrentTaskData(int state) {
		switch (state) {
		case EDIT_PAST_TASK: {
			title_edit.setText(title);
			currentTask = new Task(this, title);
			String ddl = currentTask.getDeadline();
			isUrgent = currentTask.getUrgent();
			record = currentTask.getRecord();
			color = currentTask.getColor();

			if (isUrgent == 1) {
				urgent_switch.setChecked(true);
			} else
				urgent_switch.setChecked(false);

			currentTask.printDebugInfo();
			parseDeadlineString(ddl);
			deadline_text.setText(ddl);
			color_edit.setText(color);
			task_list.setText(record);

			break;
		}
		case ADD_NEW_TASK: {
			newYear = Integer.valueOf(getCurrentYear());
			newMonth = Integer.valueOf(getCurrentMonth());
			newDay = Integer.valueOf(getCurrentDay());
			deadline_text.setText(makeDeadline());

		}
		default:
			break;
		}
	}

	private void parseDeadlineString(String ddl) {
		// parse the deadline string to get day, month, year.
		String delims = "-";
		String[] tokens = ddl.split(delims);
		newYear = Integer.valueOf(tokens[0]);
		newMonth = Integer.valueOf(tokens[1]);
		newDay = Integer.valueOf(tokens[2]);
	}

	private String makeDeadline() {
		String ddl = newYear + "-" + newMonth + "-" + newDay;
		return ddl;

	}

	private String getCurrentYear() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	private String getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	private String getCurrentDay() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	// switch button
	private void addListenerOnSwitchButton() {

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

				String ddl = newYear + "-" + newMonth + "-" + newDay;

				currentTask = new Task(TaskDetail.this, title);

				currentTask.setUrgent(isUrgent);
				currentTask.setDeadline(ddl);
				currentTask.setColor(color);

				int num_rows_updated = currentTask.update();
				Toast.makeText(getBaseContext(),
						num_rows_updated + " task updated", Toast.LENGTH_SHORT)
						.show();

				Intent intent = new Intent(TaskDetail.this, MainActivity.class);
				startActivity(intent);

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

			return new DatePickerDialog(this, datePickerListener, newYear,
					newMonth - 1, newDay);
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
			deadline_text.setText(makeDeadline());
		}
	};

}
