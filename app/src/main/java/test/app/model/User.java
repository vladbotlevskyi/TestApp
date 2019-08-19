package test.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private long id;
    private String login, avatarURL;

//    public User (String login, long id, String avatarURL){
//        this.login = login;
//        this.id = id;
//        this.avatarURL = avatarURL;
//    }
}
