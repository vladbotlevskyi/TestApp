package test.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Repository extends RealmObject {

    @PrimaryKey
    private long id;
    private String name, owner;

//    public Repository(long id, String name, String owner){
//        this.id = id;
//        this.name = name;
//        this.owner = owner;
//    }
}
