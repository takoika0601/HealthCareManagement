package jp.co.akiguchilab.healthcaremanagement.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CountUtil {
    private static final String TAG = CountUtil.class.getSimpleName();

    public static String dateformat(String format, Date date, int afterDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (afterDay != 0) calendar.add(Calendar.DATE, afterDay);

        SimpleDateFormat dateformat = new SimpleDateFormat(format);

        return dateformat.format(calendar.getTime());
    }
}
