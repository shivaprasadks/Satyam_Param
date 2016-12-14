package com.think.survey2016;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog dialog;
    private String ques,  op3, qid, img;
    TextView ques_tv, total_tv,n_count,y_count;
    View v_ques;
    BootstrapButton btn_poll,btn_poll_done;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String uid;
    int res,total,op1,op2;
    private ImageView imageQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ques_tv = (TextView) findViewById(R.id.tv_question);
        total_tv = (TextView) findViewById(R.id.tv_total);
        n_count  =(TextView) findViewById(R.id.no_count);
        y_count  =(TextView) findViewById(R.id.yes_count);

        v_ques = (View) findViewById(R.id.qView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        imageQuestion = (ImageView) findViewById(R.id.qImg);
        btn_poll = (BootstrapButton) findViewById(R.id.poll);
        btn_poll_done = (BootstrapButton) findViewById(R.id.poll_done);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Intent in = getIntent();
        uid = in.getStringExtra("UID");

        getSharedPreferences("PREF", MODE_PRIVATE)
                .edit()
                .putString("UID", uid)
                .commit();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    new CheckoutAsync().execute("http://lifeofpet.com/survey/db_function.php?uid=" + uid);
                } else {
                    Toast.makeText(HomeActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isNetworkAvailable()) {
            new CheckoutAsync().execute("http://lifeofpet.com/survey/db_function.php?uid=" + uid);
        } else {
            Toast.makeText(HomeActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }


        btn_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(HomeActivity.this, PollActivity.class);
                in.putExtra("IMG", img);
                in.putExtra("UID",uid);
                in.putExtra("QID", qid);
                in.putExtra("QUESTION", ques);
                String res_str = String.valueOf(res);
                in.putExtra("OPTION",res_str);
                startActivity(in);
                finish();
            }
        });

        btn_poll_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this,"Already Voted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CheckoutAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data1 = EntityUtils.toString(entity);
                    JSONObject jsono = new JSONObject(data1);
                    res = jsono.getInt("type");
                    // total = jsono.getInt("total");
                    JSONArray question_array = jsono.getJSONArray("ques");
                    //     int res = jsono.getInt("status");
                    for (int i = 0; i < question_array.length(); i++) {
                        JSONObject json_data = question_array.getJSONObject(i);
                        img = json_data.getString("img");
                        qid = json_data.getString("oid");
                        ques = json_data.getString("question");


                    }
                    JSONArray poll_array = jsono.getJSONArray("poll");
                    //     int res = jsono.getInt("status");
                    for (int i = 0; i < poll_array.length(); i++) {
                        JSONObject json_data = poll_array.getJSONObject(i);
                        op1 = json_data.getInt("op1");
                        op2 = json_data.getInt("op2");
                        total = json_data.getInt("total");

                        return true;
                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.cancel();
            mSwipeRefreshLayout.setRefreshing(false);
            if (result) {
                Glide.with(HomeActivity.this).load(img)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(imageQuestion);
                total_tv.setText(" Votes : " + total );
                y_count.setText(" : "+op1);
                n_count.setText(" : "+op2);
                if (res == 0) {
                    btn_poll.setVisibility(View.VISIBLE);
                    btn_poll_done.setVisibility(View.GONE);
                } else if (res == 1) {
                    btn_poll.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, "Welcome Back ! \n You currently voted to YES", Toast.LENGTH_LONG).show();
                } else if (res == 2) {
                    btn_poll.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, "Welcome Back ! \n" +
                            " You currently voted to NO", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(HomeActivity.this, "Pull to refresh", Toast.LENGTH_SHORT).show();
            }

        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(HomeActivity.this,"Linked to playstore to rate app",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            getSharedPreferences("PREF", MODE_PRIVATE)
                    .edit()
                    .putString("UID", null)
                    .commit();
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
