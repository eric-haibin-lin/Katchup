package hk.hku.qboy.catcher;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TaskCursorAdapter extends CursorAdapter {

	Context context;
	String currentTitle;
	String currentColor;
	String isUrgent;
	MainActivity main;

	public TaskCursorAdapter(Context context, Cursor c) {
		super(context, c);
		this.context = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		getDataFromCursor(cursor);
		addOnClickListener(view, currentTitle);
	//	addStartClickListener(view);
		updateDataInListView(view);
		main = (MainActivity) context;

	}

	private void getDataFromCursor(Cursor cursor) {
		currentTitle = cursor.getString(cursor
				.getColumnIndex(TaskProvider.TITLE));

		currentColor = cursor.getString(cursor
				.getColumnIndex(TaskProvider.COLOR));

		int urgentValue = cursor.getInt(cursor
				.getColumnIndex(TaskProvider.URGENT));
		isUrgent = urgentValue > 0 ? "Urgent" : "Not Urgent";

	}

	private void addStartClickListener(View view) {
		Button startButton = (Button) view.findViewById(R.id.startButton);
		final String title = currentTitle;
		View.OnClickListener edit_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				//main.taskStart(title);
			}
		};
		startButton.setOnClickListener(edit_button_on_click_listener);
	}
	
	

	private void updateDataInListView(View view) {
		TextView title = (TextView) view.findViewById(R.id.title);
		Button urgent = (Button) view.findViewById(R.id.urgent);
		Button color = (Button) view.findViewById(R.id.color);

		title.setText(currentTitle);
		color.setText(currentColor);
		urgent.setText(isUrgent);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(R.layout.list, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	private void addOnClickListener(View view, String currentTitle) {
		Button editButton = (Button) view.findViewById(R.id.edit);
		final String title = currentTitle;
		View.OnClickListener edit_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, TaskDetail.class);
				intent.putExtra("title_key", title);
				Log.d("ADAPTER", title);
				context.startActivity(intent);
			}
		};
		editButton.setOnClickListener(edit_button_on_click_listener);

		return;
	}
}
