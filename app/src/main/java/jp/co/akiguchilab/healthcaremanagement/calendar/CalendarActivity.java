package jp.co.akiguchilab.healthcaremanagement.calendar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.calendarview.CalendarController;
import com.example.calendarview.DayFragment;
import com.example.calendarview.MonthByWeekFragment;

import jp.co.akiguchilab.healthcaremanagement.R;

public class CalendarActivity extends Activity implements CalendarController.EventHandler {
    private CalendarController mController;
    private CalendarController.EventInfo event;
    private boolean dayView;
    Fragment monthFrag;
    Fragment dayFrag;
    boolean eventView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cal_layout);
        mController = CalendarController.getInstance(this);

        monthFrag = new MonthByWeekFragment(System.currentTimeMillis(), false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.cal_frame, monthFrag).commit();

        mController.registerEventHandler(R.id.cal_frame, (CalendarController.EventHandler) monthFrag);
        mController.registerFirstEventHandler(0, this);
/*
        ContentValues values = new ContentValues();
        values.put(CalendarProvider.COLOR, Event.COLOR_RED);
        values.put(CalendarProvider.DESCRIPTION, "Some description");
        values.put(CalendarProvider.LOCATION, "Some location");
        values.put(CalendarProvider.EVENT, "Event name");

        Calendar jCalendar = Calendar.getInstance();

        jCalendar.set(2015, 5, 5, 19, 0);
        Time time = new Time(Time.TIMEZONE_UTC);
        time.setToNow();
        values.put(CalendarProvider.START, jCalendar.getTimeInMillis());
        values.put(CalendarProvider.START_DAY, Time.getJulianDay(time.toMillis(true), 0));

        values.put(CalendarProvider.END, jCalendar.getTimeInMillis());
        values.put(CalendarProvider.END_DAY, Time.getJulianDay(time.toMillis(true), 0));

        Uri uri =  getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
*/
    }

    @Override
    public long getSupportedEventTypes() {
        return CalendarController.EventType.GO_TO | CalendarController.EventType.VIEW_EVENT | CalendarController.EventType.UPDATE_TITLE;
    }

    @Override
    public void handleEvent(CalendarController.EventInfo event) {
        if (event.eventType == CalendarController.EventType.GO_TO) {
            Log.d("handleEvent", "Event started! : EVENT_CODE; GO_TO");
            // day selected calendar, start DayFragment to display the day that was clicked
            this.event = event;
            dayView = true;

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dayFrag = new DayFragment(event.startTime.toMillis(true), 1);
            dayFrag = new PicturesFragment();
            ft.replace(R.id.cal_frame, dayFrag).addToBackStack(null).commit();

        }
        if (event.eventType == CalendarController.EventType.VIEW_EVENT) {
            Log.d("handleEvent", "Event started! : EVENT_CODE; VIEW_EVENT");
            dayView = false;
            eventView = true;
            this.event = event;
        }
    }

    @Override
    public void eventsChanged() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calender, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
