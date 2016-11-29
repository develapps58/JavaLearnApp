package controllers;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.Controller;
import core.CurrentUser;
import core.Settings;
import models.Answers;
import models.Questions;
import models.UserAnswers;

/**
 * Created by Дмитрий on 24.11.2016.
 */

public class UserAnswersController extends Controller {
    private  static  UserAnswersController _instance = null;
    private UserAnswersController() {}

    public static UserAnswersController instance () {
        if(_instance == null) {
            _instance = new UserAnswersController();
            _instance.remoteLoad();
        }
        return  _instance;
    }

    @Override
    protected void addItem(JSONObject jsonObject) throws JSONException {
        UserAnswers userAnswers = new UserAnswers();
        userAnswers.setId(jsonObject.getString("_id"));
        userAnswers.setAnswerid(jsonObject.getString("answerid"));
        userAnswers.setEtag(jsonObject.getString("_etag"));

        Answers answer = AnswersController.instance().getById(userAnswers.getAnswerid());
        if(answer == null) return;
        Questions question = QuestionsController.instance().getById(answer.getQuestion_id());
        if(question == null) return;

        answer.setSelected(true);
        question.addCurrent(userAnswers);
    }

    @Override
    public boolean add(Object item) {
        return  false;
    }

    @Override
    public boolean remove(Object item) {
        return false;
    }

    @Override
    public String getEntityName() {
        CurrentUser currentUser = CurrentUser.getCurrentUser();
        if(currentUser == null) return null;
        return "user_answers?where={\"userid\":\"" + currentUser.getId() + "\"}";
    }

    @Override
    public String getError() {
        return error;
    }

}
