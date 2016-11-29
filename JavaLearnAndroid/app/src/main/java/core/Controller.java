package core;

import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import http.HTTPRequest;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public abstract class Controller implements IController {
    protected String error, requestString = "";
    protected boolean isErrorFlag;

    protected boolean remoteLoad () {
        if(!Settings.isOnline) {
            error = "Нет подключения к интернету";
            return false;
        }
        try {
            HTTPRequest request = new HTTPRequest(new URL(Settings.getDomain() + getEntityName() + requestString));
            if(request.Get()) {
                JSONObject jsonObject = request.GetResponseResultAsJSON();
                JSONArray jArray = (JSONArray)jsonObject.get("_items");
                for(int i = 0; i < jArray.length(); i++) {
                    addItem((JSONObject)jArray.get(i));
                }
                return true;
            }
            else {
                error = request.GetError();
            }
        } catch (MalformedURLException e) {
            error = "Controller MalformedURLException " + e.getMessage();
        } catch (JSONException e) {
            error = "Controller JSONException " + e.getMessage();
        }
        return false;
    }

    protected abstract void addItem (JSONObject jsonObject) throws JSONException;
    public boolean isError () { return  isErrorFlag; }
}
