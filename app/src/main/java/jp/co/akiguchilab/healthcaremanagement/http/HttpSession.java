package jp.co.akiguchilab.healthcaremanagement.http;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpSession extends AsyncTaskLoader<Res> {
    private static final String TAG = HttpSession.class.getSimpleName();

    private Bundle bundle = null;
    int type = 0;
    Res res = null;

    public HttpSession(Context context, Bundle bundle) {
        super(context);

        this.bundle = bundle;
        this.res = null;
    }

    @Override
    public Res loadInBackground() {
        boolean success = false;
        int status = 0;
        String text = "";
        URI url = null;
        int id;
        File file;
        int whoami;
        double lon;
        double lat;

        try {
            url = new URI(bundle.getString("url"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        id = bundle.getInt("id");
        type = bundle.getInt("type");
        file = (File) bundle.get("file");
        whoami = bundle.getInt("whoami");
        lon = bundle.getDouble("lon");
        lat = bundle.getDouble("lat");

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpResponse response = null;

        if (type == 1) {
            HttpGet request = new HttpGet(url);
            try {
                response = httpClient.execute(request);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HttpPost request = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            ContentType textContextType = ContentType.create("text/plaint", "UTF-8");
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //builder.setCharset(Charset.forName("UTF-8"));
            builder.addTextBody("user", String.valueOf(whoami), textContextType);
            builder.addBinaryBody("photo", file, ContentType.create("image/jpeg"), file.getName());
            builder.addTextBody("lon", String.valueOf(lon));
            builder.addTextBody("lat", String.valueOf(lat));

            request.setEntity(builder.build());
            try {
                response = httpClient.execute(request);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (response != null) {
            status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.SC_OK) {
                success = true;

                HttpEntity entity = response.getEntity();
                try {
                    text = EntityUtils.toString(entity);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        res = new Res(id, success, status, text, bundle);

        return res;
    }
}
