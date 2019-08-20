package test.app;

import android.content.Intent;
import android.os.Bundle;
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
import io.realm.RealmResults;
import test.app.db.RealmDB;
import test.app.fetchData.FetchDataResponse;
import test.app.model.User;

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

//        DataFetch df = new DataFetch();
//        df.setFdr(this);
//        df.execute("https://api.github.com/users");

        Realm.init(this);
        setUpUsersListView();

    }

    @Override
    public void onFetchDataResponse(JSONArray data) {
        updateRealmDB(data);
        setUpUsersListView();
    }

    private void setUpUsersListView() {
        final JSONArray data;
        try {
            data = retrieveDataFromRealmDB();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

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
//                Bundle args = new Bundle();
//                try {
//                    args.putString(getResources().getString(R.string.UserLogin_key),
//                            ((JSONObject) data.get(i)).get(getResources().getString(R.string.UserLogin_key)).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                RepositoriesActivity fragment = new RepositoriesActivity();
//                fragment.setArguments(args);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                //transaction.replace(null, fragment);
//                transaction.commit();
            }
        };

        usersListView.setOnItemClickListener(listClick);
    }

    private void updateRealmDB(JSONArray _data) {
        final JSONArray data = _data;

        Realm realm = RealmDB.getInstance(this);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jo = (JSONObject) data.get(i);

                        User user = new User();
                        user.setId(jo.getLong(getResources().getString(R.string.userId_key)));
                        user.setData(jo.toString());

                        realm.copyToRealmOrUpdate(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //realm.close();
    }

    private JSONArray retrieveDataFromRealmDB() throws JSONException {
        Realm realm = RealmDB.getInstance(this);
        RealmResults<User> users = realm.where(User.class).findAll();
        String result = "[";
        for (int i = 0; i < users.size(); i++) {
            result = result + users.get(i).getData();
            if (i != users.size() - 1)
                result = result + ",";
        }
        return new JSONArray(result + "]");

    }
}
