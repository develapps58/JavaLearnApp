package controllers;

import android.widget.ListPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.Controller;
import models.Answers;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class AnswersController extends Controller {

    private  static  AnswersController _instance = null;
    private ArrayList<Answers> _collection = null;
    private AnswersController() {
        _collection = new ArrayList<Answers>();
    }

    public ArrayList<Answers> get_collection() {
        return _collection;
    }

    public static AnswersController instance () {
        if(_instance == null) {
            _instance = new AnswersController();
            _instance.remoteLoad();
        }
        return  _instance;
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        Answers answer = new Answers();
        answer.setId(jsonObject.getString("_id"));
        answer.setTitle(jsonObject.getString("title"));
        answer.setQuestion_id(jsonObject.getString("questionid"));
        answer.setIs_correct(jsonObject.getBoolean("iscorrect"));
        answer.setRoworder(jsonObject.getInt("roworder"));
        add(answer);
    }

    @Override
    public boolean add(Object item) {
        Answers answer = (Answers)item;
        if(!_collection.contains(answer)) {
            return _collection.add(answer);
        }
        return  false;
    }

    @Override
    public boolean remove(Object item) {
        Answers answer = (Answers)item;
        if(_collection.contains(answer)) {
            return _collection.remove(answer);
        }
        return false;
    }

    @Override
    public String getEntityName() {
        return "answers";
    }

    @Override
    public String getError() {
        return error;
    }

    public ArrayList<Answers> getByQuestionId (String questionId) {
        ArrayList<Answers> answerses = new ArrayList<Answers>();
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getQuestion_id().equals(questionId)) {
                answerses.add(_collection.get(i));
            }
        }
        return answerses;
    }

    public Answers getById (String id) {
        for (int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId().equals(id)) return _collection.get(i);
        }
        return null;
    }
}
