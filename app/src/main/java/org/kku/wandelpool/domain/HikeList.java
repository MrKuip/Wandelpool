package org.kku.wandelpool.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HikeList
    implements Serializable
{
  private static final long serialVersionUID = 1L;

  private List<Hike> m_hikeList = new ArrayList<>();
  private Map<String, Hike> m_hikeMapById;

  public HikeList()
  {
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

    newHikeList.m_hikeMapById = null;

    return newHikeList;
  }

  public List<Hike> getList()
  {
    return m_hikeList;
  }

  public void reset()
  {
    for (Hike hike : m_hikeList)
    {
      hike.reset();
    }
  }

  public void add(
      Hike hike)
  {
    getList().add(hike);
  }

  public Hike getHikeById(
      String hikeId)
  {
    if (m_hikeMapById == null)
    {
      m_hikeMapById = new HashMap<>();
      for (Hike hike : getList())
      {
        m_hikeMapById.put(hike.getId(), hike);
      }
    }

    return m_hikeMapById.get(hikeId);
  }

  public void sort()
  {
    Collections.sort(m_hikeList, new HikeComparator());
  }

  static private class HikeComparator
      implements Comparator<Hike>
  {
    @Override
    public int compare(
        Hike hike1, Hike hike2)
    {
      Date d1;
      Date d2;

      d1 = hike1.getDate();
      d2 = hike2.getDate();

      assert d1 != null;
      assert d2 != null;

      if (d1 == null && d2 == null)
      {
        return 0;
      }

      if (d1 == null && d2 != null)
      {
        return -1;
      }

      if (d1 != null && d2 == null)
      {
        return 1;
      }

      if (d1.before(d2))
      {
        return -1;
      }

      if (d1.after(d2))
      {
        return 1;
      }

      return Integer.valueOf(hike1.getId()) - Integer.valueOf(hike2.getId());
    }
  }
}
