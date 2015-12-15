package kg.ut.distributionalkony.Helpers;

public class ApiUrlsHelper {
	//public static String Domain = "http://178.216.210.11:3062/api/";
	public static String Domain = "http://176.126.167.9:8080/api";
	//public static String Domain = "http://10.0.3.2:4455/api";
	//public static String Domain = "http://176.126.167.9:4455/api";
	//public static String Domain = "http://10.0.3.2:45761/api";

	public static String LogOnUrl = Domain + "/account/logon";
	public static String DayOfWeekUrl = Domain + "/Routes/GetVisitDays";
	public static String OutletsByDayUrl = Domain + "/Routes/GetOutlets/{currentDay}";
	public static String GetOutletDataUrl = Domain + "/Routes/OpenOutlet";
	public static String UpdateOutletRouteItems = Domain + "/Routes/UpdateOutletRouteItems";
	public static String GetOfflineData = Domain + "/Routes/GetOfflineData";
}
