package hk.hku.qboy.catcher;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

public class Timer {

	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	boolean isCountingTime = false;
	boolean keepCounting = true;
	TextView timerValue;

	private Handler customHandler = new Handler();
	private Runnable updateTimerThread;

	public Timer(TextView timerValue) {
		this.timerValue = timerValue;
	}

	public void createRunnable() {
		updateTimerThread = new Runnable() {
			public void run() {
				if (keepCounting) {
					timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
					updatedTime = timeSwapBuff + timeInMilliseconds;
					int secs = (int) (updatedTime / 1000);
					int mins = secs / 60;
					secs = secs % 60;
					timerValue.setText("" + mins + ":"
							+ String.format("%02d", secs));
					customHandler.postDelayed(this, 0);
				}
			}
		};
	}

	public void pause() {
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		isCountingTime = false;
	}

	public void start() {
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		isCountingTime = true;
		keepCounting = true;
	}

	public void finish() {
		isCountingTime = false;
		keepCounting = false;
		timeInMilliseconds = 0L;
		timeSwapBuff = 0L;
		updatedTime = 0L;
	}
}
