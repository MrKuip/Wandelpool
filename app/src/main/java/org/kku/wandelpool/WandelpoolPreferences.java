package org.kku.wandelpool;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileOutputStream;

public class WandelpoolPreferences
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

  public static FileOutputStream openFileOutput(String fileName, int modePrivate)
  {
    return null;
  }
}
