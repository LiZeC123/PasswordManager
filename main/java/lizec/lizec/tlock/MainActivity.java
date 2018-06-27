package lizec.lizec.tlock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.aes.exception.SameKeyException;
import lizec.lizec.tlock.base.NoScreenShotActivity;
import lizec.lizec.tlock.base.PwdAdapter;
import lizec.lizec.tlock.file.FileHelper;
import lizec.lizec.tlock.model.PwdInfo;
import lizec.lizec.tlock.rand.RandomPassword;

public class MainActivity extends NoScreenShotActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private AESMap map;
    private String TAG = "MyLog";
    private RecyclerView recyclerView;
    private PwdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDrawerLayout(toolbar);
        initNavigationView();
        initMap();
        initRecyclerView();
        initFabMenu();
    }

    private void initDrawerLayout(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initMap(){
        File dataFile = new File(getFilesDir(),"pwd.data");
        try{
            byte[] pwd = getIntent().getByteArrayExtra("pwd");
            byte[] data = FileHelper.fileToByte(dataFile);
            map = new AESMap(pwd,data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initFabMenu(){
        FloatingActionButton fabFind = findViewById(R.id.fabItemFind);

        findViewById(R.id.fabItemAdd).setOnClickListener(v -> {
            RandomPassword rand = new RandomPassword();
            try {
                PwdInfo info = new PwdInfo(rand.getOne(5),rand.getOne(5),rand.getOne());
                map.addPair(info.getAPPName(),info);
                adapter.addItemAndNotify(info);
                Log.i(TAG, "initFabMenu: 添加数据成功");
            } catch (SameKeyException e) {
                e.printStackTrace();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PwdAdapter(map);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        try {
            File dataFile = new File(getFilesDir(),"pwd.data");
            FileHelper.write(dataFile,map.encode());
            Log.i(TAG, "onBackPressed: 数据保存成功");
        } catch (IOException | GeneralSecurityException e) {
            Toast.makeText(this,"数据保存失败",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
