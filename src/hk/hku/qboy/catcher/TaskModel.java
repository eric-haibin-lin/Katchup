package hk.hku.qboy.catcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class TaskModel {

	static private final Uri books_provider = TaskProvider.CONTENT_URI;
	static private String log_tag = "catcher";

	private Activity activity;

	// Contructor
	public TaskModel(Activity activity) {
		this.activity = activity;
		return;
	}

	// update title
	public int update(ContentValues content_values) {
		String title = (String) content_values.get(TaskProvider.TITLE);
		Integer id = (Integer) content_values.get(TaskProvider._ID);

		Cursor cursor = find(id);

		int num_rows_updated = 0;

		if ((cursor == null) || (cursor.getCount() <= 0)) {
			if (TextUtils.isEmpty(title)) {
			} else {
				num_rows_updated = insert(content_values);
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

	// find title
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

	// delete title
	// public int delete(String title) {
	// int num_rows_deleted = 0;
	//
	// try {
	// if (TextUtils.isEmpty(title)) {
	// num_rows_deleted = activity.getContentResolver().delete(
	// books_provider, null, null);
	// } else {
	// String selection_clause = TaskProvider.TITLE + " = ?";
	// String[] selection_args = { "" };
	// selection_args[0] = title;
	//
	// num_rows_deleted = activity.getContentResolver().delete(
	// books_provider, selection_clause, selection_args);
	// }
	// } catch (Exception e) {
	// final String error_message = "error in deleting";
	// Toast.makeText(activity.getBaseContext(), error_message,
	// Toast.LENGTH_SHORT);
	// Log.e(log_tag, error_message);
	// }
	//
	// return num_rows_deleted;
	//	}

	// insert
	public int insert(ContentValues content_values) {
		int num_rows_updated = 0;
		try {
			activity.getContentResolver()
					.insert(books_provider, content_values);
			num_rows_updated++;
		} catch (Exception e) {
			final String error_message = "error in inserting "
					+ content_values.get(TaskProvider.TITLE);
			Toast.makeText(activity.getBaseContext(), error_message,
					Toast.LENGTH_SHORT);
			Log.e(log_tag, error_message);
		}

		return num_rows_updated;
	}
}
