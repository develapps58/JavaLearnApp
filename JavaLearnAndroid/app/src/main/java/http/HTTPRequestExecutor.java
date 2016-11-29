package http;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import core.KeyValuePair;
import core.Settings;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class HTTPRequestExecutor extends AsyncTask<String, Void, Boolean> {
    String responseString, errorString;

    HttpURLConnection connection;
    ArrayList<KeyValuePair> params;
    HTTPRequestExecutor(HttpURLConnection connection, ArrayList<KeyValuePair> params, ArrayList<KeyValuePair> headers) {
        this.connection = connection;
        this.params = params;
        for(int i = 0; i < headers.size(); i++) {
            connection.setRequestProperty(headers.get(i).getKey().toString(), headers.get(i).getValue().toString());
        }
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
    }

    public String GetResponse ()
    {
        return responseString;
    }

    public String GetError ()
    {
        return  errorString;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            if(this.params.size() > 0 && connection.getRequestMethod() == "POST") {
                OutputStream stream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
                writer.write(getParamsAsJson(this.params));
                writer.flush();
                writer.close();
                stream.close();
            }

            connection.connect();
            int status = connection.getResponseCode();
            if(!(status == 200 || status == 201 || status == 204)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder builder = new StringBuilder();
                String output;
                while ((output = reader.readLine()) != null) {
                    builder.append(output);
                }
                errorString = builder.toString();
                reader.close();
                return false;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = reader.readLine()) != null) {
                builder.append(output);
            }
            responseString = builder.toString();
            reader.close();
            return true;
        } catch (UnknownHostException e) {
            errorString = "no_inet";
        } catch (IOException e) {
            errorString = "HTTPRequestExecutor IOException: " + e.getMessage();
        } catch (JSONException e) {
            errorString = "HTTPRequestExecutor JSONException: " + e.getMessage();
        }
        return false;
    }

    private String getParamsAsJson (ArrayList<KeyValuePair> params) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for(int i = 0; i < params.size(); i++)
            jsonObject.put(params.get(i).getKey().toString(), params.get(i).getValue());

        return jsonObject.toString();
    }

    private String getPostQuery(ArrayList<KeyValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (KeyValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey().toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }
}
