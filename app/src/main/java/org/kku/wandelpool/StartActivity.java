package org.kku.wandelpool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import org.cube.wandelpool.R;
import org.kku.wandelpool.domain.Settings;
import org.kku.wandelpool.domain.WandelpoolFilter;

import java.util.ArrayList;
import java.util.List;

public class StartActivity
    extends Activity
{
  private List<StartForm> m_startFormList;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.start);

    init();
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    init();
  }

  private void init()
  {
    GridView gridView;

    initStartForm();

    gridView = (GridView) findViewById(R.id.startform_grid);
    gridView.setAdapter(new ButtonAdapter(this));
  }

  private void initStartForm()
  {
    m_startFormList = new ArrayList<StartForm>();
    for (WandelpoolFilter wandelpoolFilter : Settings.getInstance().getFilterList())
    {
      m_startFormList.add(new StartForm(wandelpoolFilter.getName(), WandelpoolActivity.class,
          wandelpoolFilter));
    }
    //m_startFormList.add(new StartForm("Prikbord", WandelpoolActivity.class, null));
    m_startFormList.add(new StartForm("Filters", FilterListActivity.class, null));
    //m_startFormList.add(new StartForm("Login", WandelpoolActivity.class, null));
  }

  public class ButtonAdapter
      extends BaseAdapter
  {
    private Context mContext;

    public ButtonAdapter(Context c)
    {
      mContext = c;
    }

    public int getCount()
    {
      return m_startFormList.size();
    }

    public Object getItem(
        int position)
    {
      return null;
    }

    public long getItemId(
        int position)
    {
      return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(
        int position, View convertView, ViewGroup parent)
    {
      Button button;

      if (convertView == null)
      {
        StartForm startForm;

        startForm = m_startFormList.get(position);

        button = new Button(mContext);
        button.setLayoutParams(new GridView.LayoutParams(140, 140));
        button.setPadding(8, 8, 8, 8);
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rowheader));
        button.setText(startForm.getText());
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        button.setTextColor(getResources().getColor(R.color.textColor));
        if (!startForm.isEnabled())
        {
          button.setVisibility(View.GONE);
        }
        button.setOnClickListener(getStartFormListener(startForm));
      }
      else
      {
        button = (Button) convertView;
      }

      return button;
    }

    private OnClickListener getStartFormListener(
        final StartForm startForm)
    {
      return new View.OnClickListener()
      {
        public void onClick(
            View v)
        {
          startActivity(new Intent(StartActivity.this, startForm.getActivityClass()));
        }
      };
    }

  }

  class StartForm
  {
    private String mi_text;
    private Class<? extends Activity> mi_activityClass;
    private WandelpoolFilter mi_wandelpoolFilter;

    StartForm(
        String text,
        Class<? extends Activity> activityClass,
        WandelpoolFilter wandelpoolFilter)
    {
      mi_text = text;
      mi_activityClass = activityClass;
      mi_wandelpoolFilter = wandelpoolFilter;
    }

    public String getText()
    {
      return mi_text;
    }

    public Class<? extends Activity> getActivityClass()
    {
      return mi_activityClass;
    }

    public WandelpoolFilter getWandelpoolFilter()
    {
      return mi_wandelpoolFilter;
    }

    public boolean isEnabled()
    {
      return mi_activityClass != null;
    }
  }

}
