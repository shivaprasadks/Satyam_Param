package com.think.survey2016;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.think.survey2016.adapter.AdapterQuestion;
import com.think.survey2016.model.QuestionModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private String ques, op1, op2, op3, total, qid, img;
    TextView ques_tv, total_tv;
    View v_ques;
    BootstrapButton btn_poll, btn_poll_done;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView questionList;
    String uid;
    int res;
    private QuestionModel questionData;
    AdapterQuestion questionAdapter;
    private ImageView imageQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    /*    ques_tv = (TextView) findViewById(R.id.tv_question);
        total_tv = (TextView) findViewById(R.id.tv_total);

        v_ques = (View) findViewById(R.id.qView);
        imageQuestion = (ImageView) findViewById(R.id.qImg);
        btn_poll = (BootstrapButton) findViewById(R.id.poll);
        btn_poll_done = (BootstrapButton) findViewById(R.id.poll_done); */

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        questionList = (RecyclerView) findViewById(R.id.question_list);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    new CheckoutAsync().execute("http://lifeofpet.com/survey/get_question.php","http://lifeofpet.com/survey/get_poll.php");
                } else {
                    Toast.makeText(FirstActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

      /*  btn_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(FirstActivity.this, MainActivity.class);

                startActivity(in);
                finish();
            }
        });*/

        if (isNetworkAvailable()) {
            new CheckoutAsync().execute("http://lifeofpet.com/survey/get_question.php","http://lifeofpet.com/survey/get_poll.php");
        } else {
            Toast.makeText(FirstActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    private class CheckoutAsync extends AsyncTask<String, Void, Boolean> {
        List<QuestionModel> question_data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FirstActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            questionData = new QuestionModel();
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

                    JSONArray question_array = jsono.getJSONArray("ques");
                    //     int res = jsono.getInt("status");
                    for (int i = 0; i < question_array.length(); i++) {
                        JSONObject json_data = question_array.getJSONObject(i);


                        questionData.qimg = json_data.getString("img");
                        questionData.qid = json_data.getString("oid");
                        questionData.question = json_data.getString("question");
                      //  questionData.total = json_data.getString("totla");

                    }

                }
                //------------------>>
                HttpGet httppost1 = new HttpGet(urls[1]);
                HttpResponse response1 = httpclient.execute(httppost1);
                // StatusLine stat = response.getStatusLine();
                int status1 = response1.getStatusLine().getStatusCode();

                if (status1 == 200) {
                    HttpEntity entity = response1.getEntity();
                    String data1 = EntityUtils.toString(entity);
                    JSONObject jsono = new JSONObject(data1);
                    JSONArray jarray = jsono.getJSONArray("poll");
                    //  JSONArray jarray = jsono.getJSONArray(data1);
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json_data = jarray.getJSONObject(i);
                        questionData.op1 = json_data.getString("op1");
                        questionData.op2 = json_data.getString("op2");
                        questionData.total = json_data.getString("total");
                        question_data.add(questionData);

                    }
                    return true;
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


                LinearLayoutManager layoutManager = new LinearLayoutManager(FirstActivity.this, LinearLayoutManager.VERTICAL, false);
                questionAdapter = new AdapterQuestion(FirstActivity.this, question_data);
                questionList.setAdapter(questionAdapter);
                questionList.setLayoutManager(layoutManager);


            } else {
                Toast.makeText(FirstActivity.this, "Pull to Reload", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
