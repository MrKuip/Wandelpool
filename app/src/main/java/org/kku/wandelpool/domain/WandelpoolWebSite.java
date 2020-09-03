package org.kku.wandelpool.domain;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kku.util.DateUtil;
import org.kku.util.StringUtil;
import org.kku.wandelpool.WandelpoolApplication;
import org.kku.wandelpool.domain.Hike.Categorie;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WandelpoolWebSite
{
  public static final String LOGIN_PREFERENCES = "LOGIN_PREFERENCES";
  public static final String USERNAME_PREFERENCE = "userName";
  public static final String PASSWORD_PREFERENCE = "password";
  private static final String BASE_URL = "https://www.wandelpool.nl/";
  private static final String LOGIN_URL = BASE_URL + "login.php";
  private static final String HIKES_URL = BASE_URL + "tochten-overzicht";
  private static final String HIKE_DETAIL_URL = BASE_URL + "tocht/toon";
  private static final String LOGIN_USER_REQUEST = "gebruikersnaam";
  private static final String LOGIN_PASSWORD_REQUEST = "wachtwoord";
  private static final String LOGIN_SUBMITTED_REQUEST = "submitted_login";
  private static final String LOGIN_SUBMITTED_REQUEST_DATA = "2";
  private static final String LOGIN_USER_COOKIE = "deelnemerid";
  private static final String LOGIN_SESSION_COOKIE = "PHPSESSID";
  private static final String HIKE_LIST_SUBMIT_REQUEST_DATA = "Zoek op!";
  private static final String HIKE_DETAIL_WANDELINGID_REQUEST = "tocht_id";
  private static final int TIMEOUT = 30000;
  private static WandelpoolWebSite m_instance = new WandelpoolWebSite();
  private Map<String, String> m_cookies;
  private HikeList m_hikeList;

  private WandelpoolWebSite()
  {
  }

  static public WandelpoolWebSite getInstance()
  {
    return m_instance;
  }

  public static void main(
      String[] args)
  {
    try
    {
      int maxCount = 0;
      int count = 0;
      WandelpoolWebSite wandelpoolSite;

      wandelpoolSite = new WandelpoolWebSite();

      for (Hike hike : wandelpoolSite.getHikeList().getList())
      {
        count++;

        System.out.printf("%03d %s %10.10s:  %-10.10s %-15.15s %s%n", count, hike.getId(),
            hike.getCategory(), hike.getDateString(), hike.getOrganisor(), hike.getTitle());

        if (count >= maxCount)
        {
          continue;
        }

        System.out.println("  id        =" + hike.getId());
        System.out.println("  category  =" + hike.getCategory());
        System.out.println("  date      =" + hike.getDate());
        System.out.println("  dateString=" + hike.getDateString());
        System.out.println("  distance  =" + hike.getDistance());
        System.out.println("  location  =" + hike.getLocation());
        System.out.println("  organizor =" + hike.getOrganisor());
        System.out.println("  title     =" + hike.getTitle());
        System.out.println("  traject   =" + hike.getTrajectory());

        wandelpoolSite.getHikeDetails(hike);

        for (Hike.Type type : Hike.Type.values())
        {
          System.out.printf("  %-20.20s = %s%n", type, hike.getParameter(type));
        }
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Login the website of the wandelpool.
   */

  public boolean login(
      String userName, String password)
      throws Exception
  {
    Connection connection;

    connection = Jsoup.connect(LOGIN_URL);
    connection.data(LOGIN_USER_REQUEST, userName);
    connection.data(LOGIN_PASSWORD_REQUEST, password);
    connection.data(LOGIN_SUBMITTED_REQUEST, LOGIN_SUBMITTED_REQUEST_DATA);
    connection.timeout(TIMEOUT);
    connection.post();

    m_cookies = connection.response().cookies();

    if (m_hikeList != null)
    {
      m_hikeList.reset();
    }

    if (isLoggedIn())
    {
      persistCredentials(userName, password);
    }

    return isLoggedIn();
  }

  public void login()
      throws Exception
  {
    SharedPreferences pref;
    String userName;
    String password;

    pref = WandelpoolApplication.getInstance().getSharedPreferences(LOGIN_PREFERENCES,
        Activity.MODE_PRIVATE);
    userName = pref.getString(USERNAME_PREFERENCE, "");
    password = pref.getString(PASSWORD_PREFERENCE, "");

    if (StringUtil.isEmpty(userName))
    {
      return;
    }

    login(userName, password);
  }

  public void logout()
  {
    if (m_hikeList != null)
    {
      m_hikeList.reset();
      m_hikeList = null;
    }

    if (m_cookies != null)
    {
      m_cookies = null;
    }

    persistCredentials("", "");
  }

  private void persistCredentials(
      String userName, String password)
  {
    Editor edit;

    edit = WandelpoolApplication.getInstance()
                                .getSharedPreferences(LOGIN_PREFERENCES, Activity.MODE_PRIVATE).edit();
    edit.putString(USERNAME_PREFERENCE, userName);
    edit.putString(PASSWORD_PREFERENCE, password);
    edit.commit();
  }

  public boolean isLoggedIn()
  {
    return m_cookies != null && m_cookies.containsKey(LOGIN_USER_COOKIE);
  }

  public HikeList getHikeList()
      throws WandelpoolException
  {
    if (m_hikeList != null)
    {
      return m_hikeList;
    }

    //m_hikeList = HikeList.load();
    if (m_hikeList != null)
    {
      return m_hikeList;
    }

    Future<HikeList> a = Executors.newFixedThreadPool(1).submit(
        new GetHikeList(DateUtil.getDefaultBeginDate(), DateUtil.getDefaultEndDate()));
    try
    {
      m_hikeList = a.get(20, TimeUnit.SECONDS);
    } catch (Exception e)
    {
      throw new WandelpoolException(e);
    }

    //HikeList.save(m_hikeList);

    return m_hikeList;
  }

  /**
   * Scrape the hikes of the website.
   */
  public HikeList getHikeList(
      Date beginDate, Date endDate)
      throws WandelpoolException
  {
    Document doc;
    int tableCount;
    int trCount;
    int tdCount;
    Connection connection;
    HikeList hikeList;

    hikeList = new HikeList();

    try
    {
      Element table;

      for (Hike.SoortTocht soortTocht : Hike.SoortTocht.values())
      {
        connection = Jsoup.connect(HIKES_URL);
        connection.data("meerdaags", soortTocht.getId());
        connection.data("datum", DateUtil.SQL_FORMATTER.format(beginDate));
        connection.data("einddatum", DateUtil.SQL_FORMATTER.format(endDate));
        connection.data("submit", HIKE_LIST_SUBMIT_REQUEST_DATA);
        doc = connection.post();

        tableCount = 0;

        table = doc.select("table").get(1);
        trCount = 0;
        for (Element tr : table.getElementsByTag("tr"))
        {
          Hike hike;

          trCount++;
          if (trCount == 1)
          {
            continue;
          }

          hike = new Hike();
          hikeList.add(hike);

          tdCount = 0;
          for (Element td : tr.getElementsByTag("td"))
          {
            tdCount++;
            switch (tdCount)
            {
              case 1:
                String datumString;
                Elements pList;

                pList = td.select("p");

                hike.setId(td.select("form").select("input[name$=item]").attr("value"));
                datumString = pList.get(0).text();
                hike.setDate(parseDate(datumString));
                hike.setDateString(datumString);
                hike.setParameter(Hike.Type.STATE, pList.get(1).text());
                break;
              case 2:
                hike.setTitle(td.select("p").get(0).text());
                break;
              case 3:
                Elements elements;
                elements = td.select("p");
                hike.setDistance(elements.get(0).text());
                hike.setLocation(elements.get(1).text());
                break;
              case 4:
                hike.setCategory(getCategory(td.getElementsByTag("img").get(0).attr("src")));
                hike.setOrganisor(td.getElementsByTag("p").get(1).text());
                break;
            }
          }
        }
      }
      hikeList.sort();
    } catch (Exception e)
    {
      throw new WandelpoolException(e);
    }

    return hikeList;
  }

  private Categorie getCategory(
      String category)
  {
    if (!StringUtil.isEmpty(category))
    {
      if (category.contains("schoen5."))
      {
        return Categorie.SCHOEN_05;
      }
      if (category.contains("schoen1."))
      {
        return Categorie.SCHOEN_10;
      }
      if (category.contains("schoen15."))
      {
        return Categorie.SCHOEN_15;
      }
      if (category.contains("schoen2."))
      {
        return Categorie.SCHOEN_20;
      }
      if (category.contains("schoen25."))
      {
        return Categorie.SCHOEN_25;
      }
      if (category.contains("schoen3."))
      {
        return Categorie.SCHOEN_30;
      }
    }

    return Categorie.SCHOEN_00;
  }

  public Hike getHikeDetails(
      String hikeId)
      throws Exception
  {
    return getHikeList().getHikeById(hikeId);
  }

  /**
   * Scrape the details of a walk from the website.
   */

  public Hike getHikeDetails(
      Hike hike)
      throws Exception
  {
    Document doc;
    Connection connection;
    Elements content3List;
    Hike.Type currentType;

    if (hike == null)
    {
      return null;
    }

    if (hike.hasDetails())
    {
      return hike;
    }

    connection = Jsoup.connect(HIKE_DETAIL_URL);
    if (isLoggedIn())
    {
      connection = connection.cookie(LOGIN_USER_COOKIE, m_cookies.get(LOGIN_USER_COOKIE));
      connection = connection.cookie(LOGIN_SESSION_COOKIE, m_cookies.get(LOGIN_SESSION_COOKIE));
    }
    connection = connection.data(HIKE_DETAIL_WANDELINGID_REQUEST, hike.getId());
    connection.timeout(TIMEOUT);
    doc = connection.post();

    content3List = doc.select("div[id=content3]");
    if (content3List != null)
    {
      Element content3;
      Elements trList;

      content3 = content3List.get(0);
      trList = content3.getElementsByTag("tr");

      hike.setParameter(Hike.Type.TITEL, content3.getElementsByTag("h1").text());
      if (trList.get(0).getElementsByTag("i").size() > 0)
      {
        hike.setParameter(Hike.Type.SUB_TITEL, trList.get(0).getElementsByTag("i").get(0).text());
      }

      currentType = null;
      for (int index = 0; index < trList.size(); index++)
      {
        String key;
        Elements tdList;
        Hike.Type previousType;

        previousType = currentType;
        currentType = null;

        tdList = trList.get(index).getElementsByTag("td");
        key = tdList.get(0).text();


        if (previousType == Hike.Type.REPORT_TO_1 || previousType == Hike.Type.REPORT_TO_2)
        {
          Hike.Type aanmeld_bijzonderheden;
          Hike.Type mobiel;

          if (previousType == Hike.Type.REPORT_TO_1)
          {
            aanmeld_bijzonderheden = Hike.Type.AANMELD_BIJZONDERHEDEN1;
            mobiel = Hike.Type.MOBIEL1;
          }
          else
          {
            aanmeld_bijzonderheden = Hike.Type.AANMELD_BIJZONDERHEDEN2;
            mobiel = Hike.Type.MOBIEL2;
          }

          hike.setParameter(aanmeld_bijzonderheden, tdList.get(1).text());
          hike.setParameter(mobiel, tdList.get(2).text());
        }

        if ("datum".equals(key))
        {
          String text;
          String datum;
          int index2;

          text = tdList.get(1).text();
          datum = text;
          index2 = text.indexOf('-');
          if (index2 != -1)
          {
            // 2 want er staat nog een spatie achter de '-'
            hike.setParameter(Hike.Type.TILL_DATE, text.substring(index2 + 2));
            datum = text.substring(0, index2);
          }

          hike.setParameter(Hike.Type.DATE, datum);
        }
        else
        {
          if ("traject".equals(key))
          {
            hike.setParameter(Hike.Type.TRAJECTORY, tdList.get(1).text());
            if (tdList.size() > 1)
            {
              hike.setParameter(Hike.Type.DISTANCE, tdList.get(2).text());
            }
          }
          else
          {
            if ("verzamelen".equals(key))
            {
              hike.setParameter(Hike.Type.ASSEMBLY_POINT, tdList.get(1).text());
              if (tdList.size() > 1)
              {
                hike.setParameter(Hike.Type.ASSEMBLY_TIME, tdList.get(2).text());
              }
            }
            else
            {
              if ("heenreis".equals(key))
              {
                hike.setParameter(Hike.Type.OUTWARD_TRIP, tdList.get(1).text());
              }
              else
              {
                if ("terugreis".equals(key))
                {
                  hike.setParameter(Hike.Type.RETURN_TRIP, tdList.get(1).text());
                }
                else
                {
                  if ("deelnemers".equals(key))
                  {
                    hike.setParameter(Hike.Type.MAXIMUM_NUMBER_OF_PARTICIPANTS, tdList.get(1).text());
                  }
                  else
                  {
                    if ("opgeven bij".equals(key))
                    {
                      Hike.Type email;
                      Hike.Type telefoon_nummer;
                      Hike.Type opgeven_bij;

                      if (hike.getParameter(Hike.Type.REPORT_TO_1) == null)
                      {
                        opgeven_bij = Hike.Type.REPORT_TO_1;
                        email = Hike.Type.EMAIL_1;
                        telefoon_nummer = Hike.Type.PHONE_NUMBER_1;
                      }
                      else
                      {
                        opgeven_bij = Hike.Type.REPORT_TO_2;
                        email = Hike.Type.EMAIL_2;
                        telefoon_nummer = Hike.Type.PHONE_NUMBER_2;
                      }
                      currentType = opgeven_bij;

                      hike.setParameter(opgeven_bij, tdList.get(1).text());
                      hike.setParameter(email, tdList.get(2).text());
                      hike.setParameter(telefoon_nummer, tdList.get(3).text());
                    }
                    else
                    {
                      if (tdList.size() == 1)
                      {
                        if (!StringUtil.isEmpty(trList.get(index).text()))
                        {
                          if (hike.getParameter(Hike.Type.INTRODUCTION) == null)
                          {
                            hike.setParameter(Hike.Type.INTRODUCTION, trList.get(index).text());
                          }
                          else
                          {
                            if (hike.getParameter(Hike.Type.DESCRIPTION) == null)
                            {
                              hike.setParameter(Hike.Type.DESCRIPTION, trList.get(index).text());
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    if (hike.getParameter(Hike.Type.DESCRIPTION) == null)
    {
      hike.setParameter(Hike.Type.DESCRIPTION, hike.getParameter(Hike.Type.INTRODUCTION));
      hike.setParameter(Hike.Type.INTRODUCTION, null);
    }

    HikeList.save(m_hikeList);

    return hike;
  }

  private Date parseDate(
      String dateString)
      throws ParseException
  {
    Calendar cal;
    int day;
    int month;
    int year;
    int index1, index2;

    index1 = dateString.indexOf(" ");
    index2 = dateString.indexOf("-");
    if (index1 == -1 || index2 == -1)
    {
      return null;
    }

    day = Integer.valueOf(dateString.substring(index1 + 1, index2));
    month = Integer.valueOf(dateString.substring(index2 + 1));

    cal = Calendar.getInstance();
    year = cal.get(Calendar.YEAR);
    if (month < cal.get(Calendar.MONTH))
    {
      year++;
    }

    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);

    return cal.getTime();
  }

  public List<BulletinBoardItem> getBulletinBoardItemList()
  {
    List<BulletinBoardItem> list;
    BulletinBoardItem bbi;

    list = new ArrayList<BulletinBoardItem>();

    bbi = new BulletinBoardItem();
    bbi.setAuthor("P. Kuip ");
    bbi.setDate("10-09-2001");
    bbi.setText("Hallo daar\nBen ik weer");
    list.add(bbi);

    bbi = new BulletinBoardItem();
    bbi.setAuthor("P. Kuip 2");
    bbi.setDate("10-09-2001");
    bbi.setText("Hallo daar\nBen ik weer\ne weer");
    list.add(bbi);

    bbi = new BulletinBoardItem();
    bbi.setAuthor("P. Kuip 3");
    bbi.setDate("10-09-2011");
    bbi.setText("Hallo daar\nBen ik weer\ne wee\n wee\n weerrr");
    list.add(bbi);

    return list;
  }

  class GetHikeList
      implements Callable<HikeList>
  {
    private final Date mi_endDate;
    private final Date mi_beginDate;

    public GetHikeList(Date beginDate, Date endDate)
    {
      mi_beginDate = beginDate;
      mi_endDate = endDate;
    }

    @Override
    public HikeList call() throws Exception
    {
      return getHikeList(mi_beginDate, mi_endDate);
    }
  }
}
