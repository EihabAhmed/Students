package com.bbk.students.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bbk.students.ui.BaseActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentsApi {
    private final BaseActivity activity;
    private final String TAG;
    private String requestMethod;
    private String urlString;
    private String body;
    private Map<String, String> requestProperties;
    private final Response response;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public StudentsApi(BaseActivity activity, String TAG) {
        this.activity = activity;
        this.TAG = TAG;

        response = new Response();
    }

    public void request(String requestMethod, String urlString, Map<String, String> requestProperties, String body) {
        this.requestMethod = requestMethod;
        this.urlString = urlString;
        this.requestProperties = requestProperties;
        this.body = body;
        //new ApiExec().execute();
        executeRequest();
    }

    private void executeRequest() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                // Do background work here
                HttpURLConnection urlConnect = null;
                try {
                    URL url = new URL(urlString);
                    urlConnect = (HttpURLConnection) (url.openConnection());
                    urlConnect.setConnectTimeout(7000);

                    urlConnect.setRequestMethod(requestMethod);

                    if (requestProperties != null) {
                        for (String key : requestProperties.keySet()) {
                            urlConnect.setRequestProperty(key, requestProperties.get(key));
                        }
                    }

                    urlConnect.setDoInput(true);

                    if (body != null) {
                        urlConnect.setDoOutput(true);
                        String jsonRequestBody = body;
                        DataOutputStream os = new DataOutputStream(urlConnect.getOutputStream());
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                        writer.write(jsonRequestBody);
                        writer.close();
                        os.close();
                    }

                    response.responseCode = urlConnect.getResponseCode(); // Actually call the API service
                    response.responseMessage = urlConnect.getResponseMessage();
                    response.responseBody = convertStreamToString(urlConnect.getInputStream());
                } catch(Exception ex) {
                    Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
                    response.responseBody = convertStreamToString(urlConnect.getErrorStream());
                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            // Do UI thread work here
                            activity.receiveResponse(response);
                        }
                    });
                }
            }
        });
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferReader;
        String line;
        StringBuilder allString = new StringBuilder();

        try {
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            do {
                line = bufferReader.readLine();
                if (line != null) {
                    allString.append(line);
                }
            } while (line != null);

            inputStream.close();
            bufferReader.close();
        } catch (Exception e) {
            Log.d(TAG, "convertStreamToString: " + e.getLocalizedMessage());
        }

        return allString.toString();
    }

    public static class Response {
        public int responseCode = 0;
        public String responseMessage = "";
        public String responseBody = "";
    }
}
