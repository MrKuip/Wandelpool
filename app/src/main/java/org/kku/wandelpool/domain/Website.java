package org.kku.wandelpool.domain;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kku.util.DateUtil;
import org.kku.util.StringUtil;
import org.kku.wandelpool.Preferences;
import org.kku.wandelpool.domain.Hike.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Website
{
  public static final String LOGIN_PREFERENCES = "LOGIN_PREFERENCES";
  public static final String USERNAME_PREFERENCE = "userName";
  public static final String PASSWORD_PREFERENCE = "password";
  private static final String BASE_URL = "https://www.wandelpool.nl/";
  private static final String HIKES_URL = BASE_URL + "tochten-overzicht";
  private static final String HIKE_DETAIL_URL = BASE_URL + "tocht/toon";
  private static final String LOGIN_SESSION_COOKIE = "PHPSESSID";
  private static final String LOGIN_USER_COOKIE = "wordpress_logged_in";
  private static final String HIKE_LIST_SUBMIT_REQUEST_DATA = "Zoek op!";
  private static final String HIKE_DETAIL_WANDELINGID_REQUEST = "tocht_id";
  // DO NOT COMMIT
  //private static final int TIMEOUT = 30000;
  private static final int TIMEOUT = 3000000;
  private static Website m_instance = new Website();
  private Map<String, String> m_cookies;
  private HikeList m_hikeList;
  private ExecutorService m_threadPool;

  private Website()
  {
  }

  static public Website getInstance()
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
      Website website;

      website = new Website();

      for (Hike hike : website.getHikeList().getList())
      {
        count++;

        System.out.printf("%03d %s %10.10s:  %-10.10s %-15.15s %s%n", count, hike.getId(),
            hike.getCategory(), Hike.Type.DATE_STRING.get(hike), Hike.Type.ORGANISER.get(hike),
            Hike.Type.TITEL.get(hike));

        if (count >= maxCount)
        {
          continue;
        }

        System.out.println("  id        =" + hike.getId());
        System.out.println("  category  =" + hike.getCategory());
        System.out.println("  date      =" + hike.getDate());
        System.out.println("  dateString=" + Hike.Type.DATE_STRING.get(hike));
        System.out.println("  distance  =" + Hike.Type.AFSTAND.get(hike));
        System.out.println("  location  =" + Hike.Type.LOCATION.get(hike));
        System.out.println("  organiser =" + Hike.Type.ORGANISER.get(hike));
        System.out.println("  title     =" + Hike.Type.TITEL.get(hike));
        System.out.println("  trajectory=" + Hike.Type.TRAJECT.get(hike));

        website.getHikeDetails(hike.getId());

        for (Hike.Type type : Hike.Type.values())
        {
          System.out.printf("  %-20.20s = %s%n", type, hike.getParameter(type));
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Login the website of the wandelpool.
   */

  public boolean login(String userName, String password)
      throws Exception
  {
    Future<Boolean> loginFuture;

    loginFuture = getThreadPool().submit(new Login(userName, password));

    try
    {
      return loginFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (Exception e)
    {
      throw new WandelpoolException(e);
    }
  }

  public void login()
  {
    SharedPreferences pref;
    String userName;
    String password;

    pref = Preferences.getSharedPreferences(LOGIN_PREFERENCES);
    userName = pref.getString(USERNAME_PREFERENCE, "");
    password = pref.getString(PASSWORD_PREFERENCE, "");

    if (StringUtil.isEmpty(userName))
    {
      return;
    }

    try
    {
      login(userName, password);
    }
    catch (Exception e)
    {
      // Do Nothing
    }
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
    SharedPreferences preferences;

    preferences = Preferences.getSharedPreferences(LOGIN_PREFERENCES);
    if (preferences == null)
    {
      return;
    }

    edit = preferences.edit();
    edit.putString(USERNAME_PREFERENCE, userName);
    edit.putString(PASSWORD_PREFERENCE, password);
    edit.commit();
  }

  public boolean isLoggedIn()
  {
    if (m_cookies != null)
    {
      for (String cookie : m_cookies.keySet())
      {
        if (cookie.startsWith(LOGIN_USER_COOKIE))
        {
          return true;
        }
      }
    }

    return false;
  }

  public HikeList getHikeList()
      throws WandelpoolException
  {
    Future<HikeList> hikeListFuture;

    if (m_hikeList != null)
    {
      return m_hikeList;
    }

    m_hikeList = CacheManager.load();
    if (m_hikeList != null)
    {
      return m_hikeList;
    }

    hikeListFuture = getThreadPool().submit(
        new GetHikeList(DateUtil.getDefaultBeginDate(), DateUtil.getDefaultEndDate()));

    try
    {
      m_hikeList = hikeListFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (Exception e)
    {
      throw new WandelpoolException(e);
    }

    CacheManager.save(m_hikeList);

    return m_hikeList;
  }

  private ExecutorService getThreadPool()
  {
    if (m_threadPool == null)
    {
      m_threadPool = Executors.newFixedThreadPool(1);
    }

    return m_threadPool;
  }


  private Category getCategory(
      String category)
  {
    if (!StringUtil.isEmpty(category))
    {
      if (category.contains("schoen5."))
      {
        return Category.SCHOEN_05;
      }
      if (category.contains("schoen1."))
      {
        return Category.SCHOEN_10;
      }
      if (category.contains("schoen15."))
      {
        return Category.SCHOEN_15;
      }
      if (category.contains("schoen2."))
      {
        return Category.SCHOEN_20;
      }
      if (category.contains("schoen25."))
      {
        return Category.SCHOEN_25;
      }
      if (category.contains("schoen3."))
      {
        return Category.SCHOEN_30;
      }
    }

    return Category.SCHOEN_00;
  }

  public Hike getHikeDetails(
      String hikeId)
      throws Exception
  {
    Future<Hike> loginFuture;

    loginFuture = getThreadPool().submit(new GetHikeDetails(getHikeList().getHikeById(hikeId)));

    try
    {
      return loginFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (Exception e)
    {
      throw new WandelpoolException(e);
    }
  }

  private Date parseDate(
      String dateString)
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

    day = Integer.parseInt(dateString.substring(index1 + 1, index2));
    month = Integer.parseInt(dateString.substring(index2 + 1));

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

    list = new ArrayList<>();

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

  public class Credentials
  {

    public void persist()
    {
    }
  }

  private class Login
      implements Callable<Boolean>
  {
    private String mi_userName;
    private String mi_password;

    public Login(String userName, String password)
    {
      mi_userName = userName;
      mi_password = password;
    }

    @Override
    public Boolean call() throws Exception
    {
      Connection connection;

      connection = Jsoup.connect(BASE_URL + "wordpress/wp-content/plugins/wandelpool/login.php");
      connection.data("gebruikersnaam", mi_userName);
      connection.data("wachtwoord", mi_password);
      connection.data("submitted_login", "1");
      connection.data("submit", "Inloggen");
      connection.timeout(TIMEOUT);
      connection.post();

      m_cookies = connection.response().cookies();

      if (m_hikeList != null)
      {
        m_hikeList.reset();
      }

      if (isLoggedIn())
      {
        persistCredentials(mi_userName, mi_password);
      }

      return isLoggedIn();
    }
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
    public HikeList call() throws WandelpoolException
    {
      Document doc;
      int trCount;
      int tdCount;
      Connection connection;
      HikeList hikeList;

      hikeList = new HikeList();

      try
      {
        Element table;

        for (Hike.HikeType hikeType : Hike.HikeType.values())
        {
          connection = Jsoup.connect(HIKES_URL);
          connection.data("meerdaags", hikeType.getId());
          connection.data("datum", DateUtil.SQL_FORMATTER.format(mi_beginDate));
          connection.data("einddatum", DateUtil.SQL_FORMATTER.format(mi_endDate));
          connection.data("submit", HIKE_LIST_SUBMIT_REQUEST_DATA);
          doc = connection.post();

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
              Elements selectList;

              tdCount++;
              switch (tdCount)
              {
                case 1:
                  String datumString;

                  Hike.Type.ID.set(hike, td.select("form").select("input[name$=item]").attr("value"));

                  selectList = td.select("p");
                  if (selectList.size() > 0)
                  {
                    datumString = selectList.get(0).text();

                    Hike.Type.DATE.set(hike, parseDate(datumString));
                    Hike.Type.DATE_STRING.set(hike, datumString);
                  }
                  if (selectList.size() > 1)
                  {
                    Hike.Type.STATE.set(hike, selectList.get(1).text());
                  }
                  break;
                case 2:
                  selectList = td.select("p");

                  if (selectList.size() > 0)
                  {
                    Hike.Type.TITEL.set(hike, selectList.get(0).text());
                  }

                  if (selectList.size() > 1)
                  {
                    Hike.Type.TRAJECT.set(hike, selectList.get(1).text());
                  }
                  break;
                case 3:
                  selectList = td.select("p");
                  if (selectList.size() > 0)
                  {
                    Hike.Type.AFSTAND.set(hike, selectList.get(0).text());
                  }
                  if (selectList.size() > 1)
                  {
                    Hike.Type.LOCATION.set(hike, selectList.get(1).text());
                  }
                  break;
                case 4:
                  selectList = td.getElementsByTag("img");
                  if (selectList.size() > 0)
                  {
                    Hike.Type.CATEGORIE.set(hike, getCategory(selectList.get(0).attr("src")));
                  }

                  selectList = td.getElementsByTag("p");
                  if (selectList.size() > 1)
                  {
                    Hike.Type.ORGANISER.set(hike, selectList.get(1).text());
                  }
                  break;
              }
            }
          }
        }
        hikeList.sort();
      }
      catch (Exception e)
      {
        throw new WandelpoolException(e);
      }

      return hikeList;
    }
  }

  class GetHikeDetails
      implements Callable<Hike>
  {
    private Hike m_hike;

    GetHikeDetails(Hike hike)
    {
      m_hike = hike;
    }

    @Override
    public Hike call() throws Exception
    {
      Document doc;
      Connection connection;
      Elements tables;

      if (m_hike == null)
      {
        return null;
      }

      if (m_cookies == null)
      {
        return null;
      }

      connection = Jsoup.connect(HIKE_DETAIL_URL);
      if (isLoggedIn())
      {
        connection.cookie(LOGIN_SESSION_COOKIE, m_cookies.get(LOGIN_SESSION_COOKIE));
        for (String cookie : m_cookies.keySet())
        {
          if (cookie.startsWith(LOGIN_USER_COOKIE))
          {
            connection.cookie(cookie, m_cookies.get(cookie));
          }
        }
      }
      connection.data(HIKE_DETAIL_WANDELINGID_REQUEST, m_hike.getId());
      connection.data("item", m_hike.getId());
      connection.timeout(TIMEOUT);
      doc = connection.post();

      tables = doc.select("table");
      if (tables != null)
      {
        Element table;
        Elements trList;
        Elements tdList;
        Elements selectList;

        table = tables.get(0);
        trList = table.select("tr");

        if (trList.size() > 0)
        {
          tdList = trList.get(0).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            String title;

            title = tdList.get(0).text();
            selectList = tdList.get(0).getElementsByTag("i");
            if (selectList.size() > 0)
            {
              String stage;

              stage = selectList.get(0).text();
              Hike.Type.ETAPPE.set(m_hike, stage);

              title = title.substring(0, title.length() - stage.length() - 2); // 2 = ", "
            }
            Hike.Type.TITEL.set(m_hike, title);
          }

          if (tdList.size() > 1)
          {
            selectList = tdList.get(1).getElementsByTag("img");
            if (selectList.size() > 0)
            {
              Hike.Type.CATEGORIE.set(m_hike, getCategory(selectList.get(0).attr("src")));
            }
          }
        }

        if (trList.size() > 1)
        {
          tdList = trList.get(1).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.SUB_TITEL.set(m_hike, tdList.get(0).text());
          }
        }

        if (trList.size() > 2)
        {
          tdList = trList.get(2).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.INLEIDING.set(m_hike, tdList.get(0).text());
          }
        }

        if (trList.size() > 3)
        {
          tdList = trList.get(3).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.OMSCHRIJVING.set(m_hike, tdList.get(0).text());
          }
        }

        // TODO: DATUM
        if (trList.size() > 4)
        {
        }

        // TODO: ALTERNATIEVE DATUM
        if (trList.size() > 5)
        {
        }

        if (trList.size() > 6)
        {
          tdList = trList.get(6).getElementsByTag("td");
          if (tdList.size() > 1)
          {
            Hike.Type.TRAJECT.set(m_hike, tdList.get(1).text());
          }

          if (tdList.size() > 2)
          {
            Hike.Type.AFSTAND.set(m_hike, tdList.get(2).text());
          }
        }

        if (trList.size() > 7)
        {
          tdList = trList.get(7).getElementsByTag("td");
          if (tdList.size() > 1)
          {
            Hike.Type.VERZAMEL_PUNT.set(m_hike, tdList.get(1).text());
          }

          if (tdList.size() > 2)
          {
            Hike.Type.VERZAMEL_TIJD.set(m_hike, tdList.get(2).text());
          }
        }

        if (trList.size() > 8)
        {
          tdList = trList.get(8).getElementsByTag("td");
          if (tdList.size() > 1)
          {
            Hike.Type.HEENREIS.set(m_hike, tdList.get(1).text());
          }
        }

        if (trList.size() > 9)
        {
          tdList = trList.get(9).getElementsByTag("td");
          if (tdList.size() > 1)
          {
            Hike.Type.TERUGREIS.set(m_hike, tdList.get(1).text());
          }
        }

        if (trList.size() > 10)
        {
          tdList = trList.get(10).getElementsByTag("td");
          if (tdList.size() > 1)
          {
            Hike.Type.DEELNEMERS.set(m_hike, tdList.get(1).text());
          }
        }

        if (trList.size() > 11)
        {
          tdList = trList.get(11).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.OPGEVEN_BIJ_1.set(m_hike, tdList.get(0).text());
          }

          if (tdList.size() > 1)
          {
            Hike.Type.EMAIL_1.set(m_hike, tdList.get(1).text());
          }

          if (tdList.size() > 2)
          {
            Hike.Type.TELEFOONNUMMER_1.set(m_hike, tdList.get(2).text());
          }
        }

        if (trList.size() > 12)
        {
          tdList = trList.get(12).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.AANMELD_BIJZONDERHEDEN_1.set(m_hike, tdList.get(0).text());
          }

          if (tdList.size() > 1)
          {
            Hike.Type.MOBIEL_1.set(m_hike, tdList.get(1).text());
          }
        }

        if (trList.size() > 13)
        {
          tdList = trList.get(13).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.OPGEVEN_BIJ_2.set(m_hike, tdList.get(0).text());
          }

          if (tdList.size() > 1)
          {
            Hike.Type.EMAIL_2.set(m_hike, tdList.get(1).text());
          }

          if (tdList.size() > 2)
          {
            Hike.Type.TELEFOONNUMMER_2.set(m_hike, tdList.get(2).text());
          }
        }

        if (trList.size() > 14)
        {
          tdList = trList.get(14).getElementsByTag("td");
          if (tdList.size() > 0)
          {
            Hike.Type.AANMELD_BIJZONDERHEDEN_2.set(m_hike, tdList.get(0).text());
          }

          if (tdList.size() > 1)
          {
            Hike.Type.MOBIEL_2.set(m_hike, tdList.get(1).text());
          }
        }

        for (Map.Entry<Hike.Type, Object> entry : m_hike.getParameterMap().entrySet())
        {
          System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("lala");

        /*
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

            m_hike.setParameter(aanmeld_bijzonderheden, tdList.get(1).text());
            m_hike.setParameter(mobiel, tdList.get(2).text());
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
              m_hike.setParameter(Hike.Type.TILL_DATE, text.substring(index2 + 2));
              datum = text.substring(0, index2);
            }

            m_hike.setParameter(Hike.Type.DATE, datum);
          }
          else
          {
            if ("traject".equals(key))
            {
              m_hike.setParameter(Hike.Type.TRAJECTORY, tdList.get(1).text());
              if (tdList.size() > 1)
              {
                m_hike.setParameter(Hike.Type.DISTANCE, tdList.get(2).text());
              }
            }
            else
            {
              if ("verzamelen".equals(key))
              {
                m_hike.setParameter(Hike.Type.ASSEMBLY_POINT, tdList.get(1).text());
                if (tdList.size() > 1)
                {
                  m_hike.setParameter(Hike.Type.ASSEMBLY_TIME, tdList.get(2).text());
                }
              }
              else
              {
                if ("heenreis".equals(key))
                {
                  m_hike.setParameter(Hike.Type.OUTWARD_TRIP, tdList.get(1).text());
                }
                else
                {
                  if ("terugreis".equals(key))
                  {
                    m_hike.setParameter(Hike.Type.RETURN_TRIP, tdList.get(1).text());
                  }
                  else
                  {
                    if ("deelnemers".equals(key))
                    {
                      m_hike.setParameter(Hike.Type.MAXIMUM_NUMBER_OF_PARTICIPANTS, tdList.get(1).text());
                    }
                    else
                    {
                      if ("opgeven bij".equals(key))
                      {
                        Hike.Type email;
                        Hike.Type telefoon_nummer;
                        Hike.Type opgeven_bij;

                        if (m_hike.getParameter(Hike.Type.REPORT_TO_1) == null)
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

                        m_hike.setParameter(opgeven_bij, tdList.get(1).text());
                        m_hike.setParameter(email, tdList.get(2).text());
                        m_hike.setParameter(telefoon_nummer, tdList.get(3).text());
                      }
                      else
                      {
                        if (tdList.size() == 1)
                        {
                          if (!StringUtil.isEmpty(trList.get(index).text()))
                          {
                            if (m_hike.getParameter(Hike.Type.INTRODUCTION) == null)
                            {
                              m_hike.setParameter(Hike.Type.INTRODUCTION, trList.get(index).text());
                            }
                            else
                            {
                              if (m_hike.getParameter(Hike.Type.DESCRIPTION) == null)
                              {
                                m_hike.setParameter(Hike.Type.DESCRIPTION, trList.get(index).text());
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
         */
      }

      if (m_hike.getParameter(Hike.Type.OMSCHRIJVING) == null)
      {
        m_hike.setParameter(Hike.Type.OMSCHRIJVING, m_hike.getParameter(Hike.Type.INLEIDING));
        m_hike.setParameter(Hike.Type.INLEIDING, null);
      }

      CacheManager.save(m_hikeList);

      return m_hike;
    }
  }
}
