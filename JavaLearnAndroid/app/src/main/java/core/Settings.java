package core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.net.InetAddress;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class Settings {
    private Settings () {}
    public static String getDomain () {
        return "http://pi3.duckdns.org/";
    }
    public static Context context = null;
    public static boolean existUpdate = false;
    public static boolean isOnline = false;
    public static boolean isLoaded = false;
}
