package org.kku.wandelpool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.cube.wandelpool.R;
import org.kku.wandelpool.domain.Settings;
import org.kku.wandelpool.domain.WandelpoolFilter;

public class FilterListActivity
    extends Activity
{
  private FilterAdapter m_filterAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(
      Bundle savedInstanceState)
  {
    ListView listView;

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.filterlist);

    try
    {
      listView = findViewById(R.id.filterlist_items);
      listView.setOnItemClickListener(getOnItemClickListener());
      listView.setOnItemLongClickListener(getOnItemLongClickListener());

      m_filterAdapter = new FilterAdapter(this, R.layout.filteritem);
      listView.setAdapter(m_filterAdapter);
    }
    catch (Exception ex)
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
        WandelpoolFilter wandelpoolFilter;
        Intent intent;
        Bundle bundle;

        lv = findViewById(R.id.filterlist_items);
        wandelpoolFilter = (WandelpoolFilter) lv.getItemAtPosition(position);
        bundle = new Bundle();
        bundle.putString(FilterDetailActivity.FILTER_NAME, wandelpoolFilter.getName());

        intent = new Intent(FilterListActivity.this, FilterDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
      }
    };
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

        alert = new AlertDialog.Builder(FilterListActivity.this);

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
    if (item.getItemId() == R.id.filterlist_additem)
    {
      AlertDialog.Builder alert;
      final EditText input;

      alert = new AlertDialog.Builder(this);

      alert.setTitle("Nieuw filter aanmaken");
      alert.setMessage("Vul de naam van de nieuwe filter in");

      // Set an EditText view to get user input
      input = new EditText(this);
      alert.setView(input);

      alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
      {
        public void onClick(
            DialogInterface dialog, int whichButton)
        {
          Editable value;
          WandelpoolFilter wandelpoolFilter;

          value = input.getText();

          wandelpoolFilter = new WandelpoolFilter();
          wandelpoolFilter.setName(value.toString());

          Settings.getInstance().getFilterList().add(wandelpoolFilter);
          Settings.save();

          m_filterAdapter.notifyDataSetChanged();
        }
      });

      alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
      {
        public void onClick(
            DialogInterface dialog, int whichButton)
        {
          // Canceled.
        }
      });

      alert.show();
      return true;
    }

    return false;
  }

  private class FilterAdapter
      extends ArrayAdapter<WandelpoolFilter>
  {
    public FilterAdapter(Context context, int textViewResourceId)
    {
      super(context, textViewResourceId, Settings.getInstance().getFilterList());
    }

    @Override
    public View getView(
        int position, View convertView, ViewGroup parent)
    {
      View v;
      WandelpoolFilter filter;

      v = convertView;
      if (v == null)
      {
        LayoutInflater vi;

        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.filteritem, null);
      }

      filter = getItem(position);
      if (filter != null)
      {
        TextView textView;

        textView = v.findViewById(R.id.filter_item_name);
        textView.setText(filter.getName());
      }

      return v;
    }
  }


}
