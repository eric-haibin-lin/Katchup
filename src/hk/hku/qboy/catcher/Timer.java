package hk.hku.qboy.catcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

@SuppressLint("SimpleDateFormat")
public class Timer extends Service {

	public final IBinder mBinder = new ServiceBinder();

	public class ServiceBinder extends Binder {
		Timer getService() {
			return Timer.this;
		}
	}

	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	public boolean isCountingTime = false;
	public boolean keepCounting = true;
	String timerValue;

	private Handler customHandler = new Handler();
	private Runnable updateTimerThread;
	boolean broadcast = true;
	String timeFrom;
	String timeTo;

	String title;

	public int onStartCommand(Intent intent, int flags, int start) {
		createRunnable();
		return START_STICKY;
	}

	public void setTitle(String title) {
		this.title = title;
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
					timerValue = ("" + mins + ":" + String.format("%02d", secs));

					if (Timer.this.broadcast) {
						Intent i = new Intent("android.intent.action.TIMER");
						i.putExtra("time", timerValue);
						i.putExtra("title", title);
						Timer.this.sendBroadcast(i);
					}

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
		isCountingTime = true;
		keepCounting = true;
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		timeFrom = getCurrentTime();
	}

	public void finish() {
		isCountingTime = false;
		keepCounting = false;
		timeInMilliseconds = 0L;
		timeSwapBuff = 0L;
		updatedTime = 0L;
		stopSelf();
	}

	public String makeRecord() {
		timeTo = getCurrentTime();
		return timeFrom + "/" + timeTo;
	}

	private String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String formattedDate = df.format(c.getTime());
		formattedDate = formattedDate.substring(0, 8) + "T"
				+ formattedDate.substring(8);
		return formattedDate;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void setBroadcast(boolean b) {
		this.broadcast = b;
	}

}
