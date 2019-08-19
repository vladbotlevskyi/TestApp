package test.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import test.app.fetchData.FetchData;
import test.app.fetchData.FetchDataResponse;

public class RepositoriesActivity extends AppCompatActivity implements FetchDataResponse {

    ListView repoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        repoListView = findViewById(R.id.repoList);

        String login = "{login}";
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(getResources().getString(R.string.UserLogin_key)) != null) {
            login = bundle.getString(getResources().getString(R.string.UserLogin_key));
        }

        FetchData fd = new FetchData();
        fd.setFdr(this);
        fd.execute("https://api.github.com/users/" + login + "/repos");
    }

    @Override
    public void onFetchDataResponse(JSONArray data) {
        List<String> reposNameList = new ArrayList();

        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jo = (JSONObject) data.get(i);
                reposNameList.add(jo.getString(getResources().getString(R.string.repoName_key)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, reposNameList);
        repoListView.setAdapter(adapter);
    }
}
