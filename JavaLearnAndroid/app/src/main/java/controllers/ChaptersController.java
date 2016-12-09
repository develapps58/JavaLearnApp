package controllers;

import java.util.ArrayList;

import core.IController;
import models.Chapters;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class ChaptersController implements IController {
    private  static  ChaptersController _instance = null;
    private ArrayList<Chapters> _collection = null;

    private ChaptersController() {
        _collection = new ArrayList<Chapters>();
    }

    public ArrayList<Chapters> get_collection() {
        return _collection;
    }

    public static ChaptersController instance () {
        if(_instance == null) _instance = new ChaptersController();
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

    public ArrayList<Chapters> getBySectionId (int sectionId) {
        ArrayList<Chapters> chaptersList = new ArrayList<Chapters>();
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId_section() == sectionId) {
                chaptersList.add(_collection.get(i));
            }
        }
        return  chaptersList;
    }
}
