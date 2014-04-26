package hk.hku.qboy.catcher;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class TaskDetail extends Activity implements
		DatePicker.OnDateChangedListener {

	EditText title_edit;
	TextView color_view;
	TextView deadline_text;
	Switch urgent_switch;
	TextView task_list;
	DatePicker picker;
	Task currentTask;
	ImageButton redBtn;
	ImageButton blueBtn;
	ImageButton greenBtn;
	ImageButton orangeBtn;
	ImageButton yellowBtn;

	int newYear;
	int newMonth;
	int newDay;
	int id;
	int isUrgent;
	String title;
	String color;
	int completed = 0;
	String record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		getViewsById();

		Intent intent = getIntent();
		if (intent.hasExtra("title_key")) {
			title = intent.getStringExtra("title_key");
			id = intent.getIntExtra("id_key", 0);
			fillInCurrentTaskData();
		} else
			Log.d("EDIT", "No intent passed");

		addListenerOnUpdateButton();
		addListenerOnSwitchButton();
		addListenerOnColorButtons();
		addListenerOnCompleteButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task_detail, menu);
		return true;
	}

	private void getViewsById() {
		title_edit = (EditText) findViewById(R.id.eventTitleInput);
		color_view = (TextView) findViewById(R.id.tagColorButton);
		deadline_text = (TextView) findViewById(R.id.deadlineText);
		urgent_switch = (Switch) findViewById(R.id.urgentSwitch);
		task_list = (TextView) findViewById(R.id.taskList);
		picker = (DatePicker) findViewById(R.id.datePicker);
		redBtn = (ImageButton) findViewById(R.id.redColor);
		blueBtn = (ImageButton) findViewById(R.id.blueColor);
		yellowBtn = (ImageButton) findViewById(R.id.yellowColor);
		orangeBtn = (ImageButton) findViewById(R.id.orangeColor);
		greenBtn = (ImageButton) findViewById(R.id.greenColor);

	}

	// Read from database to set right attributes of this task
	private void fillInCurrentTaskData() {
		title_edit.setText(title);
		currentTask = new Task(this, id);
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
		picker.init(newYear, newMonth - 1, newDay, this);

		color_view.setText(color);
		task_list.setText(record);

	}

	// TODO: parse the history records and present it in a nice way

	// parse the deadline string to get day, month, year.
	private void parseDeadlineString(String ddl) {
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

	// add listener for color buttons
	private void addListenerOnColorButtons() {
		redBtn.setOnClickListener(listenerOnColorButton("red"));
		greenBtn.setOnClickListener(listenerOnColorButton("green"));
		yellowBtn.setOnClickListener(listenerOnColorButton("yellow"));
		blueBtn.setOnClickListener(listenerOnColorButton("blue"));
		orangeBtn.setOnClickListener(listenerOnColorButton("orange"));
	}

	private View.OnClickListener listenerOnColorButton(final String color) {
		View.OnClickListener color_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				color_view.setText(color);
			}
		};
		return color_on_click_listener;
	}

	// switch button to set urgent
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
				String color = color_view.getText().toString();
				String ddl = newYear + "-" + newMonth + "-" + newDay;

				currentTask = new Task(TaskDetail.this, id);
				currentTask.setTitle(title);
				currentTask.setUrgent(isUrgent);
				currentTask.setDeadline(ddl);
				currentTask.setColor(color);
				currentTask.setCompleted(completed);

				int num_rows_updated = currentTask.update();
				currentTask.printDebugInfo();
				Log.d("EDIT", num_rows_updated + " task updated");
				finish();
			}
		};

		Button update_button = (Button) findViewById(R.id.updateTask);
		update_button.setOnClickListener(update_button_on_click_listener);
	}

	// complete button which sets completed to 1 and perform click on update
	// button
	private void addListenerOnCompleteButton() {
		Button complete_button = (Button) findViewById(R.id.completeTask);
		View.OnClickListener complete_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				completed = 1;
				Button update_button = (Button) findViewById(R.id.updateTask);
				update_button.performClick();
			}
		};
		complete_button.setOnClickListener(complete_button_on_click_listener);
	}

	@Override
	public void onDateChanged(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		newYear = selectedYear;
		newMonth = selectedMonth + 1;
		newDay = selectedDay;
		deadline_text.setText(makeDeadline());
	}

}
