package org.smartregister.tbr.service;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.ssl.OpensrpSSLHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import static org.smartregister.domain.LoginResponse.MALFORMED_URL;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.TIMEOUT;

public class HttpAgentExtended {

    public static final int CONNECTION_TIMEOUT = 60000;
    private static final int READ_TIMEOUT = 60000;
    private static final String TAG = HTTPAgent.class.getCanonicalName();
    private Context context;
    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;

    private String boundary = "***" + System.currentTimeMillis() + "***";
    private String twoHyphens = "--";
    private String crlf = "\r\n";

    public HttpAgentExtended(Context context, AllSettings settings, AllSharedPreferences
            allSharedPreferences, DristhiConfiguration configuration) {
        this.context = context;
        this.settings = settings;
        this.allSharedPreferences = allSharedPreferences;
        this.configuration = configuration;
    }

    public Response<String> post(String postURLPath, String jsonPayload) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(postURLPath, true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonPayload);
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Log.e(TAG, "EXCEPTION" + ex.toString(), ex);
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    private HttpURLConnection initializeHttp(String requestURLPath, boolean useBasicAuth) throws IOException {
        URL url = new URL(requestURLPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        /*if (urlConnection instanceof HttpsURLConnection) {
            OpensrpSSLHelper opensrpSSLHelper = new OpensrpSSLHelper(context, configuration);
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(opensrpSSLHelper.);
        }*/
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);

        if(useBasicAuth) {
            final String basicAuth = "Basic " + Base64.encodeToString(("demo" +
                    ":" + "Admin123").getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }
        return urlConnection;
    }


    private Response<String> handleResponse(HttpURLConnection urlConnection) {
        String responseString;
        try {
            int statusCode = urlConnection.getResponseCode();

            InputStream inputStream;
            if (statusCode >= HttpStatus.SC_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            responseString = IOUtils.toString(inputStream);

        } catch (MalformedURLException e) {
            Log.e(TAG, MALFORMED_URL + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, TIMEOUT + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } catch (IOException e) {
            Log.e(TAG, NO_INTERNET_CONNECTIVITY + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new Response<>(ResponseStatus.success, responseString);
    }



}
