package org.kku.wandelpool.domain;

import org.kku.util.DateUtil;
import org.kku.util.ObjectUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WandelpoolFilter
    implements java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  private String m_name;
  private boolean m_defaultFilter;
  private List<Filter> m_filterList = new ArrayList<Filter>();

  public WandelpoolFilter()
  {
  }

  public String getName()
  {
    return m_name;
  }

  public void setName(
      String name)
  {
    m_name = name;
  }

  public boolean isDefaultFilter()
  {
    return m_defaultFilter;
  }

  public void setDefaultFilter(
      boolean defaultFilter)
  {
    m_defaultFilter = defaultFilter;
  }

  public Filter setFilter(
      Filter filter)
  {
    removeFilter(filter);
    return addFilter(filter);
  }

  public Filter addFilter(
      Filter filter)
  {
    if (filter.getFilterValue() == null)
    {
      return null;
    }

    if (m_filterList.contains(filter))
    {
      return m_filterList.get(m_filterList.indexOf(filter));
    }

    m_filterList.add(filter);
    Collections.sort(m_filterList);

    return filter;
  }

  public void removeFilter(
      Filter filter)
  {
    m_filterList.remove(filter);
  }

  public List<Filter> getFilterList()
  {
    return m_filterList;
  }

  public HikeList filter(
      HikeList tochten)
  {
    HikeList result;

    result = new HikeList();
    for (Hike t : tochten.getList())
    {
      if (!accept(t))
      {
        continue;
      }

      result.add(t);
    }

    return result;
  }


  private boolean accept(
      Hike t)
  {
    for (Filter filter : getFilterList())
    {
      if (!filter.accept(t))
      {
        return false;
      }
    }

    return true;
  }

  public enum FilterType
  {
    SOORT_TOCHT("Soort tocht")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return ObjectUtil.equals(hike.getSoortTocht(), filterValue);
          }
        },
    VAN_DATUM("Van datum")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return ((Date) filterValue).after(hike.getDate());
          }
        },
    TOT_DATUM("Tot datum")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return ((Date) filterValue).before(hike.getDate());
          }
        },
    LOCATIE("Locatie")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return ObjectUtil.equals(hike.getLocation(), filterValue);
          }
        },
    CATEGORIE("Categorie")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return ObjectUtil.equals(hike.getCategory(), filterValue);
          }
        },
    VAN_AFSTAND("Van afstand")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return intValue(hike.getDistance()) >= intValue(filterValue.toString());
          }
        },
    TOT_AFSTAND("Tot afstand")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return intValue(hike.getDistance()) <= intValue(filterValue.toString());
          }
        },
    ORGANISATOR("Organisator")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return hike.getOrganiser().contains(filterValue.toString());
          }
        },
    DAG_VAN_DE_WEEK("Dag van de week")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            return hike.getDate().getDay() == intValue(filterValue);
          }
        },
    MIJN_TOCHT("Mijn tocht")
        {
          @Override
          public boolean accept(
              Hike hike, Object filterValue)
          {
            boolean starred;

            starred = booleanValue(filterValue);
            if (!starred)
            {
              return true;
            }

            return ObjectUtil.equals(hike.getParameter(Hike.Type.USER_STARRED), filterValue);
          }
        };

    private String m_name;

    FilterType(String name)
    {
      m_name = name;
    }

    private static int intValue(
        Object o)
    {
      return Integer.parseInt(o.toString());
    }

    private static boolean booleanValue(
        Object o)
    {
      return Boolean.parseBoolean(o.toString());
    }

    public String getName()
    {
      return m_name;
    }

    public String toString()
    {
      return getName();
    }

    abstract public boolean accept(
        Hike hike, Object filterValue);
  }

  public static class MyDate
      implements Serializable
  {
    private static final long serialVersionUID = 1L;
    private DateType mi_dateType;
    private Date mi_date;

    public MyDate(DateType dateType)
    {
      this(dateType, null);
    }

    public MyDate(DateType dateType, Date date)
    {
      mi_dateType = dateType;
      mi_date = date;
    }

    public DateType getDateType()
    {
      return mi_dateType;
    }

    public Date getDate()
    {
      switch (mi_dateType)
      {
        default:
        case VANDAAG:
          return DateUtil.getBeginOfDay();
        case VANDAAG_OVER_9_MAANDEN:
          return DateUtil.add(DateUtil.getBeginOfDay(), Calendar.MONTH, 9);
        case DATE:
          return mi_date;
      }
    }

    @Override
    public boolean equals(
        Object o)
    {
      MyDate other;

      if (!(o instanceof MyDate))
      {
        return false;
      }

      other = (MyDate) o;
      if (!ObjectUtil.equals(mi_dateType, other.mi_dateType))
      {
        return false;
      }

      return ObjectUtil.equals(getDate(), other.getDate());
    }

    @Override
    public String toString()
    {
      if (getDateType() == DateType.DATE)
      {
        return DateUtil.FORMATTER.format(mi_date);
      }

      return getDateType().getDescription();
    }

    public enum DateType
    {
      VANDAAG("Vandaag"),
      VANDAAG_OVER_9_MAANDEN("Vandaag over 9 maanden"),
      DATE("Datum");

      private String mii_description;

      DateType(String description)
      {
        mii_description = description;
      }

      public String getDescription()
      {
        return mii_description;
      }

      public String toString()
      {
        return getDescription();
      }
    }
  }

  static public class Filter
      implements Comparable<Filter>, java.io.Serializable
  {
    private static final long serialVersionUID = 1L;
    private FilterType mi_filterType;
    private Object mi_filterValue;

    public Filter(FilterType filterType, Object filterValue)
    {
      mi_filterType = filterType;
      mi_filterValue = filterValue;
    }

    public FilterType getFilterType()
    {
      return mi_filterType;
    }

    public Object getFilterValue()
    {
      return mi_filterValue;
    }

    public boolean accept(
        Hike hike)
    {
      return mi_filterType.accept(hike, mi_filterValue);
    }

    @Override
    public int hashCode()
    {
      return mi_filterValue.hashCode();
    }

    public boolean equals(
        Object o)
    {
      Filter other;

      if (!(o instanceof Filter))
      {
        return false;
      }

      other = (Filter) o;

      if (mi_filterType != other.mi_filterType)
      {
        return false;
      }

      return ObjectUtil.equals(mi_filterValue, other.mi_filterValue);
    }

    @Override
    public int compareTo(
        Filter other)
    {
      int result;

      result = mi_filterType.ordinal() - other.mi_filterType.ordinal();
      if (result != 0)
      {
        return result;
      }

      return mi_filterValue.toString().compareTo(other.mi_filterValue.toString());
    }
  }
}
