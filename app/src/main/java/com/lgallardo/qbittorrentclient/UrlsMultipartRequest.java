package com.lgallardo.qbittorrentclient;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Date;
import java.util.Map;

/**
 * Created by lgallard on 15/01/2020.
 * Based on https://stackoverflow.com/a/38238994
 */

public class UrlsMultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    final String boundary = "-----------------------" + (new Date()).getTime();
    final String urlContentType = "multipart/form-data; boundary=" + boundary;

    private String createPostBody(Map<String, String> params) {
        StringBuilder sbPost = new StringBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    sbPost.append("--" + boundary + "\r\n");
                    sbPost.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n");
                    sbPost.append(params.get(key).toString());
                }
            }
        }

//        Log.d("Debug", "[UrlsMultipartRequest] Body: " + sbPost.toString());
        return sbPost.toString();
    }


    public UrlsMultipartRequest(String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

//    @Override
//    protected Map<String, String> getParams() throws AuthFailureError {
//        return super.getParams();
//    }

    @Override
    public String getBodyContentType() {
        return urlContentType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return createPostBody(getParams()).getBytes();
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }
}