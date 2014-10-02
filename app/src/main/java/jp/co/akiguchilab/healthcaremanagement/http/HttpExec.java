package jp.co.akiguchilab.healthcaremanagement.http;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;
import java.util.LinkedHashMap;

public class HttpExec implements LoaderCallbacks<Res> {
    private static final String TAG = HttpExec.class.getSimpleName();

    private Context context = null;
    private LoaderManager manager = null;
    private ConnectivityManager connectivityManager = null;
    private callBack cbPointer = null;

    public final int TYPE_GET = 1;
    public final int TYPE_POST = 2;

    LinkedHashMap<Integer, Bundle> remain = null;

    Handler handler = null;

    public HttpExec(Context context, LoaderManager manager, callBack cbPointer) {
        this.context = context;
        this.manager = manager;
        this.cbPointer = cbPointer;

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        remain = new LinkedHashMap<Integer, Bundle>();
        handler = new Handler();
    }

    public int getPOSTRemain() {
        return remain.size();
    }

    public boolean GET(int id, String url) {
        boolean result = false;
        Bundle bundle = new Bundle();
        bundle.putInt("type", TYPE_GET);
        bundle.putInt("id", id);
        bundle.putString("url", url);

        if (isConnecting()) {
            manager.restartLoader(TYPE_GET, bundle, this);
            result = true;
        }
        return result;
    }

    public boolean POST(int id, String url, File file, double lon, double lat, int whoami) {
        boolean result = false;
        Bundle bundle = new Bundle();
        bundle.putInt("type", TYPE_POST);
        bundle.putInt("id", id);
        bundle.putString("url", url);
        bundle.putInt("whoami", whoami);
        bundle.putSerializable("file", file);
        bundle.putDouble("lon", lon);
        bundle.putDouble("lat", lat);

        remain.put(id, bundle);

        if (isConnecting()) {
            manager.restartLoader(TYPE_POST, bundle, this);
            result = true;
        } else {
            prepareRetry(bundle);
        }
        return result;
    }

    private void prepareRetry(Bundle lastBundle) {
        final Bundle bundle = lastBundle;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = bundle.getInt("id");
                String url = bundle.getString("url");
                File file = (File) bundle.get("file");
                int whoami = bundle.getInt("whoami");
                double lon = bundle.getDouble("lon");
                double lat = bundle.getDouble("lat");

                POST(id, url, file, lon, lat, whoami);
            }
        }, 1000 * 30);
    }

    @Override
    public Loader<Res> onCreateLoader(int which, Bundle bundle) {
        Loader<Res> loader = new HttpSession(context, bundle);
        loader.forceLoad();

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Res> loader, Res res) {
        int type = res.bundle.getInt("type");

        switch (type) {
            case TYPE_GET:
                cbPointer.onFinished(res.id, res.success, res.status, res.text);
                break;
            case TYPE_POST:
                if (res.success) {
                    remain.remove(res.id);
                    cbPointer.onFinished(res.id, res.success, res.status, res.text);
                } else {
                    prepareRetry(res.bundle);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Res> arg0) {
    }

    public boolean isConnecting() {
        boolean result = false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                result = true;
            }
        }
        return result;
    }

    public interface callBack {
        public void onFinished(int id, boolean success, int status, String text);
    }

}
