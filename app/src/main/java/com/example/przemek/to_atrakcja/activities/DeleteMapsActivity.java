package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.przemek.to_atrakcja.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteMapsActivity extends Activity
{
    private static String URL_get_maps = "http://192.168.0.13/get_maps.php";
    private static String URL_delete_map = "http://192.168.0.13/delete_map.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletemaps);
        new PopulateTable().execute();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, AdministratorActivity.class);
        startActivity(intent);
    }

    class PopulateTable extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(URL_get_maps);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);

                urlConnection.connect();

                urlConnection.getResponseMessage();
                urlConnection.getResponseCode();

                InputStreamReader aa= new InputStreamReader((urlConnection.getInputStream()));
                BufferedReader br = new BufferedReader(aa);

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                response=sb.toString();
                br.close();

                urlConnection.disconnect();

                jsonResponse = new JSONObject(response) ;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            try
            {
                if (jsonResponse.getInt("success")==1)
                {
                    TableLayout table = (TableLayout) findViewById(R.id.TableDeleteMapData);
                    JSONArray jsonrzedy=(JSONArray ) jsonResponse.get("Mapy");
                    TextView WzorName = (TextView) findViewById(R.id.DeleteMapDataNameExample);
                    TextView WzorURL = (TextView) findViewById(R.id.DeleteMapDataURLExample);
                    LinearLayout WzorLinear = (LinearLayout) findViewById(R.id.DeleteMapDataLinearLayoutExample);
                    for(int n = 0; n < jsonrzedy.length(); n++)
                    {
                        JSONObject object = jsonrzedy.getJSONObject(n);
                        TableRow row = new TableRow(DeleteMapsActivity.this);
                        LinearLayout Linear = new LinearLayout(DeleteMapsActivity.this, null, R.style.LinearLayoutTableStyle);
                        Linear.setLayoutParams(WzorLinear.getLayoutParams());
                        TextView TextName = new TextView(DeleteMapsActivity.this,null , R.style.TableCellNameStyle);
                //        TextView TextURL = new TextView(DeleteMapsActivity.this, null, R.style.TableCellURLStyle );
                //       TextName.setLayoutParams(WzorName.getLayoutParams());
                 //       TextURL.setLayoutParams(WzorURL.getLayoutParams());
                        Linear.addView(TextName);
                 //       Linear.addView(TextURL);
                        row.addView(Linear);
                        table.addView(row);
                    }
                }
                else
                {
                    TextView DatabaseResponse = (TextView) findViewById(R.id.DatabaseResponseAddMap);
                    DatabaseResponse.setText(jsonResponse.getString("message"));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }
}
