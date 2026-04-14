package org.teleal.common.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class Text {
	public static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";

	public static String ltrim(String str) {
		return str.replaceAll("(?s)^\\s+", "");
	}

	public static String rtrim(String str) {
		return str.replaceAll("(?s)\\s+$", "");
	}

	public static String displayFilesize(long j) {
		if (j >= 1073741824L) {
			return new BigDecimal(j / 1073741824.0) + " GiB";
		}
		if (j >= 1048576) {
			return new BigDecimal(j / 1048576.0) + " MiB";
		}
		if (j >= 1024) {
			return new BigDecimal(j / 1024.0) + " KiB";
		}
		return new BigDecimal(j) + " bytes";
	}

	public static Calendar fromISO8601String(TimeZone timeZone, String str) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_PATTERN);
		simpleDateFormat.setTimeZone(timeZone);
		try {
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.setTime(simpleDateFormat.parse(str));
			return gregorianCalendar;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toISO8601String(TimeZone timeZone, Date date) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		return toISO8601String(timeZone, gregorianCalendar);
	}

	public static String toISO8601String(TimeZone timeZone, long j) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTimeInMillis(j);
		return toISO8601String(timeZone, gregorianCalendar);
	}

	public static String toISO8601String(TimeZone timeZone, Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_PATTERN);
		simpleDateFormat.setTimeZone(timeZone);
		try {
			return simpleDateFormat.format(calendar.getTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

