package jp.co.akiguchilab.healthcaremanagement.util;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseUserInfoFromJSON {
    private static final String TAG = ParseUserInfoFromJSON.class.getSimpleName();

    public boolean isVaild = true;
    public int id = 0;
    public int pHour = 0;
    public int pLong = 0;
    public String fullname = "";
    public String number = "";
    private String name = "";
    private String last = "";

    public ParseUserInfoFromJSON() {
    }

    public boolean Get(String text) {
        isVaild = true;
        JSONObject json = null;
        try {
            json = new JSONObject(text);
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            id = json.getInt("id");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            number = json.getString("number");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            name = json.getString("name");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            last = json.getString("last");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            pHour = json.getInt("phour");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        try {
            pLong = json.getInt("plong");
        } catch (JSONException e) {
            isVaild = false;
            e.printStackTrace();
        }
        fullname = String.format("%s %s", name, last);

        return isVaild;
    }
}
