package com.shu.shupractice.app.bt; // this is my custom package.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingLogSessionProcess {

	private static int PARTS_SPLIT_LENTH = 3;
	private static String SIMPLE_DATE_FORMAT = "HH:mm:ss";
	private static String START_ACTIVITY = "Start";
	private static String END_ACTIVITY = "End";
	private static String PARTS_LINE_SPLITER = "\\s+";

	static class InformationalSession {
		List<Date> startTimes = new ArrayList<>();
		long overallTimeDuration = 0;
		int numberOfSessions = 0;
	}

	private static Date getParseDateAndTime(String timeStr) throws ParseException {
		SimpleDateFormat sd = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		return sd.parse(timeStr);
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Please check argument log_file in LogSessionProcess...");
			System.exit(1);
		}

		String logFilePath = args[0];

		Map<String, InformationalSession> infoSessions = new HashMap<>();
		Date minTime = null;
		Date maxTime = null;

		try (BufferedReader bufferRd = new BufferedReader(new FileReader(logFilePath))) {
			String singleLine;
			while ((singleLine = bufferRd.readLine()) != null) {
				String[] lineParts = singleLine.trim().split(PARTS_LINE_SPLITER);
				if (lineParts.length != PARTS_SPLIT_LENTH)
					continue;

				String timeInStr = lineParts[0];
				String user = lineParts[1];
				String activity = lineParts[2];

				try {
					Date logTime = getParseDateAndTime(timeInStr);

					if (minTime == null || logTime.before(minTime)) {
						minTime = logTime;
					}
					if (maxTime == null || logTime.after(maxTime)) {
						maxTime = logTime;
					}

					infoSessions.putIfAbsent(user, new InformationalSession());
					InformationalSession infoSession = infoSessions.get(user);

					if (START_ACTIVITY.equals(activity)) {
						infoSession.startTimes.add(logTime);
					} else if (END_ACTIVITY.equals(activity)) {
						if (!infoSession.startTimes.isEmpty()) {
							Date startTime = infoSession.startTimes.remove(0);
							long duration = (logTime.getTime() - startTime.getTime()) / 1000;
							infoSession.overallTimeDuration += duration;
						} else {
							long duration = (logTime.getTime() - minTime.getTime()) / 1000;
							infoSession.overallTimeDuration += duration;
						}
						infoSession.numberOfSessions++;
					}

				} catch (ParseException e) {
					System.out.println("The date format is invalid...");

				}
			}

			for (Map.Entry<String, InformationalSession> mapEntry : infoSessions.entrySet()) {
				InformationalSession sessionInfo = mapEntry.getValue();

				for (Date startTime : sessionInfo.startTimes) {
					long duration = (maxTime.getTime() - startTime.getTime()) / 1000;
					sessionInfo.overallTimeDuration += duration;
					sessionInfo.numberOfSessions++;
				}
			}

			for (Map.Entry<String, InformationalSession> mapEntry : infoSessions.entrySet()) {
				String userName = mapEntry.getKey();
				InformationalSession sessionInfo = mapEntry.getValue();
				System.out.printf("%s %d %d%n", userName, sessionInfo.numberOfSessions,
						sessionInfo.overallTimeDuration);
			}

		} catch (IOException ex) {
			System.err.println("getting error while reading the file: " + ex.getMessage());
		}
	}
}
