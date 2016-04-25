package alarmclock;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public final class AlarmBuilder {

    private static final String ALARM_DATE_INVALID_MSG = "Alarm date is not valid.";

    public AlarmBuilder() {
    }

    /**
     * Set alarm in certain time.
     */
    public MinuteScope in() {
        return new MinuteScope();
    }

    /**
     * Set alarm in specific year(date).
     */
    public YearScope on() {
        return new YearScope(new GregorianCalendar());
    }

    /**
     * Set alarm on specific day or days(monday, tuesday, ...).
     */
    public TimeWrapperScope on(Day... days) {
        WeekAndDayMemorie wadm = new WeekAndDayMemorie();
        GregorianCalendar c = new GregorianCalendar();
        for (Day day : days) {
            wadm.addDay(day);
        }
        int oldVal = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_WEEK, (days[0].ordinal() + 2 > 7) ? 1 : days[0].ordinal() + 2);
        // Fallback
        if (oldVal > c.get(Calendar.DAY_OF_MONTH)) {
            c.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return new TimeWrapperScope(c, wadm);
    }

    /**
     * Set repeating alarm.
     */
    public WeekScope every() {
        return new WeekScope(new GregorianCalendar(), new WeekAndDayMemorie());
    }

    /**
     * Set alarm for today.
     */
    public TimeWrapperScope today() {
        return new TimeWrapperScope(new GregorianCalendar(), null);
    }

    /**
     * Adds the minutes to the current timestamp.
     */
    public final class MinuteScope {
        private MinuteScope() {
        }

        public AlarmProperties minutes(int minutes) {
            GregorianCalendar c = new GregorianCalendar();
            c.add(Calendar.MINUTE, minutes);
            return new AlarmProperties(c, null);
        }
    }

    /**
     * Sets the year of the alarm.
     */
    public final class YearScope {
        private GregorianCalendar c;

        private YearScope(GregorianCalendar c) {
            this.c = c;
        }

        public MonthScope year(int year) {
            c.set(Calendar.YEAR, year);
            return new MonthScope(c);
        }

        public MonthScope thisYear() {
            return new MonthScope(c);
        }
    }

    /**
     * Sets the month of the alarm.
     */
    public final class MonthScope {
        private GregorianCalendar c;

        private MonthScope(GregorianCalendar c) {
            this.c = c;
        }

        public DayScope month(int month) {

            // dynamic semantic check
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            c.set(Calendar.MONTH, month - 1);
            return new DayScope(c);
        }

        public DayScope thisMonth() {
            return new DayScope(c);
        }
    }

    /**
     * Sets the specific day in the month.
     */
    public final class DayScope {
        private GregorianCalendar c;

        private DayScope(GregorianCalendar c) {
            this.c = c;
        }

        public TimeWrapperScope day(int day) {

            // dynamic semantic check
            int currMonth = c.get(Calendar.MONTH);
            int highestDay;
            if (currMonth == 1) {
                highestDay = 28;
            } else if ((currMonth % 2) == 0) {
                highestDay = 31;
            } else {
                highestDay = 30;
            }
            if (day < 1 || day > highestDay) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            c.set(Calendar.DAY_OF_MONTH, day);
            return new TimeWrapperScope(c, null);
        }
    }

    /**
     * This is only needed that it looks more like natural language.
     */
    public final class TimeWrapperScope {
        private GregorianCalendar c;
        private WeekAndDayMemorie wadm;

        private TimeWrapperScope(GregorianCalendar c, WeekAndDayMemorie wadm) {
            this.c = c;
            this.wadm = wadm;
        }

        public HourScope at() {
            return new HourScope(c, wadm);
        }
    }

    /**
     * Sets the hour of the alarm.
     */
    public final class HourScope {
        private GregorianCalendar c;
        private WeekAndDayMemorie wadm;

        private HourScope(GregorianCalendar c, WeekAndDayMemorie wadm) {
            this.c = c;
            this.wadm = wadm;
        }

        public TimeMinuteScope hour(int hour) {

            // dynamic semantic check
            if (hour < 0 || hour > 23) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            c.set(Calendar.HOUR_OF_DAY, hour);
            return new TimeMinuteScope(c, wadm);
        }
    }

    /**
     * Sets the minute of the alarm
     */
    public final class TimeMinuteScope {
        private GregorianCalendar c;
        private WeekAndDayMemorie wadm;

        private TimeMinuteScope(GregorianCalendar c, WeekAndDayMemorie wadm) {
            this.c = c;
            this.wadm = wadm;
        }

        public AlarmProperties minute(int minute) {

            // dynamic semantic check
            if (minute < 0 || minute > 59) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            c.set(Calendar.MINUTE, minute);
            return new AlarmProperties(c, wadm);
        }
    }

    /**
     * Evaluates the difference between the alarms in weeks.
     * Saves the number of weeks and returns a DayWrapperScope.
     */
    public final class WeekScope {
        private GregorianCalendar c;
        private WeekAndDayMemorie wadm;

        private WeekScope(GregorianCalendar c, WeekAndDayMemorie wadm) {
            this.c = c;
            this.wadm = wadm;
        }

        public DayWrapperScope weeks(int weeks) {

            // dynamic semantic check
            if (weeks < 1) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            wadm.setWeeks(weeks);
            return new DayWrapperScope(c, wadm);
        }

        public DayWrapperScope week() {
            wadm.setWeeks(1);
            return new DayWrapperScope(c, wadm);
        }
    }

    /**
     * Evaluates which Days(Monday, Thuesday,...) the user choose.
     * Saves them and returns an TimeWrapperScope Object.
     */
    public final class DayWrapperScope {
        private GregorianCalendar c;
        private WeekAndDayMemorie wadm;

        private DayWrapperScope(GregorianCalendar c, WeekAndDayMemorie wadm) {
            this.c = c;
            this.wadm = wadm;
        }

        public TimeWrapperScope on(Day... days) {
            for (Day day : days) {
                wadm.addDay(day);
            }

            int oldVal = c.get(Calendar.DAY_OF_MONTH);
            c.set(Calendar.DAY_OF_WEEK, (days[0].ordinal() + 2 > 7) ? 1 : days[0].ordinal() + 2);
            // Fallback
            if (oldVal > c.get(Calendar.DAY_OF_MONTH)) {
                c.add(Calendar.WEEK_OF_YEAR, 1);
            }

            return new TimeWrapperScope(c, wadm);
        }
    }

    /**
     * Helper class. Saves number of weeks and the choosen days.
     * It's needed Argument in the constructor of AlarmProperties.
     */
    public final class WeekAndDayMemorie {
        private int weeks;
        private LinkedList<Day> days;

        private WeekAndDayMemorie() {
            weeks = 0;
            days = new LinkedList<Day>();
        }

        public void setWeeks(int weeks) {

            // dynamic semantic check
            if (weeks < 1) {
                throw new IllegalArgumentException(ALARM_DATE_INVALID_MSG);
            }

            this.weeks = weeks;
        }

        public void addDay(Day day) {
            if (!this.days.contains(day)) {
                days.add(day);
            }
        }

        public int getWeeks() {
            return weeks;
        }

        public LinkedList<Day> getDays() {
            return days;
        }
    }
}
