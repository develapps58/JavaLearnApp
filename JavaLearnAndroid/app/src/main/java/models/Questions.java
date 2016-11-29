package models;

import android.content.ContentValues;
import android.widget.Toast;

import java.util.ArrayList;

import controllers.AnswersController;
import controllers.QuestionsController;
import core.IController;
import core.Model;
import core.Settings;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class Questions extends Model {
    String title, section_id;
    private ArrayList<UserAnswers> current = new ArrayList<UserAnswers>();
    private ArrayList<UserAnswers> forAdded = new ArrayList<UserAnswers>();
    private ArrayList<UserAnswers> forRemoved = new ArrayList<UserAnswers>();
    int roworder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public int getRoworder() {
        return roworder;
    }

    public void setRoworder(int roworder) {
        this.roworder = roworder;
    }

    public void addCurrent (UserAnswers userAnswers) {
        current.add(userAnswers);
    }

    public UserAnswers getUserAnswerInCurrent (String answerId) {
        for(int i = 0; i < current.size(); i++) {
            if(current.get(i).getAnswerid().equals(answerId)) return current.get(i);
        }
        return null;
    }

    public UserAnswers getUserAnswerInAdded (String answerId) {
        for(int i = 0; i < forAdded.size(); i++) {
            if(forAdded.get(i).getAnswerid().equals(answerId)) return forAdded.get(i);
        }
        return null;
    }

    public UserAnswers getUserAnswerInRemoved (String answerId) {
        for(int i = 0; i < forRemoved.size(); i++) {
            if(forRemoved.get(i).getAnswerid().equals(answerId)) return forRemoved.get(i);
        }
        return null;
    }

    public void addForAdded (Answers answer) {
        UserAnswers userAnswers = getUserAnswerInCurrent(answer.getId());
        if(userAnswers == null)
            userAnswers = getUserAnswerInAdded(answer.getId());
        if(userAnswers == null)
            userAnswers = getUserAnswerInRemoved(answer.getId());
        if(userAnswers == null) {
            userAnswers = new UserAnswers();
            userAnswers.setAnswerid(answer.getId());
        }
        if(!forAdded.contains(userAnswers) && !current.contains(userAnswers)) {
            forAdded.add(userAnswers);
        }
        if(forRemoved.contains(userAnswers)) {
            forRemoved.remove(userAnswers);
        }
    }

    public void addForRemoved (Answers answer) {
        UserAnswers userAnswers = getUserAnswerInCurrent(answer.getId());
        if(userAnswers == null)
            userAnswers = getUserAnswerInAdded(answer.getId());
        if(userAnswers == null)
            userAnswers = getUserAnswerInRemoved(answer.getId());
        if(userAnswers == null) {
            userAnswers = new UserAnswers();
            userAnswers.setAnswerid(answer.getId());
        }
        if(current.contains(userAnswers) && !forRemoved.contains(userAnswers)) {
            forRemoved.add(userAnswers);
        }
        if(forAdded.contains(userAnswers)) {
            forAdded.remove(userAnswers);
        }
    }

    public void removeAnswers () {
        for(int i = 0; i < forRemoved.size(); i++) {
            if(!forRemoved.get(i).removeFromGlobalStorage()) {
                Toast.makeText(Settings.context, forRemoved.get(i).getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createAnswers () {
        for(int i = 0; i < forAdded.size(); i++) {
            if(!forAdded.get(i).saveToGlobalStorage()) {
                Toast.makeText(Settings.context, forAdded.get(i).getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void refresh () {
        for(int i = 0; i < forAdded.size(); i++)
            current.add(forAdded.get(i));

        for(int i = 0; i < forRemoved.size(); i++)
            current.remove(forRemoved.get(i));

        forAdded.clear();
        forRemoved.clear();
    }

    @Override
    public IController getController() {
        return QuestionsController.instance();
    }

    public ArrayList<Answers> getAnswers () {
        return AnswersController.instance().getByQuestionId(id);
    }
}
