package alarmclock;

import java.util.LinkedList;

import static alarmclock.Day.*;

public class TestAlarmClock {

    public static void main(String[] args) throws InterruptedException {
        AlarmBuilder alarm = new AlarmBuilder();
        LinkedList<AlarmProperties> apl = new LinkedList<AlarmProperties>();

        // alarm in minutes
        apl.add(alarm.in().minutes(5));

        // alarm on date
        apl.add(alarm.on().thisYear().thisMonth().day(28).at().hour(22).minute(12));
        apl.add(alarm.on().year(2017).month(12).day(12).at().hour(22).minute(12));
        apl.add(alarm.today().at().hour(22).minute(12));
        apl.add(alarm.on(Day.SATURDAY).at().hour(22).minute(12));

        // repeat alarm
        apl.add(alarm.every().weeks(2).on(Day.SUNDAY, Day.SATURDAY).at().hour(22).minute(12));
        apl.add(alarm.every().week().on(Day.FRIDAY).at().hour(22).minute(12));

        // print configurations
        for (AlarmProperties ap : apl) {
            System.out.println(ap);
        }

        System.out.println();

        // not allowed --> alarm in the past
        try {
            apl.add(alarm.in().minutes(-3));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            apl.add(alarm.today().at().hour(2).minute(12));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            apl.add(alarm.on().year(2000).month(12).day(12).at().hour(22).minute(12));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();

        // not allowed --> illegal arguments
        try {
            apl.add(alarm.on().year(2016).month(99).day(1).at().hour(1).minute(1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            apl.add(alarm.on().year(2016).month(1).day(99).at().hour(1).minute(1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            apl.add(alarm.on().year(2016).month(1).day(1).at().hour(99).minute(1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            apl.add(alarm.on().year(2016).month(1).day(1).at().hour(1).minute(99));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
