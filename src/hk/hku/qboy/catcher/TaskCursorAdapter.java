package hk.hku.qboy.catcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class TaskCursorAdapter extends CursorAdapter {

	Context context;
	String currentTitle;
	int currentColor;
	boolean isUrgent;
	MainActivity main;
	int completed = 0;
	int id;
	String deadline;
	int deadlineYear;
	int deadlineMonth;
	int deadlineDay;
	int currentYear;
	int currentMonth;
	int currentDay;

	ImageButton colorBtn;

	@SuppressWarnings("deprecation")
	public TaskCursorAdapter(Context context, Cursor c) {
		super(context, c);
		this.context = context;
		setCurrentDate();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		getDataFromCursor(cursor);
		addOnClickListener(view, currentTitle);
		addStartClickListener(view);
		updateDataInListView(view);
		main = (MainActivity) context;
	}

	private void getDataFromCursor(Cursor cursor) {
		currentTitle = cursor.getString(cursor
				.getColumnIndex(TaskProvider.TITLE));
		currentColor = Integer.parseInt(cursor.getString(cursor
				.getColumnIndex(TaskProvider.COLOR)));
		completed = cursor
				.getInt(cursor.getColumnIndex(TaskProvider.COMPLETED));
		id = cursor.getInt(cursor.getColumnIndex(TaskProvider._ID));
		deadline = cursor.getString(cursor.getColumnIndex(TaskProvider.DDL));

		int urgentValue = cursor.getInt(cursor
				.getColumnIndex(TaskProvider.URGENT));
		isUrgent = urgentValue > 0 ? true : false;
		Log.d("CURSOR_ADAPTER", currentTitle + " id: " + String.valueOf(id));

		if (!isUrgent && testUrgent()) {
			Task task = new Task((MainActivity) context, id);
			task.setUrgent(1);
			task.update();
			Log.d("Total", task.getTotalTime());
		}
	}

	private void setCurrentDate() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd");
		String formattedDate = df.format(c.getTime());
		currentDay = Integer.valueOf(formattedDate);
		df = new SimpleDateFormat("MM");
		formattedDate = df.format(c.getTime());
		currentMonth = Integer.valueOf(formattedDate);
		df = new SimpleDateFormat("yyyy");
		formattedDate = df.format(c.getTime());
		currentYear = Integer.valueOf(formattedDate);
	}

	private boolean testUrgent() {
		parseDeadlineString(deadline);
		if (currentYear > deadlineYear)
			return true;
		if (currentYear == deadlineYear && currentMonth > deadlineMonth)
			return true;
		if (currentYear == deadlineYear && currentMonth == deadlineMonth
				&& deadlineDay - currentDay < 3)
			return true;
		else
			return false;
	}

	private void parseDeadlineString(String ddl) {
		String delims = "-";
		String[] tokens = ddl.split(delims);
		deadlineYear = Integer.valueOf(tokens[0]);
		deadlineMonth = Integer.valueOf(tokens[1]);
		deadlineDay = Integer.valueOf(tokens[2]);
	}

	// Start timer when click on task title
	private void addStartClickListener(View view) {
		TextView title_text = (TextView) view.findViewById(R.id.title);
		final String title = currentTitle;
		final int idToPass = id;
		View.OnClickListener start_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent timerIntent = new Intent(main, TimerActivity.class);
				timerIntent.putExtra("title", title);
				timerIntent.putExtra("id_key", idToPass);
				main.startActivity(timerIntent);
			}
		};
		title_text.setOnClickListener(start_on_click_listener);
	}

	private void updateDataInListView(View view) {
		TextView title = (TextView) view.findViewById(R.id.title);
		colorBtn = (ImageButton) view.findViewById(R.id.color);
		title.setText(currentTitle);
		setColorImage();
	}

	@Override
	// For every record provided by the cursor, it calls newView, which calls
	// bindView.
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	private void setColorImage() {
		if (currentColor == (Color.RED))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.red_u
					: R.drawable.red_n);
		else if (currentColor == (Color.BLUE))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.blue_u
					: R.drawable.blue_n);
		else if (currentColor == (Color.YELLOW))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.yellow_u
					: R.drawable.yellow_n);
		else if (currentColor == (Color.GREY))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.grey_u
					: R.drawable.grey_n);
		else if (currentColor == (Color.PINK))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.pink_u
					: R.drawable.pink_n);
		else if (currentColor == (Color.GREEN))
			colorBtn.setBackgroundResource(isUrgent ? R.drawable.green_u
					: R.drawable.green_n);
	}

	private void addOnClickListener(View view, String currentTitle) {
		Button editButton = (Button) view.findViewById(R.id.edit);
		editButton.setBackgroundResource(R.drawable.edit);
		final String title = currentTitle;
		final int idToPass = id;
		View.OnClickListener edit_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, TaskDetail.class);
				intent.putExtra("title_key", title);
				intent.putExtra("id_key", idToPass);
				Log.d("ADAPTER", title);
				context.startActivity(intent);
			}
		};
		editButton.setOnClickListener(edit_button_on_click_listener);

		return;
	}
}
