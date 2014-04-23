package hk.hku.qboy.catcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

@SuppressLint("ShowToast")
public class Task {

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";

	private Activity activity;
	private TaskModel taskModel;
	private int urgent;

	private String title;
	private String color;
	private String ddl;
	private String newRecord;
	private String record = "";

	// Constructor
	public Task(Activity activity, String title) {
		this.title = title;
		this.activity = activity;
		this.taskModel = new TaskModel(activity);
		queryFromDatabase();
		return;
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

	public int update() {
		return this.taskModel.update(makeContent());
	}

	public int insert() {
		return this.taskModel.insert(makeContent());

	}

	public void queryFromDatabase() {
		Cursor cursor = taskModel.find(title);
		if (cursor.getCount() != 0) {
			Log.d("Task", title + " found in db!");
			cursor.moveToNext();
			setDeadline(cursor.getString(cursor
					.getColumnIndex(TaskProvider.DDL)));
			setColor(cursor
					.getString(cursor.getColumnIndex(TaskProvider.COLOR)));
			setUrgent(cursor.getInt(cursor.getColumnIndex(TaskProvider.URGENT)));
			setRecord(cursor.getString(cursor
					.getColumnIndex(TaskProvider.RECORD)));

			Log.d("Task", "Find record with ddl: " + ddl);
		} else
			Log.d("Task", title + " not found in db!");
		return;
	}

	private ContentValues makeContent() {
		ContentValues content_values = new ContentValues();
		content_values.put(TaskProvider.TITLE, title);
		content_values.put(TaskProvider.DDL, ddl);
		content_values.put(TaskProvider.COLOR, color);
		content_values.put(TaskProvider.URGENT, urgent);
		content_values.put(TaskProvider.RECORD, record);
		return content_values;

	}

	public void printDebugInfo() {
		Log.d("TASK_DEBUG", "TITLE: " + this.title);
		Log.d("TASK_DEBUG", "DDL: " + this.ddl);
		Log.d("TASK_DEBUG", "COLOR: " + this.color);
		Log.d("TASK_DEBUG", "URGENT: " + this.urgent);
	}

	public void addTrackRecord(String newRecord) {
		this.newRecord = newRecord;
		setRecord(this.record + " ~ " + newRecord);
	}
}
