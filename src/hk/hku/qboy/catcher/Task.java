package hk.hku.qboy.catcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;

@SuppressLint({ "ShowToast", "SimpleDateFormat" })
public class Task {

	private TaskModel taskModel;
	private int urgent;
	private int id;
	private String title;
	private String color;
	private String ddl;
	private String record = "";
	private int completed = 0;
	private long totalSec = 0;

	private int deadlineYear;
	private int deadlineMonth;
	private int deadlineDay;

	// Constructor
	public Task(Activity activity, int id) {
		this.id = id;
		this.taskModel = new TaskModel(activity);
		queryFromDatabase();
		calculateTime();
		return;
	}

	public Task(Activity activity) {
		this.taskModel = new TaskModel(activity);
		return;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDeadline(String ddl) {
		this.ddl = ddl;
		return;
	}

	public String getDeadline() {
		return this.ddl;
	}

	public void setUrgent(int urgent) {
		this.urgent = urgent;
		return;
	}

	public int getUrgent() {
		return this.urgent;
	}

	public void setColor(String color) {
		this.color = color;
		return;
	}

	public String getColor() {
		return this.color;
	}

	public void setRecord(String record) {
		this.record = record;
		return;
	}

	public String getRecord() {
		return this.record;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int update() {
		return this.taskModel.update(makeContent());
	}

	public int insert() {
		return this.taskModel.insert(makeContentWithoutId());
	}

	public void queryFromDatabase() {
		Cursor cursor = taskModel.find(id);
		if (cursor.getCount() != 0) {
			Log.d("Task", String.valueOf(id) + title + " found in db!");
			cursor.moveToNext();
			setDeadline(cursor.getString(cursor
					.getColumnIndex(TaskProvider.DDL)));
			setColor(cursor
					.getString(cursor.getColumnIndex(TaskProvider.COLOR)));
			setUrgent(cursor.getInt(cursor.getColumnIndex(TaskProvider.URGENT)));
			setRecord(cursor.getString(cursor
					.getColumnIndex(TaskProvider.RECORD)));
			setTitle(cursor
					.getString(cursor.getColumnIndex(TaskProvider.TITLE)));
			Log.d("Task", "Find record with ddl: " + ddl);
		} else
			Log.d("Task", title + " not found in db!");
		return;
	}

	private ContentValues makeContentWithoutId() {
		ContentValues content_values = new ContentValues();
		content_values.put(TaskProvider.TITLE, title);
		content_values.put(TaskProvider.DDL, ddl);
		content_values.put(TaskProvider.COLOR, color);
		content_values.put(TaskProvider.URGENT, urgent);
		content_values.put(TaskProvider.RECORD, record);
		content_values.put(TaskProvider.COMPLETED, completed);

		return content_values;
	}

	private ContentValues makeContent() {
		ContentValues content_values = new ContentValues();
		content_values.put(TaskProvider._ID, id);
		content_values.put(TaskProvider.TITLE, title);
		content_values.put(TaskProvider.DDL, ddl);
		content_values.put(TaskProvider.COLOR, color);
		content_values.put(TaskProvider.URGENT, urgent);
		content_values.put(TaskProvider.RECORD, record);
		content_values.put(TaskProvider.COMPLETED, completed);

		return content_values;
	}

	public void printDebugInfo() {
		Log.d("TASK_DEBUG", "TITLE: " + this.title);
		Log.d("TASK_DEBUG", "DDL: " + this.ddl);
		Log.d("TASK_DEBUG", "COLOR: " + this.color);
		Log.d("TASK_DEBUG", "URGENT: " + this.urgent);
		Log.d("TASK_DEBUG", "COMPELTED: " + this.completed);
	}

	public void addTrackRecord(String newRecord) {
		if (!this.record.equals(""))
			setRecord(this.record + ";" + newRecord);
		else
			setRecord(newRecord);
	}

	private void calculateTime() {
		Time start = new Time(TimeZone.getDefault().getDisplayName());
		Time end = new Time(TimeZone.getDefault().getDisplayName());

		String[] records = this.record.split(";");
		for (String currentRecord : records) {
			String[] p = currentRecord.split("/");
			if (p[0].length() < 8 || p[1].length() < 8) {
				continue;
			}
			start.parse(p[0]);
			end.parse(p[1]);
			long startSecond = start.toMillis(false) / 1000;
			long endSecond = end.toMillis(false) / 1000;
			long intervalSecond = endSecond - startSecond;
			Log.d("INTERVAL", String.valueOf(intervalSecond));
			totalSec += intervalSecond;
		}
	}

	public String getTotalTime() {
		long totalMin = totalSec / 60;
		String totalTime = "";
		if (totalMin >= 60) {
			long totalHour = totalMin / 60;
			long remainingMin = totalMin - totalHour * 60;
			totalTime = String.valueOf(totalHour) + " Hour "
					+ String.valueOf(remainingMin) + " Minutes";
		} else {
			totalTime = String.valueOf(totalMin) + " Minutes";
		}
		return totalTime;
	}

	public boolean testUrgent() {
		parseDeadlineString(ddl);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd");
		String formattedDate = df.format(c.getTime());
		int currentDay = Integer.valueOf(formattedDate);
		df = new SimpleDateFormat("MM");
		formattedDate = df.format(c.getTime());
		int currentMonth = Integer.valueOf(formattedDate);
		df = new SimpleDateFormat("yyyy");
		formattedDate = df.format(c.getTime());
		int currentYear = Integer.valueOf(formattedDate);

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

}
