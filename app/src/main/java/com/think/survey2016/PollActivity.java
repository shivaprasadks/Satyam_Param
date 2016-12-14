package com.think.survey2016;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PollActivity extends AppCompatActivity {
    private String ques, op1, op2, qid, ans = null, uid, res;
    private RadioGroup radio_ans;
    RadioButton radio_op1, radio_op2, radio_Button;
    ImageView imageQuestion;
    TextView tv_ques;
    private ProgressDialog dialog;
    View btn_vote;

    private String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        Intent in = getIntent();

        qid = in.getStringExtra("QID");
        ques = in.getStringExtra("IMG");
        uid = in.getStringExtra("UID");
        res = in.getStringExtra("OPTION");

        radio_op1 = (RadioButton) findViewById(R.id.op1);
        radio_op2 = (RadioButton) findViewById(R.id.op2);
        tv_ques = (TextView) findViewById(R.id.tv_question);
        btn_vote = (View) findViewById(R.id.next);
        radio_ans = (RadioGroup) findViewById(R.id.radioAns);
        imageQuestion = (ImageView) findViewById(R.id.qImg);

        radio_op1.setText("1");
        radio_op1.setTextColor(Color.WHITE);
        radio_op2.setText("2");
        radio_op2.setTextColor(Color.WHITE);
        Toast.makeText(PollActivity.this, res, Toast.LENGTH_SHORT).show();
        radio_ans.check(res.equals("1") ? R.id.op1 : R.id.op2);
      /*  if (res == "1") {
            radio_op1.setChecked(true);
            radio_op2.setChecked(false);
        } else if (res == "2") {
            radio_op2.setChecked(true);
            radio_op1.setChecked(false);
        } */

        tv_ques.setText(ques);
      /*  String uid = getSharedPreferences("PREF", MODE_PRIVATE).getString("UID", null);
        new getQuestionAsync().execute("http://lifeofpet.com/survey/db_function.php?uid=" + uid); */

        Glide.with(PollActivity.this).load(ques)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(imageQuestion);

        btn_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radio_ans.getCheckedRadioButtonId();

                radio_Button = (RadioButton) findViewById(selectedId);
                try {
                    ans = radio_Button.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(PollActivity.this, "Select An Option", Toast.LENGTH_SHORT).show();
                }

                if (ans == null) {

                } else {
                    String uid = getSharedPreferences("PREF", MODE_PRIVATE).getString("UID", null);

                    new CheckoutAsync().execute("http://lifeofpet.com/survey/polling.php?uid=" + uid + "&ans=" + ans + "&qid=" + qid);
                    Log.d("URL", "http://lifeofpet.com/survey/polling.php?uid=" + uid + "&ans=" + ans + "&qid=" + qid);
                }

            }
        });

    }

    private class CheckoutAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PollActivity.this);
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
                    // JSONArray question_array = jsono.getJSONArray("ques");
                    //     int res = jsono.getInt("status");


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

            if (result) {
                //  Toast.makeText(PollActivity.this, "Thanks for voting", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PollActivity.this, ResultActivity.class);
                startActivity(i);
                finish();

            } else {
                //   Toast.makeText(PollActivity.this, "Thanks for voting", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PollActivity.this, ResultActivity.class);
                startActivity(i);
                finish();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PollActivity.this, HomeActivity.class);
        i.putExtra("UID", uid);
        startActivity(i);
        finish();
    }
}
