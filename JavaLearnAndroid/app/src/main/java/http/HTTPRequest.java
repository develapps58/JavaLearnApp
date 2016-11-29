package http;


import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import core.CurrentUser;
import core.KeyValuePair;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class HTTPRequest {
    URL url;
    ArrayList<KeyValuePair> httpParams = new ArrayList<KeyValuePair>();
    ArrayList<KeyValuePair> httpHeaders = new ArrayList<KeyValuePair>();

    HttpURLConnection connection;

    String Error;
    String ResponseResult = "";

    public HTTPRequest(URL url)
    {
        this.url = url;
    }

    public HTTPRequest(URL url, ArrayList<KeyValuePair> httpParams) {
        this.url = url;
        this.httpParams = httpParams;
    }

    public String GetError ()
    {
        return Error;
    }

    public String GetResponseResult ()
    {
        return ResponseResult;
    }

    public JSONObject GetResponseResultAsJSON () throws JSONException {
        return new JSONObject(ResponseResult);
    }

    public void setParams (ArrayList<KeyValuePair> httpParams)
    {
        this.httpParams = httpParams;
    }

    public void addHeader (KeyValuePair httpHeader)
    {
        httpHeaders.add(httpHeader);
    }

    public void setHeaders (ArrayList<KeyValuePair> httpHeaders)
    {
        this.httpHeaders = httpHeaders;
    }

    //execute HTTP Get method
    public Boolean Get ()
    {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    CurrentUser user = CurrentUser.getCurrentUser();
                    if(user == null) {
                        return new PasswordAuthentication(CurrentUser.getDefaultLogin(), CurrentUser.getDefaultPassword().toCharArray());
                    } else {
                        return new PasswordAuthentication(user.getLogin(), user.getPassword().toCharArray());
                    }
                };
            });
            /*Uri.Builder builder = new Uri.Builder();
            for(int i = 0; i < httpParams.size(); i++) {
                builder.appendQueryParameter(httpParams.get(i).getKey().toString(), httpParams.get(i).getValue().toString());
            }*/
            connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            /*if(httpParams.size() > 0) {
                OutputStream stream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
                writer.write(builder.build().getEncodedQuery());
                writer.flush();
                writer.close();
                stream.close();
            }*/
            return execute();
        } catch (IOException e) {
            Error = e.getMessage();
            return false;
        }
    }
    //execute HTTP Post method
    public Boolean Post ()
    {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    CurrentUser user = CurrentUser.getCurrentUser();
                    if(user == null) {
                        return new PasswordAuthentication(CurrentUser.getDefaultLogin(), CurrentUser.getDefaultPassword().toCharArray());
                    } else {
                        return new PasswordAuthentication(user.getLogin(), user.getPassword().toCharArray());
                    }
                };
            });
            connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return execute();
        } catch (IOException e) {
            Error = e.getMessage();
            return false;
        }
    }
    //execute HTTP Put method
    public Boolean Put ()
    {
        return true;
    }
    //execute HTTP Patch method
    public Boolean Patch ()
    {
        return true;
    }
    //execute HTTP Delete method
    public Boolean Delete ()
    {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    CurrentUser user = CurrentUser.getCurrentUser();
                    if(user == null) {
                        return new PasswordAuthentication(CurrentUser.getDefaultLogin(), CurrentUser.getDefaultPassword().toCharArray());
                    } else {
                        return new PasswordAuthentication(user.getLogin(), user.getPassword().toCharArray());
                    }
                };
            });
            connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("DELETE");
            return execute();
        } catch (IOException e) {
            Error = e.getMessage();
            return false;
        }
    }

    private Boolean execute ()
    {
        HTTPRequestExecutor executor = new HTTPRequestExecutor(connection, httpParams, httpHeaders);
        try {
            if(executor.execute().get()) {
                ResponseResult = executor.GetResponse();
                return true;
            } else {
                Error = executor.GetError();
                return false;
            }
        } catch (InterruptedException e) {
            Error = e.getMessage();
        } catch (ExecutionException e) {
            Error = e.getMessage();
        }
        return false;
    }
}
