package com.youcmt.youdmcapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mDateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDateTextView = (TextView) findViewById(R.id.datetime);
        new FetchItemsTask().execute();
    }

    /**
     * Thread to fetch datetime from the API. Cannot do networking on the UI
     * thread in Android. As the app grows, I intend to use AsyncLoaders instead
     * and third party libraries to parse the JSON.
     */
    private class FetchItemsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String dateString = "";
            try {
                String tempString = jsonString();
                JSONObject jsonObject = new JSONObject(tempString);
                dateString = jsonObject.getString("datetime");

            } catch (IOException ioException) {
                dateString = "Error: " + ioException;
            } catch (JSONException je)
            {
                dateString = "Error: " + je;
            }
            return dateString;
        }

        @Override
        protected void onPostExecute(String datetimeString) {
            mDateTextView.setText(datetimeString);
        }

        private String jsonString() throws IOException
        {
            String finalUrl = "http://youcmt.com/api/datetime";
            HttpURLConnection connection = (HttpURLConnection) new URL(finalUrl)
                    .openConnection();
            try {
                connection = (HttpURLConnection) new URL(finalUrl)
                            .openConnection();
                connection.setRequestMethod("GET");
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    throw new IOException(connection.getResponseMessage()+ ": with " + finalUrl);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = connection.getInputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[1024];

                while((bytesRead = in.read(buffer)) >0)
                {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                return new String(out.toByteArray());
            } finally {
                connection.disconnect();
            }
        }
    }

}
