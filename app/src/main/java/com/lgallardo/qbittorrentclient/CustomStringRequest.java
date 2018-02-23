package com.lgallardo.qbittorrentclient;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lgallard on 2/23/18.
 */

public class CustomStringRequest extends com.android.volley.toolbox.StringRequest {

    protected static Map<String, String> headers;

    /**
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     */
    public CustomStringRequest(int method, String url, Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        CustomStringRequest.headers = null;

    }

    /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        //MyApp.get().checkSessionCookie(response.headers);

        CustomStringRequest.headers = response.headers;

        Log.d("Debug", "Response headers: " + response.headers);

        return super.parseNetworkResponse(response);
    }

    /* (non-Javadoc)
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        //MyApp.get().addSessionCookie(headers);

        return headers;
    }

}