package controllers;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.Controller;
import core.Settings;
import models.Changes;

/**
 * Created by Дмитрий on 11.11.2016.
 */

public class ChangesController extends Controller {
    private  static  ChangesController _instance = null;
    private ArrayList<Changes> _collection = null;
    private ChangesController() {
        _collection = new ArrayList<Changes>();
    }

    public ArrayList<Changes> get_collection() {
        return _collection;
    }

    public static ChangesController instance () {
        if(_instance == null) {
            _instance = new ChangesController();
            if(!_instance.remoteLoad()) {
                _instance.isErrorFlag = true;
            }
        }
        return  _instance;
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        Changes changes = new Changes();
        changes.setGuid(jsonObject.getString("guid"));

        Changes localChanges = new Changes();
        if(localChanges.loadFirst()) {
            if(!localChanges.equals(changes)) {
                localChanges.removeFromLocalStorage();
                changes.insertToLocalStorage();
                Settings.existUpdate = true;
            }
        }
        else {
            changes.insertToLocalStorage();
            Settings.existUpdate = true;
        }
    }

    @Override
    public boolean add(Object item) {
        return false;
    }

    @Override
    public boolean remove(Object item) {
        return false;
    }

    @Override
    public String getEntityName() {
        return "changes";
    }

    @Override
    public String getError() {
        return error;
    }
}
