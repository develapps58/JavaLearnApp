package models;

import android.content.ContentValues;

import java.util.ArrayList;

import controllers.ChangesController;
import core.IController;
import core.LocalStorage;
import core.LocalSyncModel;
import core.Settings;

/**
 * Created by Дмитрий on 11.11.2016.
 */

public class Changes extends LocalSyncModel {
    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public IController getController() {
        return ChangesController.instance();
    }

    @Override
    protected void prepareDataToInsert(ContentValues values) {
        values.put("_id", id);
        values.put("guid", guid);
    }

    @Override
    public boolean equals(Object o) {
        return guid.equals(((Changes)o).guid);
    }

    @Override
    public boolean loadFirst() {
        LocalStorage localStorage = new LocalStorage(Settings.context);
        ArrayList<Object> objects = localStorage.loadFirst(new String[] {"_id", "guid"}, getController().getEntityName());
        if(objects.size() == 0) {
            return false;
        }
        id = objects.get(0).toString();
        guid = objects.get(1).toString();
        return true;
    }
}
