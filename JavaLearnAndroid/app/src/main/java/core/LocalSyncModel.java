package core;

import android.content.ContentValues;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public abstract class LocalSyncModel extends Model {
    protected abstract void prepareDataToInsert(ContentValues values);

    public boolean insertToLocalStorage () {
        LocalStorage storage = new LocalStorage(Settings.context);
        ContentValues values = new ContentValues();
        prepareDataToInsert(values);
        boolean result = storage.insert(values, getController().getEntityName());
        if(!result) error = storage.getError();
        return result;
    }

    public boolean removeFromLocalStorage () {
        LocalStorage storage = new LocalStorage(Settings.context);
        boolean result = storage.remove(getController().getEntityName(), new KeyValuePair<String, String>("_id", getId()));
        if(!result) error = storage.getError();
        return result;
    }

    public boolean loadFirst () {
        return false;
    }

}
