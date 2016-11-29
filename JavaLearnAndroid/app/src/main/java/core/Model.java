package core;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.PorterDuff;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public abstract class Model {
    protected String id = "";
    protected String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract IController getController();

    public boolean save () throws ClassCastException {
        return getController().add(this);
    }

    @Override
    public boolean equals(Object o) {
        Model model = (Model) o;
        return id.equals(model.id);
    }
}
