package org.kku.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
  public static final DateFormat FORMATTER = new SimpleDateFormat("EE dd/MM yyyy");
  public static final DateFormat SQL_FORMATTER = new SimpleDateFormat("dd-MM-yy");

  public static Date getDefaultBeginDate()
  {
    return getBeginOfDay();
  }

  public static Date getDefaultEndDate()
  {
    return add(getBeginOfDay(), Calendar.MONTH, 9);
  }

  public static Date getBeginOfDay()
  {
    return getBeginOfDay(new Date());
  }

  public static Date getBeginOfDay(
      Date date)
  {
    Calendar cal;

    cal = Calendar.getInstance();
    cal.setTime(date);
    cal.clear(Calendar.HOUR);
    cal.clear(Calendar.MINUTE);
    cal.clear(Calendar.SECOND);
    cal.clear(Calendar.MILLISECOND);

    return cal.getTime();
  }

  public static Date add(
      Date date, int field, int amount)
  {
    Calendar cal;

    cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(field, amount);

    return cal.getTime();
  }
}
