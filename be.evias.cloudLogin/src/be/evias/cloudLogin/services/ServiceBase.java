package be.evias.cloudLogin.services;

/**
 * LICENSE
 *
 Copyright 2015 Grégory Saive (greg@evias.be)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
**/

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
    public static class cloudLoginServiceError
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
