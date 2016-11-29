package core;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public abstract class LocalSyncController extends Controller {

    @Override
    protected boolean remoteLoad() {
        LocalStorage storage = new LocalStorage(Settings.context);
        storage.clear(getEntityName());
        return super.remoteLoad();
    }

    public boolean localLoad() {
        LocalStorage storage = new LocalStorage(Settings.context);
        ArrayList<ArrayList<Object>> items = storage.load(getFields(), getEntityName());
        for(int i = 0; i < items.size(); i++)
            addItem(items.get(i));
        return true;
    }

    public boolean checkUpdate() {
        return false;
    }
    public boolean tableExist () {
        LocalStorage storage = new LocalStorage(Settings.context);
        return storage.tableExist(getEntityName());
    }
    protected abstract void addItem (ArrayList<Object> listObject);
    public abstract String [] getFields ();
}
