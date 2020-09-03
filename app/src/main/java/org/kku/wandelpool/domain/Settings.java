package org.kku.wandelpool.domain;

import android.app.Application;

import org.kku.wandelpool.WandelpoolApplication;
import org.kku.wandelpool.domain.WandelpoolFilter.FilterType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Settings
    implements Serializable
{
  private static final long serialVersionUID = 1L;

  private final static String FILE_NAME = "settings.ser";
  private static Settings m_instance = new Settings();

  static
  {
    load();
  }

  private List<WandelpoolFilter> m_filterList;

  private Settings()
  {
  }

  public static Settings getInstance()
  {
    return m_instance;
  }

  public static void save()
  {
    ObjectOutput oo;
    FileOutputStream fos;

    try
    {
      fos = WandelpoolApplication.getInstance().openFileOutput(FILE_NAME, Application.MODE_PRIVATE);
      oo = new ObjectOutputStream(fos);
      oo.writeObject(getInstance().getFilterList());
      oo.close();
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public static void load()
  {
    ObjectInput oi;

    try
    {
      FileInputStream fis;

      fis = WandelpoolApplication.getInstance().openFileInput(FILE_NAME);
      oi = new ObjectInputStream(fis);
      getInstance().m_filterList = (List<WandelpoolFilter>) oi.readObject();
      oi.close();
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public List<WandelpoolFilter> getFilterList()
  {
    if (m_filterList == null)
    {
      WandelpoolFilter wandelpoolFilter;

      m_filterList = new ArrayList<WandelpoolFilter>();

      wandelpoolFilter = new WandelpoolFilter();
      m_filterList.add(wandelpoolFilter);
      wandelpoolFilter.setName("Alle tochten");
      wandelpoolFilter.setDefaultFilter(true);
      wandelpoolFilter.addFilter(new WandelpoolFilter.Filter(FilterType.VAN_DATUM,
          WandelpoolFilter.MyDate.DateType.VANDAAG));
      wandelpoolFilter.addFilter(new WandelpoolFilter.Filter(FilterType.TOT_DATUM,
          WandelpoolFilter.MyDate.DateType.VANDAAG_OVER_9_MAANDEN));

      wandelpoolFilter = new WandelpoolFilter();
      m_filterList.add(wandelpoolFilter);
      wandelpoolFilter.setName("Mijn tochten");
      wandelpoolFilter.setDefaultFilter(true);
      wandelpoolFilter.addFilter(new WandelpoolFilter.Filter(FilterType.MIJN_TOCHT, Boolean.TRUE));

      save();
    }

    return m_filterList;
  }

  public WandelpoolFilter getFilterByName(
      String filterName)
  {
    for (WandelpoolFilter wandelpoolFilter : getFilterList())
    {
      if (wandelpoolFilter.getName().equals(filterName))
      {
        return wandelpoolFilter;
      }
    }

    return null;
  }
}
