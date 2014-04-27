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
	Switch urgent_switch;
	TextView task_list;
	DatePicker picker;
	Task currentTask;
	ImageButton redBtn;
	ImageButton blueBtn;
	ImageButton greenBtn;
	ImageButton greyBtn;
	ImageButton yellowBtn;
	ImageButton pinkBtn;
	ImageButton currentColorBtn;
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
		urgent_switch = (Switch) findViewById(R.id.urgentSwitch);
		task_list = (TextView) findViewById(R.id.taskList);
		picker = (DatePicker) findViewById(R.id.datePicker);
		redBtn = (ImageButton) findViewById(R.id.redColor);
		blueBtn = (ImageButton) findViewById(R.id.blueColor);
		yellowBtn = (ImageButton) findViewById(R.id.yellowColor);
		greyBtn = (ImageButton) findViewById(R.id.greyColor);
		greenBtn = (ImageButton) findViewById(R.id.greenColor);
		pinkBtn = (ImageButton) findViewById(R.id.pinkColor);
		currentColorBtn = redBtn;
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
		picker.init(newYear, newMonth - 1, newDay, this);
		Log.d("Color", "color from task:" + color);
		setColorImage(color);
		task_list.setText(currentTask.getTotalTime());

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
		redBtn.setOnClickListener(listenerOnColorButton());
		greenBtn.setOnClickListener(listenerOnColorButton());
		yellowBtn.setOnClickListener(listenerOnColorButton());
		blueBtn.setOnClickListener(listenerOnColorButton());
		greyBtn.setOnClickListener(listenerOnColorButton());
		pinkBtn.setOnClickListener(listenerOnColorButton());
	}

	private View.OnClickListener listenerOnColorButton() {
		View.OnClickListener color_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				turnN(currentColorBtn);
				turnU(v);
			}
		};
		return color_on_click_listener;
	}

	private void turnU(View v) {
		currentColorBtn = (ImageButton) v;
		switch (v.getId()) {
		case R.id.redColor:
			color = Color.RED;
			v.setBackgroundResource(R.drawable.red_u);
			break;
		case R.id.blueColor:
			color = Color.BLUE;
			v.setBackgroundResource(R.drawable.blue_u);
			break;
		case R.id.yellowColor:
			color = Color.YELLOW;
			v.setBackgroundResource(R.drawable.yellow_u);
			break;
		case R.id.greyColor:
			color = Color.GREY;
			v.setBackgroundResource(R.drawable.grey_u);
			break;
		case R.id.pinkColor:
			color = Color.PINK;
			v.setBackgroundResource(R.drawable.pink_u);
			break;
		case R.id.greenColor:
			color = Color.GREEN;
			v.setBackgroundResource(R.drawable.green_u);
			break;
		}
	}

	private void turnN(View v) {
		switch (v.getId()) {
		case R.id.redColor:
			v.setBackgroundResource(R.drawable.red_n);
			break;
		case R.id.blueColor:
			v.setBackgroundResource(R.drawable.blue_n);
			break;
		case R.id.yellowColor:
			v.setBackgroundResource(R.drawable.yellow_n);
			break;
		case R.id.greyColor:
			v.setBackgroundResource(R.drawable.grey_n);
			break;
		case R.id.pinkColor:
			v.setBackgroundResource(R.drawable.pink_n);
			break;
		case R.id.greenColor:
			v.setBackgroundResource(R.drawable.green_n);
			break;
		}
	}

	private void setColorImage(String currentColor) {
		if (currentColor.equals(Color.RED))
			findViewById(R.id.redColor).setBackgroundResource(R.drawable.red_u);
		else if (currentColor.equals(Color.BLUE))
			findViewById(R.id.blueColor).setBackgroundResource(
					R.drawable.blue_u);
		else if (currentColor.equals(Color.YELLOW))
			findViewById(R.id.yellowColor).setBackgroundResource(
					R.drawable.yellow_u);
		else if (currentColor.equals(Color.GREY))
			findViewById(R.id.greyColor).setBackgroundResource(
					R.drawable.grey_u);
		else if (currentColor.equals(Color.PINK))
			findViewById(R.id.pinkColor).setBackgroundResource(
					R.drawable.pink_u);
		else if (currentColor.equals(Color.GREEN))
			findViewById(R.id.greenColor).setBackgroundResource(
					R.drawable.green_u);
		else
			Log.d("COLOR", "non found.");
		Log.d("COLOR", currentColor);
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
				String ddl = makeDeadline();

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
	}

}
