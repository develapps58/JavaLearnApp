package controllers;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import core.Controller;
import core.IController;
import core.LocalSyncController;
import core.Settings;
import http.HTTPRequest;
import models.Sections;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class SectionsController extends LocalSyncController {
    private  static  SectionsController _instance = null;
    private ArrayList<Sections> _collection = null;
    private SectionsController() {
        _collection = new ArrayList<Sections>();
    }

    public ArrayList<Sections> get_collection() {
        return _collection;
    }

    public static SectionsController instance () {
        if(_instance == null) {
            _instance = new SectionsController();
            if(!_instance.tableExist() || Settings.existUpdate) {
                if(!_instance.remoteLoad()) {
                    _instance.isErrorFlag = true;
                }
            }
            else  {
                if(!_instance.localLoad()) {
                    _instance.isErrorFlag = true;
                }
            }
        }
        return  _instance;
    }

    @Override
    public boolean add(Object item) throws ClassCastException {
        Sections section = (Sections) item;
        if(!_collection.contains(section)) {
            return _collection.add(section);
        }
        return  false;
    }

    @Override
    public boolean remove(Object item) throws ClassCastException {
        Sections section = (Sections) item;
        if(_collection.contains(section)) {
            return _collection.remove(section);
        }
        return false;
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        Sections section = new Sections();
        section.setId(jsonObject.getString("_id"));
        section.setTitle(jsonObject.getString("title"));
        section.setDescription(jsonObject.getString("description"));
        section.setRoworder(jsonObject.getInt("roworder"));
        if(add(section)) {
            section.insertToLocalStorage();
        }
    }

    @Override
    protected void addItem(ArrayList<Object> listObject) {
        Sections section = new Sections();
        section.setId(listObject.get(0).toString());
        section.setTitle(listObject.get(1).toString());
        section.setDescription(listObject.get(2).toString());
        section.setRoworder(Integer.parseInt(listObject.get(3).toString()));
        add(section);
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public String getEntityName() {
        return "sections";
    }

    @Override
    public String[] getFields() {
        return new String[] {"_id", "title", "description", "roworder"};
    }

    public Sections getById(String id) {
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId().equals(id)) return _collection.get(i);
        }
        return null;
    }
}
