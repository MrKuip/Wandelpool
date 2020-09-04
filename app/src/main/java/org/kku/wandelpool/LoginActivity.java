package org.kku.wandelpool;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.cube.wandelpool.R;
import org.kku.util.StringUtil;
import org.kku.wandelpool.domain.Website;

public class LoginActivity
    extends Activity
{
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {
    Button button;

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.login);

    button = (Button) findViewById(R.id.loginButton);
    button.setOnClickListener(getLoginListener());
  }

  private OnClickListener getLoginListener()
  {
    return new View.OnClickListener()
    {
      public void onClick(
          View v)
      {
        TextView userNameView;
        TextView passwordView;
        String password;
        String userName;
        Website site;

        userNameView = (TextView) findViewById(R.id.userName);
        passwordView = (TextView) findViewById(R.id.password);

        userName = userNameView.getText().toString();
        password = passwordView.getText().toString();

        userName = "keeskuip@gmail.com";
        password = "lalakz01wandelpool";

        if (StringUtil.isEmpty(userName))
        {
          alert("Vul " + getResources().getString(R.string.userName) + " in");
          return;
        }

        if (StringUtil.isEmpty(password))
        {
          alert("Vul " + getResources().getString(R.string.password) + " in");
          return;
        }

        site = Website.getInstance();
        try
        {
          if (site.login(userName, password))
          {
            finish();
            return;
          }
          alert(getResources().getString(R.string.userName) + " of "
              + getResources().getString(R.string.password) + " fout");
        } catch (Exception ex)
        {
          alert(ex.getMessage());
        }
      }
    };
  }

  private void alert(
      String text)
  {
    AlertDialog.Builder builder;
    AlertDialog alert;

    builder = new AlertDialog.Builder(this);
    builder.setMessage(text);

    alert = builder.create();
    alert.show();
  }
}
