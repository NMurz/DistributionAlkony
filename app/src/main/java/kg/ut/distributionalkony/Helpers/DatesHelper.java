package kg.ut.distributionalkony.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kg.ut.distributionalkony.Models.TimeModel;
import android.util.Log;

public class DatesHelper {
	public final static Date TimeStringToDate(String timeString){
		timeString = timeString.substring(timeString.indexOf("(")+1, timeString.indexOf(")"));
		
		String[] timeSegments = timeString.split("-");
		
		Long timeZoneOffSet = Long.valueOf(timeSegments[1]) * 36000; // (("0100" / 100) * 3600 * 1000)
		Long millis = Long.valueOf(timeSegments[0]);
		Date date = new Date(millis + timeZoneOffSet);		
			
		return date;
	}
	
	public final static TimeModel TimeStringToTime(String timeString){

		//************* Departure Time ***************//
		if (timeString.indexOf("M")==-1 ){
			timeString = timeString + "0M";
		}
		
		timeString = timeString.substring(timeString.indexOf("PT")+2, timeString.indexOf("M"));
		String[] timeSegments = timeString.split("H");	
		int hh = Integer.parseInt(timeSegments[0]);
		int	mm = Integer.parseInt(timeSegments[1]);

		
		TimeModel timeModel = new TimeModel();
		timeModel.HH = hh;
		timeModel.MM = mm;
		
		return timeModel;
	}
	
	public final static Date DateTimeStringToDate(String dateTime){
		Date date = new Date();
		String  datePart = dateTime.substring(0, 10);
				
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(datePart);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

}
