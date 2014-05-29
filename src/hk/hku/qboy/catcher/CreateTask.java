package hk.hku.qboy.catcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

@SuppressLint("SimpleDateFormat")
public class CreateTask extends Activity implements
		DatePicker.OnDateChangedListener {

	EditText title_edit;
	Switch urgent_switch;
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

	int isUrgent;
	String title;
	int color = Color.RED;
	String record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_task);

		getViewsById();

		fillInCurrentTaskData();

		addListenerOnUpdateButton();
		addListenerOnSwitchButton();
		addListenerOnColorButtons();
//		generateDemoTasks();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_detail, menu);
		getActionBar().setTitle("New Task");
		return true;
	}

	private void getViewsById() {
		title_edit = (EditText) findViewById(R.id.eventTitleInput);
		urgent_switch = (Switch) findViewById(R.id.urgentSwitch);
		picker = (DatePicker) findViewById(R.id.datePicker);
		redBtn = (ImageButton) findViewById(R.id.redColor);
		blueBtn = (ImageButton) findViewById(R.id.blueColor);
		yellowBtn = (ImageButton) findViewById(R.id.yellowColor);
		greyBtn = (ImageButton) findViewById(R.id.greyColor);
		greenBtn = (ImageButton) findViewById(R.id.greenColor);
		pinkBtn = (ImageButton) findViewById(R.id.pinkColor);
		currentColorBtn = redBtn;
	}

	private void fillInCurrentTaskData() {
		newYear = Integer.valueOf(getCurrentYear());
		newMonth = Integer.valueOf(getCurrentMonth());
		newDay = Integer.valueOf(getCurrentDay());
		picker.init(newYear, newMonth - 1, newDay, this);
		setColorImage(color);
	};

	private String makeDeadline() {
		String ddl = newYear + "-" + newMonth + "-" + newDay;
		return ddl;
	}

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
			color = Color.GRAY;
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

	private void setColorImage(int currentColor) {
		if (currentColor == Color.RED)
			findViewById(R.id.redColor).setBackgroundResource(R.drawable.red_u);
		else if (currentColor == Color.BLUE)
			findViewById(R.id.blueColor).setBackgroundResource(
					R.drawable.blue_u);
		else if (currentColor == Color.YELLOW)
			findViewById(R.id.yellowColor).setBackgroundResource(
					R.drawable.yellow_u);
		else if (currentColor == Color.GRAY)
			findViewById(R.id.greyColor).setBackgroundResource(
					R.drawable.grey_u);
		else if (currentColor == Color.PINK)
			findViewById(R.id.pinkColor).setBackgroundResource(
					R.drawable.pink_u);
		else if (currentColor == Color.GREEN)
			findViewById(R.id.greenColor).setBackgroundResource(
					R.drawable.green_u);
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
				if (title.equals(""))
					return;
				if (title.equals("testdemo")) {
					generateDemoTasks();
					finish();
					return;
				}

				String ddl = makeDeadline();
				currentTask = new Task(CreateTask.this);
				currentTask.setUrgent(isUrgent);
				currentTask.setDeadline(ddl);
				currentTask.setColor(color);
				currentTask.setTitle(title);
				int num_rows_updated = currentTask.insert();
				Log.d("CREATE", num_rows_updated + " task added");
				finish();
			}
		};

		Button update_button = (Button) findViewById(R.id.markCompleteButton);
		update_button.setOnClickListener(update_button_on_click_listener);
	}

	@Override
	public void onDateChanged(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		newYear = selectedYear;
		newMonth = selectedMonth + 1;
		newDay = selectedDay;
	}

	private void generateDemoTasks() {

		Task task = new Task(this);

		task.setUrgent(1);
		task.setDeadline("2014-5-28");
		task.setColor(Color.RED);
		task.setTitle("CSIS 3330 Report");

		task.addTrackRecord("20140527T000000/20140527T003000;20140527T090500/20140527T091000;20140527T140500/20140527T145500;20140527T160500/20140527T172100");
		task.insert();

		task = new Task(this);
		task.setUrgent(0);
		task.setDeadline("2014-6-28");
		task.setColor(Color.BLUE);
		task.setTitle("CSIS 234 Assignment");

		task.addTrackRecord("20140527T110000/20140527T110800;20140527T173000/20140527T175000;20140527T180500/20140527T181000");
		task.insert();

		task = new Task(this);
		task.setUrgent(1);
		task.setDeadline("2014-7-28");
		task.setColor(Color.GREEN);
		task.setTitle("CSIS 3330 Final Review");

		task.addTrackRecord("20140527T190500/20140527T191000;20140527T192500/20140527T221000;20140527T221500/20140527T232000;20140528T001925/20140528T011000");
		task.insert();

	}

}
