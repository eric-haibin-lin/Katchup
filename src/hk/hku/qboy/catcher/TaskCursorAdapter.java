package hk.hku.qboy.catcher;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TaskCursorAdapter extends CursorAdapter {

	public TaskCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(cursor.getString(cursor
				.getColumnIndex(TaskProvider.TITLE)));

		Button color = (Button) view.findViewById(R.id.color);
		color.setText(cursor.getString(cursor
				.getColumnIndex(TaskProvider.COLOR)));

		Button urgent = (Button) view.findViewById(R.id.urgent);

		int urgentValue = cursor.getInt(cursor
				.getColumnIndex(TaskProvider.URGENT));
		String isUrgent = urgentValue > 0 ? "Urgent" : "Not Urgent";
		urgent.setText(isUrgent);
		

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(R.layout.list, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	private void addOnClickListener() {
		
		
		return;
	}

}
