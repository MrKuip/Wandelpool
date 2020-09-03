package org.kku.wandelpool;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.cube.wandelpool.R;
import org.kku.wandelpool.domain.BulletinBoardItem;
import org.kku.wandelpool.domain.WandelpoolWebSite;

public class PrikbordActivity
    extends Activity
{
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {
    ScrollView scrollView;
    LinearLayout layout;

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);

    scrollView = new ScrollView(this);
    layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    scrollView.addView(layout);

    for (BulletinBoardItem pi : WandelpoolWebSite.getInstance().getBulletinBoardItemList())
    {
      TextView textView;
      View divider;
      LinearLayout.LayoutParams params;

      textView = new TextView(this);
      textView.setText(pi.getAuteur());
      layout.addView(textView);

      textView = new TextView(this);
      textView.setText(pi.getDatum());
      layout.addView(textView);

      textView = new TextView(this);
      textView.setText(pi.getText());
      layout.addView(textView);

      divider = new View(this);
      divider.setBackgroundResource(R.drawable.divider);
      params = new LinearLayout.LayoutParams(0, 5);
      divider.setLayoutParams(params);
    }

    setContentView(scrollView);
  }
}
