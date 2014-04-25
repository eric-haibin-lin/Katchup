package hk.hku.qboy.catcher;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class DayFragment extends Fragment implements ViewFactory {

    private static final int VIEW_ID = 1;
	private ViewSwitcher mViewSwitcher;
	Time mSelectedDay = new Time();
	private EventLoader mEventLoader;

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Context context = getActivity();
        mEventLoader = new EventLoader(context);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.day_activity, null);

        mViewSwitcher = (ViewSwitcher) v.findViewById(R.id.switcher);
        mViewSwitcher.setFactory(this);
        mViewSwitcher.getCurrentView().requestFocus();
//        ((DayView) mViewSwitcher.getCurrentView()).updateTitle();

        return v;
    }
    
	@Override
	public View makeView() {
		DayView view = new DayView(getActivity(), mViewSwitcher, mEventLoader, 7);
//		DayView view = new DayView(getActivity(), mViewSwitcher, mEventLoader, 7);
        view.setId(VIEW_ID);
        view.setLayoutParams(new ViewSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSelectedDay.setToNow();
        view.setSelected(mSelectedDay);
        return view;
	}

	@Override
    public void onResume() {
        super.onResume();
        mEventLoader.startBackgroundThread();
//        mTZUpdater.run();
//        eventsChanged();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();

        view = (DayView) mViewSwitcher.getNextView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();
    }
	@Override
    public void onPause() {
        super.onPause();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.cleanup();
        view = (DayView) mViewSwitcher.getNextView();
        view.cleanup();
        mEventLoader.stopBackgroundThread();

        // Stop events cross-fade animation
//TODO        view.stopEventsAnimation();
//        ((DayView) mViewSwitcher.getNextView()).stopEventsAnimation();
    }
	public void eventsChanged() {
        if (mViewSwitcher == null) {
            return;
        }
        DayView view = (DayView) mViewSwitcher.getCurrentView();
//        view.clearCachedEvents();
        view.reloadEvents();

        view = (DayView) mViewSwitcher.getNextView();
//        view.clearCachedEvents();
    }
}
