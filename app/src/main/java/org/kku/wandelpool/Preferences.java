package org.kku.wandelpool;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Preferences
{
  private static Context mi_context;

  public static void init(WandelpoolActivity activity)
  {
    mi_context = activity.getApplicationContext();
  }

  public static SharedPreferences getSharedPreferences(String fileName)
  {
    return mi_context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
  }

  public static File getFile(String fileName)
  {
    return new File(mi_context.getFilesDir(), fileName);
  }
}
