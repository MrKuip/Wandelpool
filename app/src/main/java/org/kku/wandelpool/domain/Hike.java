package org.kku.wandelpool.domain;


import org.cube.wandelpool.R;
import org.kku.util.DateUtil;
import org.kku.util.StringUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Hike
    implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Map<Type, Object> m_parameterMap = new HashMap<>();

  public Date getDate()
  {
    return (Date) Type.DATE.getObject(this);
  }

  public void setDate(
      Date date)
  {
    Type.DATE.set(this, date);
  }

  public Category getCategory()
  {
    return (Category) Type.CATEGORY.getObject(this);
  }

  public void setCategory(
      Category category)
  {
    Type.CATEGORY.set(this, category);
  }

  public HikeType getHikeType()
  {
    return (HikeType) Type.HIKE_TYPE.getObject(this);
  }

  public void setHikeType(
      HikeType hikeType)
  {
    Type.HIKE_TYPE.set(this, hikeType);
  }

  public String getId()
  {
    return Type.ID.get(this);
  }

  public void setId(
      String id)
  {
    Type.ID.set(this, id);
  }

  public void setParameter(
      Type parameterType, Object parameterValue)
  {
    m_parameterMap.put(parameterType, parameterValue);
  }

  public Object getParameter(
      Type parameterType)
  {
    return m_parameterMap.get(parameterType);
  }

  public boolean hasParameter(
      Type parameterType)
  {
    Object o;

    o = m_parameterMap.get(parameterType);
    if (o == null)
    {
      return false;
    }

    if (o instanceof String)
    {
      return StringUtil.isEmpty((String) o);
    }

    return true;
  }

  public Map<Type, Object> getParameterMap()
  {
    return m_parameterMap;
  }

  public void reset()
  {
    m_parameterMap.clear();
  }

  public boolean hasDetails()
  {
    return !m_parameterMap.isEmpty();
  }

  public boolean hasUserData()
  {
    return hasParameter(Type.USER_INFO) || hasParameter(Type.USER_STARRED);
  }

  public boolean isExpired()
  {
    Date expirationDate;
    Date date;

    date = getDate();
    if (date == null)
    {
      return true;
    }

    expirationDate = new Date();
    expirationDate = DateUtil.add(expirationDate, Calendar.MONTH, -3);
    return date.before(expirationDate);
  }

  public void copyUserData(
      Hike hike)
  {
    setParameter(Type.USER_INFO, hike.getParameter(Type.USER_INFO));
    setParameter(Type.USER_STARRED, hike.getParameter(Type.USER_STARRED));
  }

  public boolean isCurrent()
  {
    // TODO: meerdaagse tochten start/enddatum
    return false;
  }

  public enum Type
  {
    ID,
    TITLE,
    SUB_TITLE,
    INTRODUCTION,
    DESCRIPTION,
    STATE,
    DATE,
    DATE_STRING,
    ALTERNATIEVE_DATUM,
    TILL_DATE,
    TRAJECTORY,
    DISTANCE,
    LOCATION,
    ORGANISER,
    CATEGORY,
    HIKE_TYPE,
    MINIMALE_AFSTAND,
    MAXIMALE_AFSTAND,
    ASSEMBLY_POINT,
    ASSEMBLY_TIME,
    OUTWARD_TRIP,
    RETURN_TRIP,
    DEELNEMERS_MINIMAAL,
    MAXIMUM_NUMBER_OF_PARTICIPANTS,
    DEELNEMERS_COMMENTAAR,
    REPORT_TO_1,
    AANMELD_BIJZONDERHEDEN1,
    PHONE_NUMBER_1,
    MOBIEL1,
    EMAIL_1,
    REPORT_TO_2,
    AANMELD_BIJZONDERHEDEN2,
    PHONE_NUMBER_2,
    MOBIEL2,
    EMAIL_2,
    USER_STARRED,
    USER_INFO;

    public String get(Hike hike)
    {
      Object o;

      o = getObject(hike);
      if (o instanceof String)
      {
        return (String) o;
      }

      return null;
    }

    public <T> T getObject(Hike hike)
    {
      return (T) hike.getParameter(this);
    }

    public void set(Hike hike, Serializable value)
    {
      hike.setParameter(this, value);
    }
  }

  public enum HikeType
  {
    EEN_DAG("Een daags", "0"),
    MEERDERE_DAGEN("Meerdere dagen", "1");

    private String mi_soortTocht;
    private String mi_id;

    HikeType(String soortTocht, String id)
    {
      mi_soortTocht = soortTocht;
      mi_id = id;
    }

    public String getId()
    {
      return mi_id;
    }

    public String toString()
    {
      return mi_soortTocht;
    }
  }

  public enum Location
  {
    Groningen("Groningen", "1"),
    Friesland("Friesland", "2"),
    Drenthe("Drenthe", "3"),
    Overijssel("Overijssel", "4"),
    Gelderland("Gelderland", "5"),
    Utrecht("Utrecht", "6"),
    NoordHolland("Noord-Holland", "7"),
    ZuidHolland("Zuid-Holland", "8"),
    Zeeland("Zeeland", "9"),
    NoordBrabant("Noord-Brabant", "10"),
    Limburg("Limburg", "11"),
    Flevoland("Flevoland", "12"),
    Onbekend("Onbekend", "13"),
    Belgie("België", "14"),
    Bulgarije("Bulgarije", "15"),
    Duitsland("Duitsland", "18"),
    Frankrijk("Frankrijk", "23"),
    Ghana("Ghana", "25"),
    Griekenland("Griekenland", "21"),
    GrootBrittanie("Groot-Brittannië", "24"),
    IndoChina("Indo China", "26"),
    Italie("Italië", "27"),
    Luxemburg("Luxemburg", "28"),
    Mali("Mali", "29"),
    Nepal("Nepal", "30"),
    NieuwZeeland("Nieuw-Zeeland", "34"),
    Oostenrijk("Oostenrijk", "15"),
    Polen("Polen", "31"),
    Portugal("Portugal", "32"),
    Roemenie("Roemenië", "33"),
    Spanje("Spanje", "22"),
    Tibet("Tibet", "17"),
    Tsjechie("Tsjechië", "20"),
    Zwitserland("Zwitserland", "19");

    public String mi_id;
    public String mi_text;

    Location(String id, String text)
    {
      mi_id = id;
      mi_text = text;
    }

    public String getId()
    {
      return mi_id;
    }

    public String getText()
    {
      return mi_text;
    }
  }

  public enum Category
  {
    SCHOEN_00("-1", "0 schoentjes (stilstaan)", R.drawable.schoen00),
    SCHOEN_05("-1", "0,5 schoentje", R.drawable.schoen05),
    SCHOEN_10("1", "1 schoentje (tot 4 km/uur)", R.drawable.schoen10),
    SCHOEN_15("4", "1,5 schoentje (4 - 4,5 km/uur)", R.drawable.schoen15),
    SCHOEN_20("5", "2 schoentjes (4,5 - 5 km/uur)", R.drawable.schoen20),
    SCHOEN_25("6", "2,5 schoentjes (5 - 5,5 km/uur)", R.drawable.schoen25),
    SCHOEN_30("7", "3 schoentjes (vanaf 5,5 km/uur)", R.drawable.schoen30),
    BUITEN_CATEGORIE("8", "Buitencategorie", R.drawable.schoen00),
    EXCURSIE("9", "Excursie", R.drawable.schoen00),
    RUSTIG_FIETSTEMPO("10", "Rustig fietstempo", R.drawable.schoen00),
    GEMIDDELD_FIETSTEMPO("11", "Gemiddeld fietstempo", R.drawable.schoen00),
    FORS_FIETSTEMPO("12", "Fors fietstempo", R.drawable.schoen00),
    NORDIC_WALKING("13", "Nordic Walking", R.drawable.schoen00),
    EXCURSIES("14", "Excursies", R.drawable.schoen00),
    EXCURSIE_2("15", "Excursie", R.drawable.schoen00);

    public String mi_id;
    public String mi_text;
    private int m_image;

    Category(String id, String text, int image)
    {
      mi_id = id;
      mi_text = text;
      m_image = image;
    }

    public String getId()
    {
      return mi_id;
    }

    public int getImage()
    {
      return m_image;
    }

    public String getText()
    {
      return mi_text;
    }
  }
}
