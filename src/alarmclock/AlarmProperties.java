package alarmclock;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import alarmclock.AlarmBuilder.WeekAndDayMemorie;

public class AlarmProperties {
    private int year;
    
    public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getWeeks() {
		return weeks;
	}

	public LinkedList<Day> getDays() {
		return days;
	}

	private int month;
    private int day;
    private int hour;
    private int minute;
    private int weeks;
    private LinkedList<Day> days;

    public AlarmProperties(GregorianCalendar c, WeekAndDayMemorie wadm) {

        // dynamic semantic check: check if alarm lies in the past
        if (c.before(new GregorianCalendar())) {
            throw new IllegalArgumentException("Alarm lies in the past.");
        }

        days = new LinkedList<Day>();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        weeks = 0;

        if (wadm != null) {
            weeks = wadm.getWeeks();
            days = wadm.getDays();
        } else {
            int day = c.get(Calendar.DAY_OF_WEEK);
            days.add(Day.values()[(day - 2 < 0) ? 6 : day - 2]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Year: ").append(year).append(" Month: ").append(month);
        sb.append(" Day: ").append(day).append(" Hour: ").append(hour);
        sb.append(" Minute: ").append(minute).append(" Weeks: ").append(weeks).append(" Days: ");

        for (Day day : days) {
            sb.append(day).append(" ");
        }

        return sb.toString();
    }
}

