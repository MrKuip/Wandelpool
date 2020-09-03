package org.kku.wandelpool;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.cube.wandelpool.R;
import org.kku.wandelpool.domain.Hike;
import org.kku.wandelpool.domain.WandelpoolWebSite;

import java.util.List;

public class WandelpoolActivity
    extends AppCompatActivity
{
  private HikeAdapter m_hikeAdapter;

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
    setContentView(R.layout.main);

    try
    {
      listView = (ListView) findViewById(R.id.wandelingen);
      listView.setOnItemClickListener(getOnItemClickListener());

      m_hikeAdapter = new HikeAdapter(this, R.layout.row,
          WandelpoolWebSite.getInstance()
                           .getHikeList().getList());
      listView.setAdapter(m_hikeAdapter);
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private AdapterView.OnItemClickListener getOnItemClickListener()
  {
    return new AdapterView.OnItemClickListener()
    {
      public void onItemClick(
          AdapterView<?> parent, View view, int position, long id)
      {
        ListView lv;
        Hike wandeling;
        Intent intent;
        Bundle bundle;

        lv = (ListView) findViewById(R.id.wandelingen);
        wandeling = (Hike) lv.getItemAtPosition(position);
        bundle = new Bundle();
        bundle.putString(HikeActivity.WANDELING_ID, wandeling.getId());

        intent = new Intent(WandelpoolActivity.this, HikeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
    inflater.inflate(R.menu.menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(
      MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.logout:
        WandelpoolWebSite.getInstance().logout();
        return true;
    }

    return false;
  }

  private class HikeAdapter
      extends ArrayAdapter<Hike>
  {

    public HikeAdapter(Context context, int textViewResourceId, List<Hike> items)
    {
      super(context, textViewResourceId, items);
    }

    @Override
    public View getView(
        int position, View convertView, ViewGroup parent)
    {
      View v;
      Hike wandeling;

      v = convertView;
      if (v == null)
      {
        LayoutInflater vi;

        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.row, null);
      }

      wandeling = getItem(position);
      if (wandeling != null)
      {
        TextView textView;
        ImageView categorieView;

        textView = (TextView) v.findViewById(R.id.titel);
        textView.setText(wandeling.getTitle());

        textView = (TextView) v.findViewById(R.id.datum);
        textView.setText(wandeling.getDateString());

        textView = (TextView) v.findViewById(R.id.traject);
        textView.setText(wandeling.getTrajectory());

        textView = (TextView) v.findViewById(R.id.afstand);
        textView.setText(wandeling.getDistance());

        categorieView = (ImageView) v.findViewById(R.id.categorie);
        categorieView.setImageResource(wandeling.getCategory().getImage());

        textView = (TextView) v.findViewById(R.id.locatie);
        textView.setText(wandeling.getLocation());

        textView = (TextView) v.findViewById(R.id.organisator);
        textView.setText(wandeling.getOrganisor());
      }
      return v;
    }
  }


}

