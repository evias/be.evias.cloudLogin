package be.evias.cloudLogin.services;

import java.io.Serializable;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import be.evias.cloudLogin.models.SimpleResult;

public class ServiceBase
{
    public static class LuxBrandedServiceError
        implements Serializable
    {
        int code;
        String error;
    }

    public Boolean ping(Context context)
    {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "http://evias.be/api/index/ping";
        url       += "?format=json&api_key=c611c52d7054086f207ffad27f3927cd";

        HttpGet httpGet = new HttpGet(url);
		SimpleResult result;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200)
            	return false;

            result = new Gson().fromJson(responseString, SimpleResult.class);
            return result.getResult();
        }
        catch (IOException e) {}

        return false;
    }

}
