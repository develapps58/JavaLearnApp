package controllers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import core.IController;
import models.Sections;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class SectionsController implements IController {
    private  static  SectionsController _instance = null;
    private ArrayList<Sections> _collection = null;
    private SectionsController() {
        _collection = new ArrayList<Sections>();
    }

    public ArrayList<Sections> get_collection() {
        return _collection;
    }

    public static SectionsController instance () {
        if(_instance == null) _instance = new SectionsController();
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

    public Sections getById(int id) {
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId() == id) return _collection.get(i);
        }
        return null;
    }
}
