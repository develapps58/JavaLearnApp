package core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import http.HTTPRequest;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class UserOperations {
    String full_name, login, password;
    String error;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getError() {
        return error;
    }

    public boolean registration () {

        if(full_name.isEmpty()) {
            error = "Заполните Ваше Имя";
            return false;
        }
        if(login.isEmpty()) {
            error = "Заполните Ваш Логин";
            return false;
        }
        if(password.isEmpty()) {
            error = "Заполните Ваш пароль";
            return false;
        }

        ArrayList<KeyValuePair> params = new ArrayList<KeyValuePair>();
        params.add(new KeyValuePair("full_name", full_name));
        params.add(new KeyValuePair("login", login));
        params.add(new KeyValuePair("password", password));
        params.add(new KeyValuePair("issystem", false));

        try {
            HTTPRequest request = new HTTPRequest(new URL(Settings.getDomain() + "users"), params);
            if(request.Post()) {
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

    public boolean login () {
        if(login.isEmpty()) {
            error = "Заполните Ваш Логин";
            return false;
        }
        if(password.isEmpty()) {
            error = "Заполните Ваш пароль";
            return false;
        }
        try {
            CurrentUser.setCurrentUser(login, password);
            HTTPRequest request = new HTTPRequest(new URL(Settings.getDomain() + "users?where={\"login\":\""+login+"\"}"));
            if(request.Get()) {
                JSONObject jsonObject = request.GetResponseResultAsJSON();
                JSONArray jArray = (JSONArray)jsonObject.get("_items");
                if(jArray.length() != 1) {
                    error = "Wrong response";
                    return false;
                }
                CurrentUser currentUser = CurrentUser.getCurrentUser();
                currentUser.setId(jArray.getJSONObject(0).getString("_id"));
                currentUser.setFullname(jArray.getJSONObject(0).getString("full_name"));
                currentUser.saveLocalSettings();

                return true;
            }
            else {
                error = request.GetError();
                CurrentUser.clearCurrentUser();
                return false;
            }
        } catch (MalformedURLException e) {
            error = e.getMessage();
        } catch (JSONException e) {
            error = e.getMessage();
        }
        return false;
    }

}
