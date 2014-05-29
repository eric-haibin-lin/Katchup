package hk.hku.qboy.catcher;

import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

@SuppressLint({ "ShowToast", "SimpleDateFormat" })
public class Task {
	private Activity activity;
	private int urgent;
	private int id;
	private String title;
	private int color;
	private String ddl;
	private String record = "";
	private int completed = 0;
	private long totalSec = 0;
	private long avgSec = 0;

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";

	// Constructor
	public Task(Activity activity, int id) {
		this.activity = activity;
		this.id = id;
		queryFromDatabase();
		calculateTime();
		return;
	}

	public long getTotalSec() {
		return totalSec;
	}

	public Task(Activity activity) {
		this.activity = activity;
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

	public void setColor(int color) {
		this.color = color;
		return;
	}

	public int getColor() {
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
		return this.update(makeContent());
	};

	public int insert() {
		ContentValues content_values = makeContentWithoutId();
		int num_rows_updated = 0;
		try {
			activity.getContentResolver()
					.insert(books_provider, content_values);
			num_rows_updated++;
		} catch (Exception e) {
			final String error_message = "error in inserting "
					+ content_values.get(TaskProvider.TITLE);
			Log.e(log_tag, error_message);
		}
		return num_rows_updated;
	}

	public void queryFromDatabase() {
		Cursor cursor = find(id);
		if (cursor.getCount() != 0) {
			Log.d("Task", String.valueOf(id) + title + " found in db!");
			cursor.moveToNext();
			setDeadline(cursor.getString(cursor
					.getColumnIndex(TaskProvider.DDL)));
			setColor(Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(TaskProvider.COLOR))));
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
		int count = 0;
		String[] records = this.record.split(";");
		for (String currentRecord : records) {
			count++;
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
		avgSec = totalSec / count;
	}

	public String getTotalTime() {
		return getTotalTime(totalSec);
	}

	public String getAverageTime() {
		return getTotalTime(avgSec);
	}

	private String getTotalTime(long time) {
		long totalMin = time / 60;
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

	private int update(ContentValues content_values) {
		String title = (String) content_values.get(TaskProvider.TITLE);
		Integer id = (Integer) content_values.get(TaskProvider._ID);

		Cursor cursor = find(id);

		int num_rows_updated = 0;

		if ((cursor == null) || (cursor.getCount() <= 0)) {
			if (TextUtils.isEmpty(title)) {
			} else {
				// num_rows_updated = this.insertWithContent(content_values);
			}
		} else {
			try {
				String selection_clause = TaskProvider._ID + " = ?";
				String[] selection_args = { "" };
				selection_args[0] = String.valueOf(id);
				num_rows_updated = activity.getContentResolver().update(
						books_provider, content_values, selection_clause,
						selection_args);
			} catch (Exception e) {
				final String error_message = "error in updating " + title;
				Toast.makeText(activity.getBaseContext(), error_message,
						Toast.LENGTH_SHORT);
				Log.e(log_tag, error_message);
			}
		}
		cursor.close();
		return num_rows_updated;
	}

	public Cursor find(int id) {
		Cursor cursor = null;
		if (id == 0) {
			cursor = null;
		} else {
			String selection_clause = TaskProvider._ID + " = ?";
			String[] selection_args = { "" };
			selection_args[0] = String.valueOf(id);
			cursor = activity.getContentResolver().query(books_provider, null,
					selection_clause, selection_args, null);
		}
		return cursor;
	}
}
