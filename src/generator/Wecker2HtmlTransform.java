package generator;

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import alarmclock.AlarmBuilder;
import alarmclock.AlarmProperties;
import alarmclock.Day;

public class Wecker2HtmlTransform {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws Exception {

		AlarmBuilder alarm = new AlarmBuilder();
		LinkedList<AlarmProperties> apl = new LinkedList<AlarmProperties>();

		apl.add(alarm.on().thisYear().thisMonth().day(28).at().hour(22).minute(12));
		apl.add(alarm.on().year(2017).month(12).day(12).at().hour(22).minute(12));
		apl.add(alarm.today().at().hour(22).minute(12));
		apl.add(alarm.in().minutes(45));
		apl.add(alarm.on(Day.SATURDAY).at().hour(22).minute(12));

		VelocityEngine ve = new VelocityEngine();
		ve.init();

		ArrayList veList = new ArrayList();
		for (AlarmProperties ap : apl) {
			Map map = new HashMap();
			map.put("year", ap.getYear());
			map.put("month", ap.getMonth());
			map.put("day", ap.getDay());
			map.put("hour", ap.getHour());
			map.put("minute", ap.getMinute());
			veList.add(map);
		}

		VelocityContext context = new VelocityContext();
		context.put("alarmList", veList);

		Template t = ve.getTemplate("src/generator/alarm_html.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		File file = new File("out.html");
		try (PrintWriter out = new PrintWriter(file)) {
			out.print(writer.toString());
			Desktop.getDesktop().open(file);
		}

		System.out.println(writer.toString());
	}

}
