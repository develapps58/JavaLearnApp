package models;

import java.util.Comparator;

import controllers.ChaptersController;
import controllers.SectionsController;
import core.IController;
import core.Model;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class Sections extends Model {
    private String title, description;
    private  int allQuestionsCount, passedQuestionsCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPassedQuestionsCount() {
        return passedQuestionsCount;
    }

    public void setPassedQuestionsCount(int passedQuestionsCount) {
        this.passedQuestionsCount = passedQuestionsCount;
    }

    public int getAllQuestionsCount() {
        return allQuestionsCount;
    }

    public void setAllQuestionsCount(int allQuestionsCount) {
        this.allQuestionsCount = allQuestionsCount;
    }

    public int getChaptersCount() {
        return ChaptersController.instance().getBySectionId(id).size();
    }

    @Override
    public IController getController() {
        return SectionsController.instance();
    }

    @Override
    public boolean equals(Object obj) {
        Sections section = (Sections)obj;
        return id == section.id;
    }
}
