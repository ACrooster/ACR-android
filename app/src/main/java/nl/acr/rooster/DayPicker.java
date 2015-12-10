package nl.acr.rooster;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DayPicker implements DatePickerDialog.OnDateSetListener {
    @Override
    public void onDateSet(DatePickerDialog view,int year,int monthOfYear,int dayOfMonth){
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date date=dateFormat.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            MainActivity.cal.setTime(date);
            UpdateSchedule.dayOfWeek = toDayNumber(MainActivity.cal.get(Calendar.DAY_OF_WEEK));
            int unixTime = (int)(date.getTime()/1000 + 3600);
            ScheduleFragment.setWeekUnix(unixTime);
            UpdateSchedule.scroll = true;
            ScheduleFragment.createList();
        }catch(ParseException e){
            e.printStackTrace();
        }
    }

    static int toDayNumber(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            default:
                return 0;
        }
    }

}

