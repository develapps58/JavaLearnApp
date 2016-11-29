package models;

import android.content.ContentValues;

import controllers.ChaptersController;
import core.IController;
import core.LocalSyncModel;
import core.Model;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class Chapters extends LocalSyncModel {
    private String title, content, id_section;
    private int roworder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId_section() {
        return id_section;
    }

    public void setId_section(String id_section) {
        this.id_section = id_section;
    }

    public int getRoworder() {
        return roworder;
    }

    public void setRoworder(int roworder) {
        this.roworder = roworder;
    }

    @Override
    public IController getController() {
        return ChaptersController.instance();
    }

    @Override
    protected void prepareDataToInsert(ContentValues values) {
        values.put("_id", id);
        values.put("title", title);
        values.put("content", content);
        values.put("id_section", id_section);
        values.put("roworder", roworder);
    }

    @Override
    public boolean equals(Object obj) {
        Chapters chapter = (Chapters) obj;
        return chapter.id.equals(id);
    }
}
