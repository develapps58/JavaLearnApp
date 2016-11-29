package ru.dev58.javalearn;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

import adapters.SectionGridAdapter;
import controllers.ChangesController;
import controllers.ChaptersController;
import controllers.QuestionsController;
import controllers.SectionsController;
import controllers.UserAnswersController;
import core.CurrentUser;
import core.Settings;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static MainActivity self = null;
    static GridView sectionsView = null;
    NavigationView navigationView = null;
    static Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        self = this;
        Settings.context = this;

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        menu = navigationView.getMenu();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        sectionsView = (GridView)findViewById(R.id.section_gv);

        new LoadInterface().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_registration) {
            Intent intent = new Intent(this, RegistrationActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_logout) {
            CurrentUser.logout();
            invalidateSections();
            loginMenuVisible();
            setUserInfo();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadModels () {
        if(ChangesController.instance().isError()) {
            Toast.makeText(this, ChangesController.instance().getError(), Toast.LENGTH_LONG).show();
        }
        if(SectionsController.instance().isError()) {
            Toast.makeText(this, SectionsController.instance().getError(), Toast.LENGTH_LONG).show();
        }
        if(ChaptersController.instance().isError()) {
            Toast.makeText(this, ChaptersController.instance().getError(), Toast.LENGTH_LONG).show();
        }
    }

    public static void loginMenuVisible () {
        menu.findItem(R.id.nav_login).setVisible(true);
        menu.findItem(R.id.nav_registration).setVisible(true);
        menu.findItem(R.id.nav_logout).setVisible(false);
    }

    public static void logoutMenuVisible () {
        menu.findItem(R.id.nav_login).setVisible(false);
        menu.findItem(R.id.nav_registration).setVisible(false);
        menu.findItem(R.id.nav_logout).setVisible(true);
    }

    public static void setUserInfo () {
        NavigationView navigationView = (NavigationView)self.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView title = (TextView) headerView.findViewById(R.id.textView);
        if(CurrentUser.getCurrentUser() != null) {
            title.setText("Добро пожаловать, " + CurrentUser.getCurrentUser().getFullname());
        }
        else {
           title.setText("Добро пожаловать, " + CurrentUser.getDefaultLogin());
        }
        if(!Settings.isOnline) {
            title.setText(title.getText() + " (offline)");
        }
    }

    public static void invalidateSections () {
        sectionsView.invalidateViews();
    }

    public class  LoadInterface extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(Settings.isLoaded) {
                        publishProgress("", "");
                        return;
                    }
                    publishProgress("Загружаю данные пользователя...");
                    if(CurrentUser.loadLocalSettings()) {
                        Settings.isOnline = true;
                        publishProgress("Загружаю сохраненные тесты...");
                        UserAnswersController.instance();
                        publishProgress("Загружаю вопросы...");
                        QuestionsController.instance();
                        publishProgress("Загружаю обновления...", "f_end");
                    }
                    else {
                        publishProgress("Загружаю обновления...", "n_end");
                    }
                    Settings.isLoaded = true;
                }
            }).start();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values.length == 2) {
                if (values[1].equals("f_end")) {
                    logoutMenuVisible();
                    setUserInfo();
                    ((TextView) findViewById(R.id.loading_info)).setVisibility(View.INVISIBLE);
                }
                if (values[1].equals("n_end")) {
                    loginMenuVisible();
                    ((TextView) findViewById(R.id.loading_info)).setVisibility(View.INVISIBLE);
                }
                loadModels();

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    sectionsView.setNumColumns(3);
                }
                else {
                    sectionsView.setNumColumns(2);
                }
                sectionsView.setAdapter(new SectionGridAdapter(MainActivity.this, SectionsController.instance().get_collection()));
                return;
            }
            ((TextView)findViewById(R.id.loading_info)).setText(values[0]);
        }
    }
}
