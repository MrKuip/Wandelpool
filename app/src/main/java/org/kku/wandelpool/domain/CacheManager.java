package org.kku.wandelpool.domain;

import org.kku.wandelpool.Preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class CacheManager
{
  private final static String FILE_NAME = "hikes.ser";

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
      fos = new FileOutputStream(Preferences.getFile(FILE_NAME));
      oo = new ObjectOutputStream(new BufferedOutputStream(fos));
      oo.writeObject(hikeList);
      oo.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public static HikeList load()
  {
    ObjectInput oi;
    HikeList hikeList;

    try
    {
      FileInputStream fis;

      fis = new FileInputStream(Preferences.getFile(FILE_NAME));
      oi = new ObjectInputStream(new BufferedInputStream(fis));
      hikeList = (HikeList) oi.readObject();
      oi.close();

      return hikeList;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}
