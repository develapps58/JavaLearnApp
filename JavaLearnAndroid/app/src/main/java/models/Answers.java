package models;

import android.content.ContentValues;

import controllers.AnswersController;
import core.IController;
import core.Model;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class Answers extends Model {
    String title, question_id;
    boolean is_correct, selected;
    int roworder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public boolean is_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }

    public int getRoworder() {
        return roworder;
    }

    public void setRoworder(int roworder) {
        this.roworder = roworder;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public IController getController() {
        return AnswersController.instance();
    }
}
