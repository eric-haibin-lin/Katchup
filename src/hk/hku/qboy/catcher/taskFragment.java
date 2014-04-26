package hk.hku.qboy.catcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class taskFragment extends ListFragment {
	static private final Uri tasks_provider = TaskProvider.CONTENT_URI;
	// Context context = getActivity();
	View V;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		V = inflater.inflate(R.layout.main1, container, false);
		setListAdapter();
		addTaskButtonListener();

		return V;
	}

	private void addTaskButtonListener() {
		final Button add_button = (Button) V.findViewById(R.id.button1);
		add_button.setBackgroundResource(R.drawable.plus);
		// Add task button
		View.OnClickListener add_button_on_click_listener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CreateTask.class);
				startActivity(intent);
			}
		};
		add_button.setOnClickListener(add_button_on_click_listener);
	}

	private void setListAdapter() {
		// Cursor c = context.getContentResolver().query(uri, projection,
		// selection, null, sortOrder);
		Cursor cursor = getActivity().getContentResolver().query(
				tasks_provider, null, null, null, null);
		TaskCursorAdapter taskAdapter = new TaskCursorAdapter(getActivity(),
				cursor);
		this.setListAdapter(taskAdapter);
	}

}