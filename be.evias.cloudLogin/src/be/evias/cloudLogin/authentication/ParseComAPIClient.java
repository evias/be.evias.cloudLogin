package be.evias.cloudLogin.authentication;

import android.util.Log;
import android.content.Context;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import be.evias.cloudLogin.models.User;
import be.evias.cloudLogin.models.ArrayOfUser;

public class ParseComAPIClient
    implements AuthenticationInterface
{
    @Override
    public String userSignUp(Context context, String name, String email, String pass, String authType)
        throws Exception
    {
        Log.d("cloudLogin", "ParseComAPI : userSignUp");

        String url = "https://api.parse.com/1/users";

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("X-Parse-Application-Id","jWFphUivfFNK6ID56aP38yZ2CTj089S6sINXhJmm");
        httpPost.addHeader("X-Parse-REST-API-Key", "48ifErmRhD6udTPRe59sOkRFhbw1lJSMYfWG2VZz");
        httpPost.addHeader("Content-Type", "application/json");

        String token = UUID.randomUUID().toString();
        String user  = "{\"username\":\"" + email + "\",\"password\":\"" + pass + "\",\"email\":\"" + email + "\",\"name\":\"" + name + "\"}";
        HttpEntity entity = new StringEntity(user);
        httpPost.setEntity(entity);

        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 201) {
                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
                throw new Exception("Error creating user["+error.code+"] - " + error.error);
            }

            User createdUser = new Gson().fromJson(responseString, User.class);
            authtoken = createdUser.authData;

            SharedPreferences prefs     = context.getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("cloudlogin_active_account_name", createdUser.getName());
            ed.putString("cloudlogin_active_account_email", createdUser.getEmail());
            ed.commit();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return authtoken;
    }

    @Override
    public String userSignIn(Context context, String user, String pass, String authType)
        throws Exception
    {
        Log.d("cloudLogin", "ParseComAPI : userSignIn");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url   = "https://api.parse.com/1/login";
        String query      = "";
        try {
            query = String.format("username=%s&password=%s",
                            URLEncoder.encode(user, "UTF-8"),
                            URLEncoder.encode(pass, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += (query == "") ? "" : "?" + query;

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("X-Parse-Application-Id", "jWFphUivfFNK6ID56aP38yZ2CTj089S6sINXhJmm");
        httpGet.addHeader("X-Parse-REST-API-Key", "48ifErmRhD6udTPRe59sOkRFhbw1lJSMYfWG2VZz");

//        HttpParams params = new BasicHttpParams();
//        params.setParameter("username", user);
//        params.setParameter("password", pass);
//        httpGet.setParams(params);

        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
                throw new Exception("Error signing-in ["+error.code+"] - " + error.error);
            }

            User loggedUser = new Gson().fromJson(responseString, User.class);
            authtoken = loggedUser.authData;

            SharedPreferences prefs     = context.getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("cloudlogin_active_account_name", loggedUser.getName());
            ed.putString("cloudlogin_active_account_email", loggedUser.getEmail());
            ed.commit();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return authtoken;
    }

    @Override
    public String userSignOut(Context context, String user)
        throws Exception
    {
        return "NothingToDo";
    }

    @Override
    public User getUserObject(Context context, String name)
        throws Exception
    {
        Log.d("cloudLogin", "ParseComAPI : getUserObject");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url   = "https://api.parse.com/1/users";

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("X-Parse-Application-Id", "jWFphUivfFNK6ID56aP38yZ2CTj089S6sINXhJmm");
        httpGet.addHeader("X-Parse-REST-API-Key", "48ifErmRhD6udTPRe59sOkRFhbw1lJSMYfWG2VZz");

        String authtoken = null;
        List<User> usersList  = new ArrayList<User>();
        User u = new User();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());

            Log.d("cloudLogin", "ParseComAPI : getUserObject responseString: " + responseString);

            if (response.getStatusLine().getStatusCode() != 200) {
                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
                throw new Exception("Error signing-in ["+error.code+"] - " + error.error);
            }

            Log.d("cloudLogin", "ParseComAPI : getUserObject looking for: " + name);

            // iterate through users list to identify the needed row.
            ArrayOfUser obj = new Gson().fromJson(responseString, ArrayOfUser.class);
            User[] items    = obj.getResults();
            for (int i = 0, m = items.length; i < m; i++)
                if (items[i].getUsername().equals(name))
                    return items[i];

            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private class ParseComError
        implements Serializable
    {
        int code;
        String error;
    }
}
