package core;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public abstract class Model {
    protected int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract IController getController();

    public boolean save () throws ClassCastException {
        return getController().add(this);
    }
}
