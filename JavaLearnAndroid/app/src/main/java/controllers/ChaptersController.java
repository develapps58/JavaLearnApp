package controllers;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.Controller;
import core.IController;
import core.LocalSyncController;
import core.LocalSyncModel;
import core.Settings;
import models.Chapters;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class ChaptersController extends LocalSyncController {
    private  static  ChaptersController _instance = null;
    private ArrayList<Chapters> _collection = null;

    private ChaptersController() {
        _collection = new ArrayList<Chapters>();
    }

    public ArrayList<Chapters> get_collection() {
        return _collection;
    }

    public static ChaptersController instance () {
        if(_instance == null) {
            _instance = new ChaptersController();
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
    public boolean add(Object item) {
        Chapters chapters = (Chapters) item;
        if(!_collection.contains(chapters)) {
            return _collection.add(chapters);
        }
        return  false;
    }

    @Override
    public boolean remove(Object item) {
        Chapters chapters = (Chapters) item;
        if(_collection.contains(chapters)) {
            return _collection.remove(chapters);
        }
        return false;
    }

    @Override
    public String getEntityName() {
        return "chapters";
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public String[] getFields() {
        return new String[] {"_id", "title", "content", "id_section", "roworder"};
    }

    @Override
    protected void addItem(ArrayList<Object> listObject) {
        Chapters chapter = new Chapters();
        chapter.setId(listObject.get(0).toString());
        chapter.setTitle(listObject.get(1).toString());
        chapter.setContent(listObject.get(2).toString());
        chapter.setId_section(listObject.get(3).toString());
        chapter.setRoworder(Integer.parseInt(listObject.get(4).toString()));
        add(chapter);
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        Chapters chapter = new Chapters();
        chapter.setId(jsonObject.getString("_id"));
        chapter.setTitle(jsonObject.getString("title"));
        chapter.setContent(jsonObject.getString("content"));
        chapter.setId_section(jsonObject.getString("sectionid"));
        chapter.setRoworder(jsonObject.getInt("roworder"));
        if(add(chapter)) {
            chapter.insertToLocalStorage();
        }
    }

    public Chapters getById (String id) {
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId().equals(id)) return _collection.get(i);
        }
        return null;
    }

    public ArrayList<Chapters> getBySectionId (String sectionId) {
        ArrayList<Chapters> chaptersList = new ArrayList<Chapters>();
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId_section().equals(sectionId)) {
                chaptersList.add(_collection.get(i));
            }
        }
        return  chaptersList;
    }
}
