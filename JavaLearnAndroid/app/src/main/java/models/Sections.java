package models;

import android.content.ContentValues;

import controllers.ChaptersController;
import controllers.QuestionsController;
import controllers.SectionsController;
import core.CurrentUser;
import core.IController;
import core.LocalSyncModel;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class Sections extends LocalSyncModel {
    private String title, description;
    private int roworder;



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

    public int getRoworder() {
        return roworder;
    }

    public void setRoworder(int roworder) {
        this.roworder = roworder;
    }

    public String getShortDescription () {
        if(description.length() == 0) return description;
        String noTagDescription = android.text.Html.fromHtml(description).toString();
        return noTagDescription;
        /*if(noTagDescription.length() <= 80) return noTagDescription;
        return  noTagDescription.substring(0, 80) + "...";*/
    }

    public int getPassedQuestionsCount() {
        if(CurrentUser.getCurrentUser() != null) {
            return QuestionsController.instance().calculateRightBySectionId(id);
        }
        return 0;
    }

    public int getAllQuestionsCount() {
        if(CurrentUser.getCurrentUser() == null) {
            return 0;
        }
        return QuestionsController.instance().getCountBySectionId(id);
    }

    public int getChaptersCount() {
        return ChaptersController.instance().getBySectionId(id).size();
    }

    @Override
    public IController getController() {
        return SectionsController.instance();
    }

    @Override
    protected void prepareDataToInsert(ContentValues values) {
        values.put("_id", id);
        values.put("title", title);
        values.put("description", description);
        values.put("roworder", roworder);
    }

    @Override
    public boolean equals(Object obj) {
        Sections section = (Sections)obj;
        return id.equals(section.id);
    }
}
