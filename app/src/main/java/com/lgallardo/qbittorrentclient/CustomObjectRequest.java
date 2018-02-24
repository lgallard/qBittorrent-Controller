package com.lgallardo.qbittorrentclient;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by lgallard on 2/23/18.
 */

public class CustomObjectRequest extends com.android.volley.toolbox.JsonObjectRequest {

    Map<String, String> headers;
    String token;

    /**
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     */
    public CustomObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {

        super(method, url, jsonRequest, listener, errorListener);
        this.headers = null;

    }

    /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        //MyApp.get().checkSessionCookie(response.headers);

        this.headers = response.headers;


        Log.d("Debug", "Response headers: " + response.headers);

        //return super.parseNetworkResponse(response);

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));


            Log.d("Debug", "jsonString: " + jsonString);


            JSONObject jsonResponse = new JSONObject(jsonString);
            jsonResponse.put("headers", new JSONObject(response.headers));
            return Response.success(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
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