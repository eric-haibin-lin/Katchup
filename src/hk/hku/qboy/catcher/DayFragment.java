package hk.hku.qboy.catcher;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class DayFragment extends Fragment implements ViewFactory {

    private static final int VIEW_ID = 1;
	private ViewSwitcher mViewSwitcher;
	Time mSelectedDay = new Time();

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.day_activity, null);

        mViewSwitcher = (ViewSwitcher) v.findViewById(R.id.switcher);
        mViewSwitcher.setFactory(this);
        mViewSwitcher.getCurrentView().requestFocus();
        ((DayView) mViewSwitcher.getCurrentView()).updateTitle();

        return v;
    }
    
	@Override
	public View makeView() {
		DayView view = new DayView(getActivity(), mViewSwitcher, 7);
        view.setId(VIEW_ID);
        view.setLayoutParams(new ViewSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSelectedDay.setToNow();
        view.setSelected(mSelectedDay, false, false);
        return view;
	}

}
