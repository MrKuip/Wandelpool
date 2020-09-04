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
  private String m_id;
  private Date m_date;
  private String m_dateString;
  private String m_title;
  private String m_trajectory;
  private String m_distance;
  private Categorie m_category;
  private String m_location;
  private String m_organiser;
  private HikeType m_hikeType;
  private Map<Type, String> m_parameterMap = new HashMap<>();

  public Date getDate()
  {
    return m_date;
  }

  public void setDate(
      Date datum)
  {
    m_date = datum;
  }

  public String getTitle()
  {
    return m_title;
  }

  public void setTitle(
      String title)
  {
    m_title = title;
  }

  public String getTrajectory()
  {
    return m_trajectory;
  }

  public void setTrajectory(
      String trajectory)
  {
    m_trajectory = trajectory;
  }

  public String getDistance()
  {
    return m_distance;
  }

  public void setDistance(
      String distance)
  {
    m_distance = distance;
  }

  public Categorie getCategory()
  {
    return m_category;
  }

  public void setCategory(
      Categorie category)
  {
    m_category = category;
  }

  public String getLocation()
  {
    return m_location;
  }

  public void setLocation(
      String location)
  {
    m_location = location;
  }

  public String getOrganiser()
  {
    return m_organiser;
  }

  public void setOrganiser(
      String organiser)
  {
    m_organiser = organiser;
  }

  public HikeType getSoortTocht()
  {
    return m_hikeType;
  }

  public void SetSoortTocht(
      HikeType soortTocht)
  {
    m_hikeType = soortTocht;
  }

  public String getDateString()
  {
    return m_dateString;
  }

  public void setDateString(
      String datumString)
  {
    m_dateString = datumString;
  }

  public String getId()
  {
    return m_id;
  }

  public void setId(
      String id)
  {
    m_id = id;
  }

  public void setParameter(
      Type parameterType, String parameterValue)
  {
    m_parameterMap.put(parameterType, parameterValue);
  }

  public String getParameter(
      Type parameterType)
  {
    return m_parameterMap.get(parameterType);
  }

  public boolean hasParameter(
      Type parameterType)
  {
    return !StringUtil.isEmpty(m_parameterMap.get(parameterType));
  }

  public Map<Type, String> getParameterMap()
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

    if (m_date == null)
    {
      return true;
    }

    expirationDate = new Date();
    expirationDate = DateUtil.add(expirationDate, Calendar.MONTH, -3);
    return m_date.before(expirationDate);
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
    TITEL,
    SUB_TITEL,
    INTRODUCTION,
    DESCRIPTION,
    STATE,
    DATE,
    ALTERNATIEVE_DATUM,
    TILL_DATE,
    TRAJECTORY,
    DISTANCE,
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
    USER_INFO,

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

  public enum Categorie
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

    Categorie(String id, String text, int image)
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
