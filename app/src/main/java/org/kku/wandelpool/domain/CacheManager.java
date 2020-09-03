package org.kku.wandelpool.domain;

import android.app.Application;

import org.kku.wandelpool.WandelpoolApplication;

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
      fos = WandelpoolApplication.getInstance().openFileOutput(FILE_NAME, Application.MODE_PRIVATE);
      oo = new ObjectOutputStream(new BufferedOutputStream(fos));
      oo.writeObject(hikeList);
      oo.close();
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public static HikeList load()
  {
    ObjectInput oi;
    HikeList tochten;

    try
    {
      FileInputStream fis;

      fis = WandelpoolApplication.getInstance().openFileInput(FILE_NAME);
      oi = new ObjectInputStream(new BufferedInputStream(fis));
      tochten = (HikeList) oi.readObject();
      oi.close();
    } catch (FileNotFoundException ex)
    {
      return null;
    } catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }

    return tochten;
  }

  public static HikeList merge(
      HikeList oldHikeList, HikeList newHikeList)
  {
    for (Hike hike : oldHikeList.getList())
    {
      Hike newHike;

      if (hike.isCurrent())
      {
        if (hike.hasDetails())
        {
          continue;
        }

        if (!hike.hasUserData())
        {
          continue;
        }
      }

      if (!hike.isExpired())
      {
        continue;
      }

      newHike = newHikeList.getHikeById(hike.getId());
      if (newHike != null)
      {
        newHike.copyUserData(hike);
      }
      else
      {
        newHikeList.add(newHike);
      }
    }

    return newHikeList;
  }
}
