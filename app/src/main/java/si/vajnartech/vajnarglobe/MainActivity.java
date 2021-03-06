package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

// --crkne ko je aplikacija nafrisno dana v sistem GPS not granted
// ++prevedi stringe
// ++ce kliknem pobrisi na zaslonu se vedno ostaja area, zbrise  pa prav iz baze
// ++scale se mora drsno spreminjati
// ++select aree
// ++zoptimiziraj setCurrentArea
// --probaj na razlicnih tablicah/phonih
// ++ne sporoca mi ce je server down ali ni connectiona, retries
// --terminal/monitor window poglej moonstalker
// --output do meje bos prisel sortiraj da dobimo najblizjega
// --menu za track view napolni F_track...
// --nastavljanje actiona v FAB
// ++med kontaktiranjem na server progres dialog/bar
// --ce se ne more kontaktirati server retry

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
  CurrentArea currentArea     = null;
  MyFragment  currentFragment = null;
  FloatingActionButton fab;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (C.GPS_SIMULATE) C.createArea();
    Display display = getWindowManager().getDefaultDisplay();
    display.getSize(C.size);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    fab = findViewById(R.id.fab);
    fab.setImageResource(R.drawable.play_arrow);
    fab.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        if (currentFragment instanceof F_Track)
          if (C.GPS_SIMULATE)
            _testTrack();
          else
            _confirmFirstPoint();
        else if (currentFragment instanceof F_Capture)
          ((F_Capture) currentFragment).push();
      }
    });

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    new GetAreas(this, new Runnable() {
      @Override public void run()
      {
        Log.i(C.TAG, "Areas imported: " + C.areas.size());
        setFragment("capture", F_Capture.class, new Bundle());
      }
    });
  }

  private void _testTrack()
  {
    setStatus();
    if (!C.isTesterRunning) {
      C.isTesterRunning = true;
      C.startTestGPSService(this);
    }
  }

  private void _confirmFirstPoint()
  {
    setStatus();
  }

  @Override
  public void onBackPressed()
  {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    switch(item.getItemId())
    {
    case R.id.id_capture:
      setFragment("capture", F_Capture.class, new Bundle());
      break;
    case R.id.id_track:
      setFragment("track", F_Track.class, new Bundle());
      break;
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void setFragment(String tag, Class<? extends MyFragment> cls, Bundle params)
  {
    currentFragment = createFragment(tag, cls, params);
    if (currentFragment == null) return;

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.content, currentFragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  public MyFragment createFragment(String tag, Class<? extends MyFragment> cls, Bundle params)
  {
    MyFragment frag;
    frag = (MyFragment) getSupportFragmentManager().findFragmentByTag(tag);
    if (frag == null && cls != null)
      try {
        frag = MyFragment.instantiate(cls, this);
        frag.setArguments(params);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    return frag;
  }

  public MyFragment getCurrentFragment()
  {
    return currentFragment;
  }

  public String tx(int stringId, Object... formatArgs)
  {
    if (formatArgs.length > 0)
      return getString(stringId, formatArgs);
    return getString(stringId);
  }

  void setStatus()
  {
    if (currentFragment instanceof F_Track) {
      F_Track frag = (F_Track) currentFragment;
      frag.calibrate(!frag.isCalibrated());
      if (frag.isCalibrated())
        frag.startAproximator();
      else
        frag.reset();
      fab.setImageResource(frag.isCalibrated() ? R.drawable.ic_pause : R.drawable.play_arrow);
      }
  }
}

