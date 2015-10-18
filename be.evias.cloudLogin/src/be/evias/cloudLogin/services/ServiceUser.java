package be.evias.cloudLogin.services;

import java.lang.reflect.Type;
import java.io.Serializable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import android.content.Context;
import android.util.Log;

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

import be.evias.cloudLogin.models.User;
import be.evias.cloudLogin.models.ArrayOfInt;
import be.evias.cloudLogin.models.SimpleResult;

import be.evias.cloudLogin.R;

public class ServiceUser
	extends ServiceBase
{
    /*
    public static User loadByLogin(String login)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "http://luxbranded.com/android/getbylogin";

        String query = null;
        try {
            query = String.format("format=json&api_key=%s&identifier=%s",
                            "c611c52d7054086f207ffad27f3927cd",
                            URLEncoder.encode(login, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += "?" + query;

        HttpGet httpGet  = new HttpGet(url);
        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            User returnUser       = new Gson().fromJson(responseString, User.class);
            return returnUser;
        }
        catch (IOException e) {}

        return null;
    }

    public Boolean sendLocation(Context context, int userId, UserLocation location)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "http://luxbranded.com/android/location";

        DecimalFormat df = new DecimalFormat("0.000000");
        String query = null;
        query = String.format("format=json&api_key=%s&uid=%d&lat=%s&lng=%s",
                        "c611c52d7054086f207ffad27f3927cd",
                        userId,
                        Double.valueOf(df.format(location.getLatitude())).toString(),
                        Double.valueOf(df.format(location.getLongitude())).toString());
        url  += "?" + query;

        HttpGet      httpGet  = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());

            Log.d("luxbranded", "ServiceUser/sendLocation responseString: '" + responseString + "'");

            if (response.getStatusLine().getStatusCode() != 200) {
                ServiceBase.LuxBrandedServiceError error = new Gson().fromJson(responseString, ServiceBase.LuxBrandedServiceError.class);
                throw new IOException(context.getString(R.string.error_user_location));
            }

            SimpleResult obj = new Gson().fromJson(responseString, SimpleResult.class);
            return obj.getResult();
        }
        catch (IOException e) {
            Log.d("luxbranded", "ServiceUser/sendLocation ERROR : " + e.getMessage());
        }

        return false;
    }

    public List<UserLocation> getUserPositions(Context context, User user)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "http://luxbranded.com/android/positions";

        String query = null;
        query = String.format("format=json&api_key=%s&uid=%d", "c611c52d7054086f207ffad27f3927cd", user.getId());
        url  += "?" + query;

        HttpGet      httpGet  = new HttpGet(url);
        int[]        idsList  = {};
        List<UserLocation> posList  = new ArrayList<UserLocation>();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());

            Log.d("luxbranded", "ServiceUser/getUserPositions responseString: '" + responseString + "'");

            if (response.getStatusLine().getStatusCode() != 200) {
                ServiceBase.LuxBrandedServiceError error = new Gson().fromJson(responseString, ServiceBase.LuxBrandedServiceError.class);
                throw new IOException(context.getString(R.string.error_user_getpositions));
            }

            ArrayOfLocation obj  = new Gson().fromJson(responseString, ArrayOfLocation.class);
            UserLocation[] items = obj.getItems();
            for (int i = 0, m = items.length; i < m; i++)
                posList.add(items[i]);
        }
        catch (IOException e) {
            Log.d("luxbranded", "ServiceUser/getUserPositions ERROR : " + e.getMessage());
        }

        return posList;
    }
    */
}
