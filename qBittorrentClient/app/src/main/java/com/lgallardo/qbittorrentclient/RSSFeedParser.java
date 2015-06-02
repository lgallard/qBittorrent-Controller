package com.lgallardo.qbittorrentclient;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by lgallard on 02/06/15.
 */
public class RSSFeedParser {

    private int connection_timeout;
    private int data_timeout;

    static InputStream is = null;


    public void RSSFeedParser() {

        RSSFeedParser(10,20);

    }

    public void RSSFeedParser(int connection_timeout, int data_timeout) {

        this.connection_timeout = connection_timeout;
        this.data_timeout = data_timeout;

    }

    public XmlPullParser getRSSFeed(String url){

        HttpResponse httpResponse;
        DefaultHttpClient httpclient;

        XmlPullParserFactory xmlFactoryObject;
        XmlPullParser xmlParser = null;

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
        HttpHost targetHost = new HttpHost(url);

        // httpclient = new DefaultHttpClient(httpParameters);
        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);


        try {

//            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
//            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);
//
//            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

            // set http parameters

            HttpGet httpget = new HttpGet(url);

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

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlParser = xmlFactoryObject.newPullParser();

            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(is, null);


            is.close();
        } catch (Exception e) {
            Log.e("Debug", "RSSFeedParser - : " + e.toString());
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        // return JSON String
        return xmlParser;



    }


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
}
