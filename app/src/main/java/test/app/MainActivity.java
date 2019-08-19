package test.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import test.app.fetchData.FetchData;
import test.app.fetchData.FetchDataResponse;

public class MainActivity extends AppCompatActivity implements FetchDataResponse {

    ListView usersListView;

    private static String fetchJsonURL(String urlString) {
        String data = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inpStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inpStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersListView = findViewById(R.id.usersList);

        FetchData fd = new FetchData();
        fd.setFdr(this);
        fd.execute("https://api.github.com/users");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().
                name(getResources().getString(R.string.realmDB_file_name)).build();
        Realm realm = Realm.getInstance(config);
        realm.close();
        Log.i("Realm", realm.getPath());
    }

    @Override
    public void onFetchDataResponse(JSONArray _data) {
        final JSONArray data = _data;

        List<String> usersNameList = new ArrayList();

        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jo = (JSONObject) data.get(i);
                usersNameList.add(jo.getString(getResources().getString(R.string.UserLogin_key)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, usersNameList);
        usersListView.setAdapter(adapter);

        AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent reposListView = new Intent(MainActivity.this, RepositoriesActivity.class);
                try {
                    reposListView.putExtra(getResources().getString(R.string.UserLogin_key),
                            ((JSONObject) data.get(i)).get(getResources().getString(R.string.UserLogin_key)).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(reposListView);
            }
        };

        usersListView.setOnItemClickListener(listClick);
    }
}
