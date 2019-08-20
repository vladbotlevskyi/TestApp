package test.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import test.app.db.RealmDB;
import test.app.fetchData.FetchDataResponse;
import test.app.model.Repository;

public class RepositoriesActivity extends Activity implements FetchDataResponse {

    private ListView repoListView;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_repositories);

        repoListView = findViewById(R.id.repoList);

        login = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(getResources().getString(R.string.UserLogin_key)) != null) {
            login = bundle.getString(getResources().getString(R.string.UserLogin_key));
        }

//        DataFetch df = new DataFetch();
//        df.setFdr(this);
//        df.execute("https://api.github.com/users/" + login + "/repos");

        setUpReposListView();
    }

    @Override
    public void onFetchDataResponse(JSONArray data) {
        updateRealmDB(data);
        setUpReposListView();
    }

    private void setUpReposListView() {
        JSONArray data = null;
        try {
            data = retrieveDataFromRealmDB();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void updateRealmDB(JSONArray _data) {
        final JSONArray data = _data;

        Realm realm = RealmDB.getInstance(this);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jo = (JSONObject) data.get(i);

                        Repository repo = new Repository();
                        repo.setId(jo.getLong(getResources().getString(R.string.repoId_key)));
                        repo.setOwner(login);
                        repo.setData(jo.toString());

                        realm.copyToRealmOrUpdate(repo);
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
        RealmResults<Repository> repos = realm.where(Repository.class).and()
                .equalTo(getResources().getString(R.string.repoOwner_key), login).findAll();
        String result = "[";
        for (int i = 0; i < repos.size(); i++) {
            result = result + repos.get(i).getData();
            if (i != repos.size() - 1)
                result = result + ",";
        }
        return new JSONArray(result + "]");

    }
}
