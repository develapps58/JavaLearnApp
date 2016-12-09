package models;

import controllers.ChaptersController;
import core.IController;
import core.Model;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class Chapters extends Model {
    private String title, content;
    private int id_section;

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

    public int getId_section() {
        return id_section;
    }

    public void setId_section(int id_section) {
        this.id_section = id_section;
    }

    @Override
    public IController getController() {
        return ChaptersController.instance();
    }

    @Override
    public boolean equals(Object obj) {
        Chapters chapter = (Chapters) obj;
        return chapter.id == id;
    }
}
