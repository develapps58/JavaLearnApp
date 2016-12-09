package core;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class CurrentUser {
    private String fullname, token;
    private static CurrentUser currentUser = null;
    private CurrentUser() {
    }
    public static CurrentUser getCurrentUser () {
        return currentUser;
    }
    public static void setCurrentUser (String fullname, String token) {
        currentUser = new CurrentUser();
        currentUser.fullname = fullname;
        currentUser.token = token;
    }
}
