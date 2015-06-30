/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JSONParser {
    private static final int TIMEOUT_ERROR = 1;
    static InputStream is = null;
    private JSONObject jObj = null;
    private JSONArray jArray = null;
    private String json = "";
    private String hostname;
    private String subfolder;
    private int port;
    private String protocol;
    private String username;
    private String password;
    private int connection_timeout;
    private int data_timeout;
    private String cookie;

    // constructor
    public JSONParser() {
        this("", "", "", 0, "", "", 10, 20);
    }

    public JSONParser(String hostname, String subfolder, int port, String username, String password) {
        this(hostname, subfolder, "http", port, username, password, 10, 20);
    }

    public JSONParser(String hostname, String subfolder, String protocol, int port, String username, String password, int connection_timeout, int data_timeout) {

        this.hostname = hostname;
        this.subfolder = subfolder;
        this.protocol = protocol;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connection_timeout = connection_timeout;
        this.data_timeout = data_timeout;

    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public JSONObject getJSONFromUrl(String url) throws JSONParserStatusCodeException {

        // if server is publish in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        HttpParams httpParameters = new BasicHttpParams();

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = connection_timeout * 1000;

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = data_timeout * 1000;

        // Set http parameters
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setUserAgent(httpParameters,"qBittorrent for Android");
        HttpProtocolParams.setVersion(httpParameters,HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters,HTTP.UTF_8);

        // Making HTTP request
        HttpHost targetHost = new HttpHost(this.hostname, this.port, this.protocol);

        // httpclient = new DefaultHttpClient(httpParameters);
        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);


        try {

            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);

            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

            // set http parameters

            url = protocol + "://" + hostname + ":" + port +"/" +url;

            HttpGet httpget = new HttpGet(url);

            if (this.cookie != null) {
                httpget.setHeader("Cookie", this.cookie);
            }

            httpResponse = httpclient.execute(targetHost, httpget);

            StatusLine statusLine = httpResponse.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            if (mStatusCode != 200) {
                httpclient.getConnectionManager().shutdown();
                throw new JSONParserStatusCodeException(mStatusCode);
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            // Build JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

            // try parse the string to a JSON object
            jObj = new JSONObject(json);

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("JSON", "UnsupportedEncodingException: " + e.toString());

        } catch (ClientProtocolException e) {
            Log.e("JSON", "ClientProtocolException: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("JSON", "IOException: " + e.toString());
            // e.printStackTrace();
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(TIMEOUT_ERROR);
        } catch (JSONParserStatusCodeException e) {
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(e.getCode());
        } catch (Exception e) {
            Log.e("JSON", "Generic: " + e.toString());
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        // return JSON String
        return jObj;
    }

    public JSONArray getJSONArrayFromUrl(String url) throws JSONParserStatusCodeException {

        // if server is published in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        HttpParams httpParameters = new BasicHttpParams();

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = connection_timeout * 1000;

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = data_timeout * 1000;

        // Set http parameters
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setUserAgent(httpParameters,"qBittorrent for Android");
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);

        // Making HTTP request
        HttpHost targetHost = new HttpHost(hostname, port, protocol);

        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);

        try {

            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

            url = protocol + "://" + hostname + ":" + port +"/" +url;

            HttpGet httpget = new HttpGet(url);

            if (this.cookie != null) {
                httpget.setHeader("Cookie", this.cookie);
            }

            httpResponse = httpclient.execute(targetHost, httpget);

            StatusLine statusLine = httpResponse.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            if (mStatusCode != 200) {
                httpclient.getConnectionManager().shutdown();
                throw new JSONParserStatusCodeException(mStatusCode);
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            // Build JSON

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

            jArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (UnsupportedEncodingException e) {
        } catch (ClientProtocolException e) {
            Log.e("JSON", "Client: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("JSON", "IO: " + e.toString());
            // e.printStackTrace();
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(TIMEOUT_ERROR);
        } catch (JSONParserStatusCodeException e) {
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(e.getCode());
        } catch (Exception e) {
            Log.e("JSON", "Generic: " + e.toString());
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources

            httpclient.getConnectionManager().shutdown();
        }

        // return JSON String
        return jArray;
    }

    public void postCommand(String command, String hash) throws JSONParserStatusCodeException {

        String key = "hash";

        String urlContentType = "application/x-www-form-urlencoded";

        String limit = "";
        String boundary = null;


        StringBuilder fileContent = null;

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        String url = "";

        if ("start".equals(command) || "startSelected".equals(command)) {
            url = "command/resume";
        }

        if ("pause".equals(command) || "pauseSelected".equals(command)) {
            url = "command/pause";
        }

        if ("delete".equals(command) || "deleteSelected".equals(command)) {
            url = "command/delete";
            key = "hashes";
        }

        if ("deleteDrive".equals(command) || "deleteDriveSelected".equals(command)) {
            url = "command/deletePerm";
            key = "hashes";
        }

        if ("addTorrent".equals(command)) {
            url = "command/download";
            key = "urls";
        }

        if ("addTorrentFile".equals(command)) {
            url = "command/upload";
            key = "urls";

            boundary = "-----------------------" + (new Date()).getTime();

            urlContentType = "multipart/form-data; boundary=" + boundary;

        }

        if ("pauseall".equals(command)) {
            url = "command/pauseall";
        }

        if ("pauseAll".equals(command)) {
            url = "command/pauseAll";
        }


        if ("resumeall".equals(command)) {
            url = "command/resumeall";
        }

        if ("resumeAll".equals(command)) {
            url = "command/resumeAll";
        }

        if ("increasePrio".equals(command)) {
            url = "command/increasePrio";
            key = "hashes";
        }

        if ("decreasePrio".equals(command)) {
            url = "command/decreasePrio";
            key = "hashes";

        }

        if ("maxPrio".equals(command)) {
            url = "command/topPrio";
            key = "hashes";
        }

        if ("minPrio".equals(command)) {
            url = "command/bottomPrio";
            key = "hashes";

        }

        if ("setQBittorrentPrefefrences".equals(command)) {
            url = "command/setPreferences";
            key = "json";
        }

        if ("setUploadRateLimit".equals(command)) {
            url = "command/setTorrentUpLimit";

            String[] tmpString = hash.split("&");
            hash = tmpString[0];
            limit = tmpString[1];
        }

        if ("setDownloadRateLimit".equals(command)) {
            url = "command/setTorrentDlLimit";

            String[] tmpString = hash.split("&");
            hash = tmpString[0];
            limit = tmpString[1];
        }

        if ("recheckSelected".equals(command)) {
            url = "command/recheck";
        }

        if ("toggleFirstLastPiecePrio".equals(command)) {
            url = "command/toggleFirstLastPiecePrio";
            key = "hashes";

        }

        if ("toggleSequentialDownload".equals(command)) {
            url = "command/toggleSequentialDownload";
            key = "hashes";

        }


        // if server is publish in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        HttpParams httpParameters = new BasicHttpParams();

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = connection_timeout * 1000;

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = data_timeout * 1000;

        // Set http parameters
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setUserAgent(httpParameters,"qBittorrent for Android");
        HttpProtocolParams.setVersion(httpParameters,HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters,HTTP.UTF_8);


        // Making HTTP request
        HttpHost targetHost = new HttpHost(this.hostname, this.port, this.protocol);

        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);

        try {

            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());

            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);

            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

            url = protocol + "://" + hostname + ":" + port +"/" +url;

            HttpPost httpget = new HttpPost(url);

            if ("addTorrent".equals(command)) {
                URI hash_uri = new URI(hash);
                hash = hash_uri.toString();
            }

            // In order to pass the has we must set the pair name value
            BasicNameValuePair bnvp = new BasicNameValuePair(key, hash);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(bnvp);

            // Add limit
            if (!limit.equals("")) {
                nvps.add(new BasicNameValuePair("limit", limit));

            }

            httpget.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            // Set content type and urls
            if ("addTorrent".equals(command) || "increasePrio".equals(command) || "decreasePrio".equals(command) || "maxPrio".equals(command)) {
                httpget.setHeader("Content-Type", urlContentType);

            }

            // Set cookie
            if (this.cookie != null) {
                httpget.setHeader("Cookie", this.cookie);
            }


            // Set content type and urls
            if ("addTorrentFile".equals(command)) {

                httpget.setHeader("Content-Type", urlContentType);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                // Add boundary
                builder.setBoundary(boundary);

                // Add text expected by qBittorrent server
                // builder.addTextBody("text",
                // "Content-Disposition: form-data; name=\"torrents\"; filename=\""
                // + hash + "\"\n"
                // + "Content-Type: application/x-bittorrent\n\n");

                // Add torrent file as binary
                File file = new File(hash);
                // FileBody fileBody = new FileBody(file);
                // builder.addPart("file", fileBody);

                builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY, hash);
//                builder.addBinaryBody("upfile", file, ContentType.create(urlContentType), hash);

                // Build entity
                HttpEntity entity = builder.build();

                // Set entity to http post
                httpget.setEntity(entity);

            }

            httpResponse = httpclient.execute(targetHost, httpget);

            StatusLine statusLine = httpResponse.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            if (mStatusCode != 200) {
                httpclient.getConnectionManager().shutdown();
                throw new JSONParserStatusCodeException(mStatusCode);
            }

            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();


        } catch (UnsupportedEncodingException e) {

        } catch (ClientProtocolException e) {
            Log.e("Debug", "Client: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Debug", "IO: " + e.toString());
            // e.printStackTrace();
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(TIMEOUT_ERROR);
        } catch (JSONParserStatusCodeException e) {
            httpclient.getConnectionManager().shutdown();
            throw new JSONParserStatusCodeException(e.getCode());
        } catch (Exception e) {
            Log.e("Debug", "Generic: " + e.toString());
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

    }

    // https

    public DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    // Cookies
    public String getNewCookie() throws JSONParserStatusCodeException {


        String url = "login";

        // if server is publish in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        String cookieString = null;

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        HttpParams httpParameters = new BasicHttpParams();

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = connection_timeout * 1000;

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = data_timeout * 1000;

        // Set http parameters
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setUserAgent(httpParameters, "qBittorrent for Android");
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);

        // Making HTTP request
        HttpHost targetHost = new HttpHost(hostname, port, protocol);

        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        try {

//            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
//
//            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);
//
//            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);



            url = protocol + "://" + hostname + ":" + port + "/" +url;

            HttpPost httpget = new HttpPost(url);

//            // In order to pass the username and password we must set the pair name value

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();


            nvps.add(new BasicNameValuePair("username", this.username));
            nvps.add(new BasicNameValuePair("password", this.password));

            httpget.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));


            HttpResponse response = httpclient.execute(targetHost, httpget);
            HttpEntity entity = response.getEntity( );

            StatusLine statusLine = response.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            if (mStatusCode == 200) {

                // Save cookie
                List<Cookie> cookies = httpclient.getCookieStore().getCookies();

                if (!cookies.isEmpty()) {
                    cookieString = cookies.get(0).getName() + "=" + cookies.get(0).getValue() + "; domain=" + cookies.get(0).getDomain();
                    cookieString = cookies.get(0).getName() + "=" + cookies.get(0).getValue();
                }

            }

            if (entity != null) {
                entity.consumeContent();
            }


            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();

        } catch (Exception e) {

            Log.e("Debug", "Exception " + e.toString());
        }

        if (cookieString == null) {
            cookieString = "";
        }
        return cookieString;


    }

    public String getApiVersion() throws JSONParserStatusCodeException {


                String url = "/version/api";

        // if server is publish in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        String APIVersionString = null;

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        HttpParams httpParameters = new BasicHttpParams();

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = connection_timeout * 1000;

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = data_timeout * 1000;

        // Set http parameters
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setUserAgent(httpParameters,"qBittorrent for Android");
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);

        // Making HTTP request
        HttpHost targetHost = new HttpHost(hostname, port, protocol);

        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        try {

            url = protocol + "://" + hostname + ":" + port +"/" +url;

            HttpGet httpget = new HttpGet(url);

            HttpResponse response = httpclient.execute(targetHost, httpget);
            HttpEntity entity = response.getEntity();

            StatusLine statusLine = response.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            Log.d("Debug", "API/Version - mStatusCode: " + mStatusCode );

            if (mStatusCode == 200) {

                // Save API

                APIVersionString = EntityUtils.toString(response.getEntity());

                Log.d("Debug", "API/Version - APIVersionString: " + APIVersionString );


            }


            if (entity != null) {
                entity.consumeContent();
            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();

        } catch (Exception e) {

//            Log.i("APIVer", "Exception " + e.toString());
        }

        if (APIVersionString == null) {
            APIVersionString = "";
        }
        return APIVersionString;
    }


}
