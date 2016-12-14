package com.think.survey2016;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AdhaarActivity extends AppCompatActivity {
    private String scanContent;
    TextView scancon, userID, userName;
    String UID = null, uName;
    private ProgressDialog dialog;
    View btn_nxt, btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhaar);
        Intent in = getIntent();
        scanContent = in.getStringExtra("SCAN_ID");

        scancon = (TextView) findViewById(R.id.scanID);
        userID = (TextView) findViewById(R.id.uid);
        userName = (TextView) findViewById(R.id.uname);
        btn_nxt = (View) findViewById(R.id.next);
        btn_retry = (View) findViewById(R.id.retry);

        scancon.setText(scanContent);
        setupToolbar();

        if (getMyData(scanContent)) {
            btn_nxt.setVisibility(View.VISIBLE);
            Toast.makeText(this, "VALID QR CODE", Toast.LENGTH_SHORT).show();
        } else {
            btn_retry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "QR CODE INCORRECT", Toast.LENGTH_SHORT).show();
        }
        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isNetworkAvailable()) {
                        new CheckoutAsync().execute("http://lifeofpet.com/survey/user_insert.php?uid=" + UID + "&name=" + URLEncoder.encode(uName, "UTF-8"));
                    }else {

                        Toast.makeText(AdhaarActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class CheckoutAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AdhaarActivity.this);
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
                    JSONArray question_array = jsono.getJSONArray("info");
                    //     int res = jsono.getInt("status");
                    for (int i = 0; i < question_array.length(); i++) {
                        JSONObject json_data = question_array.getJSONObject(i);

                        //  ques = json_data.getString("question");

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

            if (result) {
                Intent next_intent = new Intent(AdhaarActivity.this, HomeActivity.class);
                next_intent.putExtra("UID", UID);
                startActivity(next_intent);

            } else {
                Toast.makeText(AdhaarActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean getMyData(String scanContent) {
        String str = scanContent;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(str)));
            NodeList nList = document.getElementsByTagName("PrintLetterBarcodeData");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    UID = eElement.getAttribute("uid");
                    uName = eElement.getAttribute("name");

                    userID.append(" : " + UID);
                    userName.append(" : " + uName);
                }
                if (UID != null) return true;

            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
