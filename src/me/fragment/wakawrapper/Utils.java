package me.fragment.wakawrapper;

import java.time.Instant;
import java.util.Date;

public class Utils {

	public static Date getDateFromString(String date) {
		return Date.from(Instant.parse(date));
	}

}
