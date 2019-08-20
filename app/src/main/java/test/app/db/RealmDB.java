package test.app.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import test.app.R;

public class RealmDB {
    private static volatile Realm instance;

    public static Realm getInstance(Context context) {
        if (instance == null) {
            synchronized (Realm.class) {
                if (instance == null)
                    instance = createRealmDbInstance(context);
            }
        }
        return instance;
    }

    private static Realm createRealmDbInstance(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder().
                name(context.getResources().getString(R.string.realmDB_file_name))
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(config);
    }
}
