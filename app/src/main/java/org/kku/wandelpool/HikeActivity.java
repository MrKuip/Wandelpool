package org.kku.wandelpool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.cube.wandelpool.R;
import org.kku.util.StringUtil;
import org.kku.wandelpool.domain.Hike;
import org.kku.wandelpool.domain.Website;

public class HikeActivity
    extends Activity
{
  public static final String HIKE_ID = "HIKE_ID";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.hike);
  }

  private void showHike()
  {
    Hike hike;
    ImageView imageView;
    Button loginButton;
    Website site;

    site = Website.getInstance();

    if (site.isLoggedIn())
    {
      View view;

      view = findViewById(R.id.loginPanel);
      if (view != null)
      {
        view.setVisibility(View.GONE);
      }
    }

    try
    {
      String text, text2;

      hike = getHike();

      imageView = findViewById(R.id.categorie);
      if (hike.getCategory() != null)
      {
        imageView.setImageResource(hike.getCategory().getImage());
      }
      showTextView(hike, R.id.organisator, Hike.Type.ORGANISER);
      showTextView(hike, R.id.titel, Hike.Type.TITEL);
      showTextView(hike, R.id.subtitel, Hike.Type.SUB_TITEL);
      showTextView(hike, R.id.datum, Hike.Type.DATE);
      showTextView(hike, R.id.totdatum, Hike.Type.TILL_DATE);
      showTextView(hike, R.id.traject, Hike.Type.TRAJECT);
      showTextView(hike, R.id.afstand, Hike.Type.AFSTAND);

      text = showTextView(hike, R.id.introductie, Hike.Type.INLEIDING);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.introductieLabel).setVisibility(View.GONE);
      }

      text = showTextView(hike, R.id.omschrijving, Hike.Type.OMSCHRIJVING);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.omschrijvingLabel).setVisibility(View.GONE);
      }

      text = showTextView(hike, R.id.verzamelpunt, Hike.Type.VERZAMEL_PUNT);
      text2 = showTextView(hike, R.id.verzameltijd, Hike.Type.VERZAMEL_TIJD);
      if (StringUtil.isEmpty(text) || StringUtil.isEmpty(text2))
      {
        findViewById(R.id.verzamelpuntLabel).setVisibility(View.GONE);
      }

      text = showTextView(hike, R.id.heenreis, Hike.Type.HEENREIS);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.heenreisLabel).setVisibility(View.GONE);
      }

      text = showTextView(hike, R.id.terugreis, Hike.Type.TERUGREIS);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.terugreisLabel).setVisibility(View.GONE);
      }

      text = showTextView(hike, R.id.deelnemers, Hike.Type.DEELNEMERS);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.deelnemersLabel).setVisibility(View.GONE);
      }

      showTextView(hike, R.id.opgevenbij1, Hike.Type.OPGEVEN_BIJ_1);
      text = showTextView(hike, R.id.email1, Hike.Type.EMAIL_1);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.email1_action).setVisibility(View.GONE);
      }
      else
      {
        startEmailActivity(R.id.email1_action, text);
      }
      text = showTextView(hike, R.id.telefoonnummer1, Hike.Type.TELEFOONNUMMER_1);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.telefoonnummer1_action).setVisibility(View.GONE);
      }
      else
      {
        startDialActivity(R.id.telefoonnummer1_action, text);
      }
      showTextView(hike, R.id.aanmeldbijzonderheden1, Hike.Type.AANMELD_BIJZONDERHEDEN_1);
      text = showTextView(hike, R.id.mobiel1, Hike.Type.MOBIEL_1);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.mobiel1_action).setVisibility(View.GONE);
      }
      else
      {
        startDialActivity(R.id.mobiel1_action, text);
      }

      showTextView(hike, R.id.opgevenbij2, Hike.Type.OPGEVEN_BIJ_2);
      text = showTextView(hike, R.id.email2, Hike.Type.EMAIL_2);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.email2_action).setVisibility(View.GONE);
      }
      else
      {
        startEmailActivity(R.id.email2_action, text);
      }
      text = showTextView(hike, R.id.telefoonnummer2, Hike.Type.TELEFOONNUMMER_2);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.telefoonnummer2_action).setVisibility(View.GONE);
      }
      else
      {
        startDialActivity(R.id.telefoonnummer2_action, text);
      }
      showTextView(hike, R.id.aanmeldbijzonderheden2, Hike.Type.AANMELD_BIJZONDERHEDEN_2);
      text = showTextView(hike, R.id.mobiel2, Hike.Type.MOBIEL_2);
      if (StringUtil.isEmpty(text))
      {
        findViewById(R.id.mobiel2_action).setVisibility(View.GONE);
      }
      else
      {
        startDialActivity(R.id.mobiel2_action, text);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return;
    }

    loginButton = findViewById(R.id.loginButton);
    if (loginButton != null)
    {
      loginButton.setOnClickListener(getLoginListener());
    }
  }

  private void startEmailActivity(
      int id, String email)
  {
    View image;

    image = findViewById(id);
    if (image != null)
    {
      image.setOnClickListener(getEmailListener(email));
    }
  }

  private void startDialActivity(
      int id, String telefoonNummer)
  {
    View image;

    image = findViewById(id);
    if (image != null)
    {
      image.setOnClickListener(getDialListener(telefoonNummer));
    }
  }

  private Hike getHike()
      throws Exception
  {
    String hikeId;

    hikeId = (String) getIntent().getExtras().get(HIKE_ID);

    return Website.getInstance().getHikeDetails(hikeId);
  }

  private String showTextView(
      Hike hike, int id, Hike.Type type)
  {
    return showTextView(hike, id, type.get(hike));
  }

  private String showTextView(
      Hike hike, int id, String parameter)
  {
    TextView textView;

    textView = findViewById(id);
    if (textView == null)
    {
      return "";
    }

    if (!StringUtil.isEmpty(parameter))
    {
      textView.setText(parameter);
    }
    else
    {
      textView.setVisibility(View.GONE);
    }

    return parameter;
  }

  private OnClickListener getLoginListener()
  {
    return new View.OnClickListener()
    {
      public void onClick(
          View v)
      {
        startActivity(new Intent(HikeActivity.this, LoginActivity.class));
      }
    };
  }

  private OnClickListener getEmailListener(
      final String email)
  {
    return new View.OnClickListener()
    {
      public void onClick(
          View v)
      {
        Hike hike;
        Intent intent;

        try
        {
          hike = getHike();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          return;
        }

        intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, Hike.Type.TITEL.get(hike));

        startActivity(intent);
      }
    };
  }

  private OnClickListener getDialListener(
      final String telefoonNummer)
  {
    return new View.OnClickListener()
    {
      public void onClick(
          View v)
      {
        Intent intent;

        intent = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + telefoonNummer));
        startActivity(intent);
      }
    };
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    showHike();
  }

}