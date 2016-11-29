package models;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import controllers.UserAnswersController;
import core.CurrentUser;
import core.IController;
import core.KeyValuePair;
import core.Model;
import core.Settings;
import http.HTTPRequest;

/**
 * Created by Дмитрий on 24.11.2016.
 */

public class UserAnswers extends Model {
    private String answerid, etag;

    public String getAnswerid() {
        return answerid;
    }

    public void setAnswerid(String answerid) {
        this.answerid = answerid;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public IController getController() {
        return UserAnswersController.instance();
    }

    public boolean saveToGlobalStorage () {
        CurrentUser currentUser = CurrentUser.getCurrentUser();
        if(currentUser == null) {
            error = "Пользователь не зарегистрирован";
            return false;
        }
        ArrayList<KeyValuePair> params = new ArrayList<KeyValuePair>();
        params.add(new KeyValuePair("answerid", answerid));
        params.add(new KeyValuePair("userid", currentUser.getId()));
        try {
            HTTPRequest request = new HTTPRequest(new URL(Settings.getDomain() + "user_answers"), params);
            if(request.Post()) {
                JSONObject jsonObject = request.GetResponseResultAsJSON();
                id = jsonObject.getString("_id");
                etag = jsonObject.getString("_etag");
                return true;
            }
            else {
                error = request.GetError();
            }
        } catch (MalformedURLException e) {
            error = e.getMessage();
        } catch (JSONException e) {
            error = e.getMessage();
        }
        return false;
    }

    public boolean removeFromGlobalStorage () {
        if(id.isEmpty() || id.equals("")) {
            error = "Id not found";
            return false;
        }
        try {
            HTTPRequest request = new HTTPRequest(new URL(Settings.getDomain() + "user_answers/" + getId()));
            request.addHeader(new KeyValuePair("If-Match", etag));
            if(request.Delete()) {
                return true;
            }
            else {
                error = request.GetError();
            }
        } catch (MalformedURLException e) {
            error = e.getMessage();
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return answerid.equals(((UserAnswers)o).answerid);
    }
}
