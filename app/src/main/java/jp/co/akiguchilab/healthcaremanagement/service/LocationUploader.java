package jp.co.akiguchilab.healthcaremanagement.service;

import android.content.Context;
import android.os.Handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

class RetrySession {
    long at = 0;
    int number = 0;
    int walkcount = 0;

    public RetrySession(int walkcount) {
        this.walkcount = walkcount;
        this.at = System.currentTimeMillis();
    }
}

public class LocationUploader {
    Context context = null;
    int whoami = 0;
    String uploadURL = "";
    int uploadNumber = 0;

    LinkedHashMap<Integer, RetrySession> remain = null;

    long serviceStartMS = 0;

    Handler handler = null;

    ConnectionManager connectionManager = null;
    Semaphore semaphore = null;

    public LocationUploader(Context context, int whoami) {
        this.serviceStartMS = System.currentTimeMillis();

        this.context = context;
        this.whoami = whoami;

        remain = new LinkedHashMap<Integer, RetrySession>();
        connectionManager = new ConnectionManager(context);

        handler = new Handler();
        retryThis();

        semaphore = new Semaphore(1);
    }

    public int remainCount() {
        return remain.size();
    }

    public void retryThis() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int many = remain.size();
                if (many > 0) {
                    long now = System.currentTimeMillis();
                    int index = 0;
                    for (Map.Entry<Integer, RetrySession> entry : remain.entrySet()) {
                        RetrySession session = entry.getValue();
                        if ((now - session.at) > 1000 * 60 * 1) {
                            int walkcount = session.walkcount;
                            int number = entry.getKey();
                            uploadThis(number, walkcount);
                        }
                        index++;
                    }
                }
                handler.postDelayed(this, 1000 * 60 * 1);
            }
        };
        handler.postDelayed(runnable, 1000 * 60 * 1);
    }

    public void uploadThis(int number, int walkcount) {
        final RetrySession session = new RetrySession(walkcount);
        try {
            semaphore.acquire();
            remain.put(number, session);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (connectionManager.isConnecting()) {
            String baseURL = "http://hsys.klabo.co.jp/postit/mlog.php";
            String[] sa = {
                    String.format("who=%d", whoami),
            };
            String params = StringUtils.join(sa, "&");

            uploadURL = String.format("%s?%s", baseURL, params);
            uploadNumber = number;

            new Thread(new Runnable() {
                String thisURL = uploadURL;
                int thisKey = uploadNumber;

                @Override
                public void run() {
                    HttpClient httpClient = new DefaultHttpClient();
                    boolean success = false;
                    HttpGet method = new HttpGet(thisURL);

                    try {
                        HttpResponse res = httpClient.execute(method);
                        int status = res.getStatusLine().getStatusCode();
                        if (status == HttpStatus.SC_OK) {
                            success = true;
                            String source = EntityUtils.toString(res.getEntity(), "UTF-8");
                        }
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (success) {
                        try {
                            semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        remain.remove(thisKey);
                        semaphore.release();
                    }
                }
            }).start();
        }
    }
}