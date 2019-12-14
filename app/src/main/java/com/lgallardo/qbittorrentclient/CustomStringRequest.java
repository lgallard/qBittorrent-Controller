package com.lgallardo.qbittorrentclient;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by lgallard on 2/23/18.
 */

public class CustomStringRequest extends com.android.volley.toolbox.StringRequest {

    Map<String, String> headers;
    String token;

    /**
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     */
    public CustomStringRequest(int method, String url, Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.headers = null;

    }

    /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        //MyApp.get().checkSessionCookie(response.headers);

        this.headers = response.headers;

        token = response.headers.get("set-cookie").split(";")[0];

//        Log.d("Debug", "Response headers: " + response.headers);


        //return super.parseNetworkResponse(response);

        String data = "";

        try {
            data = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String jsonstring = "";

        jsonstring = "{\"data\": \"" + data + "\",\"headers\":\"" + response.headers.toString() + "\"}";
        return Response.success(jsonstring, HttpHeaderParser.parseCacheHeaders(response));
    }

    /* (non-Javadoc)
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = Collections.emptyMap();
        headers.put("set-cookie", token);
        return headers;
    }

}