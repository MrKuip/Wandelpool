package org.kku.wandelpool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.cube.wandelpool.R;
import org.kku.wandelpool.domain.Hike;
import org.kku.wandelpool.domain.Settings;
import org.kku.wandelpool.domain.WandelpoolFilter;

public class FilterDetailActivity
    extends Activity
{
  public static final String FILTER_NAME = "FILTER_NAME";
  private FilterDetailAdapter m_filterAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {
    ListView listView;
    TextView textView;

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.filterdetail);

    try
    {
      textView = (TextView) findViewById(R.id.headerText2);
      textView.setText(getWandelpoolFilter().getName());

      listView = (ListView) findViewById(R.id.filterdetail_items);
      listView.setOnItemClickListener(getOnItemClickListener());
      listView.setOnItemLongClickListener(getOnItemLongClickListener());

      m_filterAdapter = new FilterDetailAdapter(this, R.layout.filteritem);
      listView.setAdapter(m_filterAdapter);
    } catch (Exception ex)
    {
    }
  }

  private OnItemClickListener getOnItemClickListener()
  {
    return new OnItemClickListener()
    {
      public void onItemClick(
          AdapterView<?> parent, View view, int position, long id)
      {
        ListView lv;
        Hike wandeling;
        Intent intent;
        Bundle bundle;

        lv = (ListView) findViewById(R.id.filterdetail_items);
        wandeling = (Hike) lv.getItemAtPosition(position);
        bundle = new Bundle();
        bundle.putString(HikeActivity.HIKE_ID, wandeling.getId());

        intent = new Intent(FilterDetailActivity.this, HikeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
      }
    };
  }

  private WandelpoolFilter getWandelpoolFilter()
  {
    String filterName;

    filterName = (String) getIntent().getExtras().get(FILTER_NAME);

    return Settings.getInstance().getFilterByName(filterName);
  }

  private OnItemLongClickListener getOnItemLongClickListener()
  {
    return new OnItemLongClickListener()
    {
      public boolean onItemLongClick(
          AdapterView<?> parent, View view, int position, long id)
      {
        AlertDialog.Builder alert;
        final WandelpoolFilter wandelpoolFilter;

        wandelpoolFilter = Settings.getInstance().getFilterList().get(position);
        if (wandelpoolFilter.isDefaultFilter())
        {
          return true;
        }

        alert = new AlertDialog.Builder(FilterDetailActivity.this);

        alert.setTitle("Filter verwijderen");
        alert.setMessage("Weet u zeker dat u filter '" + wandelpoolFilter.getName()
            + "' wilt verwijderen?");

        alert.setPositiveButton("Ja", new DialogInterface.OnClickListener()
        {
          public void onClick(
              DialogInterface dialog, int whichButton)
          {
            Settings.getInstance().getFilterList().remove(wandelpoolFilter);
            Settings.save();

            m_filterAdapter.notifyDataSetChanged();
          }
        });

        alert.setNegativeButton("Nee", new DialogInterface.OnClickListener()
        {
          public void onClick(
              DialogInterface dialog, int whichButton)
          {
          }
        });

        alert.show();

        return true;
      }
    };
  }

  @Override
  public boolean onCreateOptionsMenu(
      Menu menu)
  {
    MenuInflater inflater;

    super.onCreateOptionsMenu(menu);

    inflater = getMenuInflater();
    inflater.inflate(R.menu.filterlist_menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(
      MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.filterlist_additem:
        AlertDialog.Builder alert;
        ListView listView;

        alert = new AlertDialog.Builder(this);

        alert.setTitle("Kies een filterregel");

        listView = new ListView(this);
        listView.setAdapter(new FilterTypeAdapter(this, android.R.layout.simple_list_item_1));

        alert.setView(listView);
        alert.show();
        return true;
    }

    return false;
  }

  private class FilterDetailAdapter
      extends ArrayAdapter<WandelpoolFilter.Filter>
  {
    public FilterDetailAdapter(Context context, int textViewResourceId)
    {
      super(context, textViewResourceId, FilterDetailActivity.this.getWandelpoolFilter()
                                                                  .getFilterList());
    }

    @Override
    public View getView(
        int position, View convertView, ViewGroup parent)
    {
      View v;
      WandelpoolFilter.Filter filter;

      v = convertView;
      if (v == null)
      {
        LayoutInflater vi;

        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.filterdetail_item, null);
      }

      filter = getItem(position);
      if (filter != null)
      {
        TextView textView;

        textView = (TextView) v.findViewById(R.id.filterdetail_itemname);
        textView.setText(filter.getFilterType().getName());

        textView = (TextView) v.findViewById(R.id.filterdetail_itemvalue);
        textView.setText(filter.getFilterValue().toString());
      }

      return v;
    }
  }

  private class FilterTypeAdapter
      extends ArrayAdapter<WandelpoolFilter.FilterType>
  {
    public FilterTypeAdapter(Context context, int textViewResourceId)
    {
      super(context, textViewResourceId, WandelpoolFilter.FilterType.values());
    }
  }
}
