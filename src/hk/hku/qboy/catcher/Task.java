package hk.hku.qboy.catcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;

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

	// Contructor
	public Task(Activity activity, String title, String color, String ddl) {
		this.title = title;
		this.color = color;
		this.ddl = ddl;
		
		this.activity = activity;
		this.taskModel = new TaskModel(activity);
		return;
	}

	public int update() {
		return this.taskModel.update(makeContent());
	}

	public int insert() {
		return this.taskModel.insert(makeContent());

	}

	private ContentValues makeContent() {
		ContentValues content_values = new ContentValues();
		content_values.put(TaskProvider.TITLE, title);
		content_values.put(TaskProvider.DDL, ddl);
		content_values.put(TaskProvider.COLOR, color);
		
		return content_values;

	}

}
