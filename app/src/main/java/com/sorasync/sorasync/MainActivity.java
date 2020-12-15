package com.sorasync.sorasync;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggler;
    private NavigationView navigationView;
    private JcPlayerView jcPlayerView;
    private FrameLayout mainFrameLayout;
    private GestureDetector gestureDetector;
    private SongsRecycleViewAdapter songsRecycleViewAdapter;
    private Handler handler;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initializing
        this.mainFrameLayout = findViewById(R.id.fragement_container);
        this.jcPlayerView = findViewById(R.id.jcPlayer);
        this.handler = new Handler();
        this.toolbar = findViewById(R.id.toolbar);
        this.auth = FirebaseAuth.getInstance();
        this.navigationView = findViewById(R.id.drawer_navigation_view);

        //changing nav header text on login and logout
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                View headerView = navigationView.getHeaderView(0);
                TextView drawer_greeting = headerView.findViewById(R.id.drawer_header_greeting);
                TextView drawer_full_name = headerView.findViewById(R.id.drawer_header_full_name);
                if(auth.getCurrentUser() != null){
                    drawer_greeting.setText("Hello,");
                    drawer_full_name.setText(auth.getCurrentUser().getDisplayName());
                }
                else{
                    drawer_greeting.setText("");
                    drawer_full_name.setText(R.string.drawer_greeting);
                }
            }
        });

        // methods
        addGesture();
        jcPlayerListener();

        //set actionbar
        setSupportActionBar(this.toolbar);

        //link toolbar and set icon toolbar
        setMenuIcon();

        //Navigation
        this.navigationView.bringToFront();
        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.songs_menu_item:
                        SongsFragment songsFragment = (SongsFragment) getSupportFragmentManager().findFragmentByTag("SongsFragment");
                        if (songsFragment == null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new SongsFragment(),
                                    "SongsFragment").commit();
                        }
                        break;
                    case R.id.upload_songs_menu_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new UploadFragment()).commit();
                        break;
                    case R.id.my_music_menu_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new MyMusicFragment()).commit();
                        break;
                    case R.id.logout_menu_item:
                        auth.signOut();
                        MainActivity.this.hideShowItems();
                        // redirect to login fragment
                        changeSelectedMenuItemTo(R.id.login_menu_item);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new Login()).commit();
                        Toast.makeText(MainActivity.this, "Logout ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.login_menu_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new Login()).commit();
                        break;
                    case R.id.register_menu_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new Register()).commit();
                        break;
                }
                mdrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (savedInstanceState == null) { // to check if the app is loaded from background or started clean.
            // if its a clean start then go to
            if (auth.getCurrentUser() != null) {
                //songs page
                changeSelectedMenuItemTo(R.id.songs_menu_item);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new SongsFragment(),
                        "SongsFragment").commit();
            } else {
                //login page
                changeSelectedMenuItemTo(R.id.login_menu_item);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new Login()).commit();
            }
        }
        hideShowItems();
    }

    public void hideShowItems() {
        // hide and show menuitem
        Menu menu = this.navigationView.getMenu();
        if (auth.getCurrentUser() != null) {
            menu.findItem(R.id.login_menu_item).setVisible(false);
            menu.findItem(R.id.register_menu_item).setVisible(false);
            menu.findItem(R.id.logout_menu_item).setVisible(true);
            menu.findItem(R.id.navigation_menu_group).setVisible(true);
        } else {
            menu.findItem(R.id.login_menu_item).setVisible(true);
            menu.findItem(R.id.register_menu_item).setVisible(true);
            menu.findItem(R.id.logout_menu_item).setVisible(false);
            menu.findItem(R.id.navigation_menu_group).setVisible(false);
        }
    }

    private void setMenuIcon() {
        this.mdrawer = findViewById(R.id.navigation_drawer);
        this.mtoggler = new ActionBarDrawerToggle(this, this.mdrawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.mdrawer.addDrawerListener(this.mtoggler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show ham menu icon on default actionbar
        this.mtoggler.syncState(); // syncs with the open drawer and back button
    }

    @Override
    public void onBackPressed() {
        if (this.mdrawer.isDrawerOpen(GravityCompat.START)) { //check if drawer is open
            this.mdrawer.closeDrawer(GravityCompat.START); // close drawer if open
        } else {
            super.onBackPressed(); // if drawer is not open then call super back press
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // to enable ham menu icon to show navigation drawer.
        if (this.mtoggler.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jcPlayerView.isPlaying()) {
            jcPlayerView.kill();
        }
    }

    private void addGesture() {
        //Gesture code
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffy = e2.getY() - e1.getY();
                float diffx = e2.getX() - e1.getX();
                if (Math.abs(diffx) < Math.abs(diffy)) {
                    // up or down swipe
                    if (Math.abs(diffy) > 10 && Math.abs(velocityY) > 10) {
                        if (diffy > 0) {
                            //swipe bottom
                            jcPlayerView.pause();
                            jcPlayerView.setVisibility(View.GONE);
                            mainFrameLayout.setPadding(0, 0, 0, 0);
                            songsRecycleViewAdapter.setRow_index(-1);
                            songsRecycleViewAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Player Closed", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            //swipe up
                            //Toast.makeText(MainActivity.this, "up swipe", Toast.LENGTH_SHORT).show();
                            //return true;
                        }
                    }
                }
                //else left or right swipe;
                // compare diffx and velocityx
                return false;
            }

        });

        //touch listener
        jcPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

        });
    }

    private void jcPlayerListener() {
        //jcplayer listener
        jcPlayerView.setJcPlayerManagerListener(new JcPlayerManagerListener() {
            @Override
            public void onPreparedAudio(JcStatus jcStatus) {
                songsRecycleViewAdapter.setRow_index(jcStatus.getJcAudio().getPosition());
                songsRecycleViewAdapter.notifyDataSetChanged();
                //adding slight delay to get the full height
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mainFrameLayout.getPaddingBottom() != jcPlayerView.getHeight()) {
                            mainFrameLayout.setPadding(0, 0, 0, jcPlayerView.getHeight());
                        }
                    }
                }, 50);
            }

            @Override
            public void onCompletedAudio() {

            }

            @Override
            public void onPaused(JcStatus jcStatus) {

            }

            @Override
            public void onContinueAudio(JcStatus jcStatus) {

            }

            @Override
            public void onPlaying(JcStatus jcStatus) {

            }

            @Override
            public void onTimeChanged(JcStatus jcStatus) {

            }

            @Override
            public void onStopped(JcStatus jcStatus) {
            }

            @Override
            public void onJcpError(Throwable throwable) {
            }
        });
    }

    public JcPlayerView getJcPlayerView() {
        return jcPlayerView;
    }

    public FrameLayout getMainFrameLayout() {
        return mainFrameLayout;
    }

    public void setSongsRecycleViewAdapter(SongsRecycleViewAdapter songsRecycleViewAdapter) {
        this.songsRecycleViewAdapter = songsRecycleViewAdapter;
    }

    public SongsRecycleViewAdapter getSongsRecycleViewAdapter() {
        return this.songsRecycleViewAdapter;
    }

    public void changeSelectedMenuItemTo(int rId) {
        navigationView.setCheckedItem(rId);
    }

}
