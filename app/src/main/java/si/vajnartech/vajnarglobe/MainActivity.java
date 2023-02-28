package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import si.vajnartech.vajnarglobe.rest.Areas;

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
// undo pri mark tockah, tudi pri construct medtem ko pri push ostane samo delete area!!
// urejanje obmocij da izbrises obmocje/select ...


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
  MyFragment  currentFragment = null;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Display display = getWindowManager().getDefaultDisplay();
    display.getSize(C.size);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    new Areas("GET", () -> setFragment("capture", F_Capture.class, new Bundle()), this);
  }

  private void confirmFirstPoint()
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
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == R.id.action_settings)
      return true;

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    if (item.getItemId() == R.id.id_capture)
      setFragment("capture", F_Capture.class, new Bundle());
    else if (item.getItemId() == R.id.id_track)
      setFragment("track", F_Track.class, new Bundle());

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
      }
  }
}

