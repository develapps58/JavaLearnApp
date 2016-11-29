package core;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class CurrentUser {
    private String id, fullname, login, password;
    private static CurrentUser currentUser = null;
    private CurrentUser() {}
    public static CurrentUser getCurrentUser () {
        return currentUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {this.fullname = fullname;}

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    private static String error;
    public static String getError () {
        return error;
    }

    public static CurrentUser setCurrentUser (String login, String password) {
        currentUser = new CurrentUser();
        currentUser.login = login;
        currentUser.password = password;
        return currentUser;
    }

    public static void clearCurrentUser () {
        currentUser = null;
    }

    public static String getDefaultFullname () {
        return "Гость";
    }

    public static String getDefaultLogin () {
        return "guest";
    }

    public static String getDefaultPassword () {
        return "guest";
    }

    public boolean saveLocalSettings () {
        if(currentUser == null) return false;
        SharedPreferences sharedPreferences = Settings.context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.putString("full_name", fullname);
        editor.putString("login", login);
        editor.putString("password", password);
        return editor.commit();
    }

    public static boolean loadLocalSettings () {
        SharedPreferences sharedPreferences = Settings.context.getSharedPreferences("UserInfo", 0);
        String _id = sharedPreferences.getString("id", "");
        String _fullname = sharedPreferences.getString("full_name", "");
        String _login = sharedPreferences.getString("login", "");
        String _password = sharedPreferences.getString("password", "");

        if(_id.equals("") || _fullname.equals("") || _login.equals("") || _password.equals("")) {
            return false;
        }
        UserOperations operation = new UserOperations();
        operation.setLogin(_login);
        operation.setPassword(_password);
        boolean result = operation.login();
        if(!result) error = operation.getError();
        return result;
    }

    public static void logout () {
        SharedPreferences sharedPreferences = Settings.context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", "");
        editor.putString("full_name", "");
        editor.putString("login", "");
        editor.putString("password", "");
        editor.commit();
        currentUser = null;
    }
}
