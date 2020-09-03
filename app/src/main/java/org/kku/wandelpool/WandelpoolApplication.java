package org.kku.wandelpool;

import android.app.Application;

import org.kku.wandelpool.domain.WandelpoolWebSite;

public class WandelpoolApplication
    extends Application
{

  private static Application m_instance;

  static public Application getInstance()
  {
    return m_instance;
  }

  public void onCreate()
  {
    super.onCreate();
    m_instance = this;
    loginWithLastCredentials();
  }

  private void loginWithLastCredentials()
  {
    try
    {
      WandelpoolWebSite.getInstance().login();
    } catch (Exception e)
    {
      // Do nothing. User has to login again at wandeling-details
    }
  }

}
