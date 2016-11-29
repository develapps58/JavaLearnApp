package controllers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.Controller;
import models.Answers;
import models.Questions;
import models.UserAnswers;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class QuestionsController extends Controller {
    private  static  QuestionsController _instance = null;
    private ArrayList<Questions> _collection = null;
    private QuestionsController() {
        _collection = new ArrayList<Questions>();
    }

    public ArrayList<Questions> get_collection() {
        return _collection;
    }

    public static QuestionsController instance () {
        if(_instance == null) {
            _instance = new QuestionsController();
            _instance.remoteLoad();
        }
        return  _instance;
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        Questions question = new Questions();
        question.setId(jsonObject.getString("_id"));
        question.setTitle(jsonObject.getString("title"));
        question.setSection_id(jsonObject.getString("sectionid"));
        question.setRoworder(jsonObject.getInt("roworder"));
        add(question);
    }

    @Override
    public boolean add(Object item) {
        Questions question = (Questions) item;
        if(!_collection.contains(question)) {
            return _collection.add(question);
        }
        return  false;
    }

    @Override
    public boolean remove(Object item) {
        Questions question = (Questions) item;
        if(_collection.contains(question)) {
            return _collection.remove(question);
        }
        return false;
    }

    @Override
    public String getEntityName() {
        return "questions";
    }

    @Override
    public String getError() {
        return error;
    }

    public ArrayList<Questions> getBySectionId (String sectionId) {
        ArrayList<Questions> questionses = new ArrayList<Questions>();
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getSection_id().equals(sectionId)) {
                questionses.add(_collection.get(i));
            }
        }
        return questionses;
    }

    public int getCountBySectionId(String sectionId) {
        int count = 0;
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getSection_id().equals(sectionId)) {
                count++;
            }
        }
        return count;
    }

    public Questions getById(String id) {
        for(int i = 0; i < _collection.size(); i++) {
            if(_collection.get(i).getId().equals(id)) {
                return _collection.get(i);
            }
        }
        return null;
    }

    public Questions getPrevBySectionId (String sectionId, Questions current) {
        int currentIndex = current.getRoworder();
        ArrayList<Questions> questions = getBySectionId(sectionId);
        for(int i = 0; i < questions.size(); i++) {
            if(questions.get(i).getRoworder() == currentIndex-1)
                return questions.get(i);
        }
        return null;
    }

    public Questions getNextBySectionId (String sectionId, Questions current) {
        int currentIndex = current.getRoworder();
        ArrayList<Questions> questions = getBySectionId(sectionId);
        for(int i = 0; i < questions.size(); i++) {
            if(questions.get(i).getRoworder() == currentIndex+1)
                return questions.get(i);
        }
        return null;
    }

    public int calculateRightBySectionId (String sectionId) {
        ArrayList<Questions> questions = getBySectionId(sectionId);
        int rightAnswers = 0;
        for(int i = 0; i < questions.size(); i++) {
            int currentRightAnswers = 0;
            int currentNoRightAnswer = 0;
            ArrayList<Answers> answers = questions.get(i).getAnswers();
            for(int j = 0; j < answers.size(); j++) {
                if(answers.get(j).isSelected() == answers.get(j).is_correct()) {
                    currentRightAnswers++;
                }
                else {
                    currentNoRightAnswer++;
                }
            }
            if(currentNoRightAnswer == 0) {
                rightAnswers ++;
            }
        }
        return rightAnswers;
    }

    public void saveAnswers (String sectionId) {
        ArrayList<Questions> questions = getBySectionId(sectionId);
        for(int i = 0; i < questions.size(); i++) {
            Questions question = questions.get(i);
            question.removeAnswers();
            question.createAnswers();
            question.refresh();
        }
    }
}
