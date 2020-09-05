package org.kku.wandelpool.domain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class CacheManager
{
  private final static String FILE_NAME = "tochten.ser";

  private CacheManager()
  {
  }

  public static void save(
      HikeList hikeList)
  {
    ObjectOutput oo;
    FileOutputStream fos;

    try
    {
      //fos = WandelpoolPreferences.openFileOutput(FILE_NAME, Application.MODE_PRIVATE);
      fos = null;
      if (fos != null)
      {
        oo = new ObjectOutputStream(new BufferedOutputStream(fos));
        oo.writeObject(hikeList);
        oo.close();
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public static HikeList load()
  {
    ObjectInput oi;
    HikeList hikeList;

    hikeList = null;
    try
    {
      FileInputStream fis;

      //fis = WandelpoolPreferences.openFileInput(FILE_NAME);
      fis = null;
      if (fis != null)
      {
        oi = new ObjectInputStream(new BufferedInputStream(fis));
        hikeList = (HikeList) oi.readObject();
        oi.close();
      }
    }
    catch (FileNotFoundException ex)
    {
      return null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }

    return hikeList;
  }
}
