package org.kku.util;

public class StringUtil
{
  public static boolean isEmpty(
      String s)
  {
    if (s == null)
    {
      return true;
    }

    return s.trim().length() == 0;
  }

  public static boolean equals(
      String s1, String s2)
  {
    if (s1 == null && s2 == null)
    {
      return true;
    }

    if (s1 == null && s2 != null)
    {
      return false;
    }

    if (s1 != null && s2 == null)
    {
      return false;
    }

    return s1.equals(s2);
  }
}
