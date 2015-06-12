package com.lgallardo.qbittorrentclient;

import android.net.Uri;
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

import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lgallard on 02/06/15.
 */
public class RSSFeedParser {

    private int connection_timeout;
    private int data_timeout;

    static InputStream is = null;

    private int itemCount;


    public RSSFeedParser() {

        RSSFeedParser(10, 20);

    }

    public void RSSFeedParser(int connection_timeout, int data_timeout) {

        this.connection_timeout = connection_timeout;
        this.data_timeout = data_timeout;

    }

    public RSSFeed getRSSFeed(String channelTitle, String channelUrl) {

        // Parse url
        Uri uri = Uri.parse(channelUrl);
        int event;
        String text = null;
        String torrent = null;
        boolean header = true;

        this.itemCount = 0;


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


        Log.d("Debug", "Host: " + uri.getAuthority());

        // Making HTTP request
        HttpHost targetHost = new HttpHost(uri.getAuthority());

        // httpclient = new DefaultHttpClient(httpParameters);
        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);


        RSSFeed rssFeed = new RSSFeed();

        try {

//            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
//            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);
//
//            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

            // set http parameters

            HttpGet httpget = new HttpGet(channelUrl);

            httpResponse = httpclient.execute(targetHost, httpget);

            StatusLine statusLine = httpResponse.getStatusLine();

            int mStatusCode = statusLine.getStatusCode();

            if (mStatusCode != 200) {
                httpclient.getConnectionManager().shutdown();
                throw new JSONParserStatusCodeException(mStatusCode);
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlParser = xmlFactoryObject.newPullParser();

            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(is, null);


            event = xmlParser.getEventType();

            // Get Channel info
            String name;
            RSSFeedItem item = null;
            List<RSSFeedItem> items = new ArrayList<RSSFeedItem>();

            // Get items
            while (event != XmlPullParser.END_DOCUMENT) {


                name = xmlParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:

                        if (name != null && name.equals("item")) {
                            header = false;
                            item = new RSSFeedItem();
                            itemCount = itemCount +1;
                        }


                        try {
                            torrent = xmlParser.getAttributeValue(0);
                        } catch (Exception e) {

                        }


                        break;

                    case XmlPullParser.TEXT:
                        text = xmlParser.getText();
                        break;

                    case XmlPullParser.END_TAG:


                        if (name.equals("title")) {
                            if (header) {
                                rssFeed.setChannelTitle(channelTitle);
                            } else {
                                item.setTitle(text);
                                Log.d("Debug", "Title: " + text);
                            }
                        } else if (name.equals("description")) {
                            if (header) {
                                Log.d("Debug", "Channel Description: " + text);
                            } else {
                                item.setDescription(text);
                                Log.d("Debug", "Description: " + text);
                            }
                        } else if (name.equals("link")) {
                            if (header) {

                                rssFeed.setChannelLink(channelUrl);
                            } else {
                                item.setLink(text);
                                Log.d("Debug", "Link: " + text);
                            }

                        } else if (name.equals("pubDate")) {

                            Log.d("Debug", "pubDate - item: " + items.size());
                            Log.d("Debug", "pubDate - text: " + text);

                            // Set item pubDate
                            item.setPubDate(text);

                        } else if (name.equals("enclosure")) {
                            item.setTorrentUrl(torrent);
                            Log.d("Debug", "Enclosure: " + torrent);
                        }

                        break;
                }

                event = xmlParser.next();

                if (!header) {
                    items.add(item);
                }

            }

            rssFeed.setItems(items);

            rssFeed.setItemCount(itemCount);

            rssFeed.setChannelPubDate(items.get(0).getPubDate());



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
        return rssFeed;


    }

    public RSSFeed getRSSChannelInfo(String url) {

        // Parse url
        Uri uri = Uri.parse(url);
        int event;
        String text = null;
        String torrent = null;
        boolean header = true;


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


        Log.d("Debug", "Host: " + uri.getAuthority());

        // Making HTTP request
        HttpHost targetHost = new HttpHost(uri.getAuthority());

        // httpclient = new DefaultHttpClient(httpParameters);
        // httpclient = new DefaultHttpClient();
        httpclient = getNewHttpClient();

        httpclient.setParams(httpParameters);


        RSSFeed rssFeed = new RSSFeed();

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

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlParser = xmlFactoryObject.newPullParser();

            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(is, null);


            event = xmlParser.getEventType();

            // Get Channel info
            String name;
            RSSFeedItem item = null;
            List<RSSFeedItem> items = new ArrayList<RSSFeedItem>();

            // Get items
            while (event != XmlPullParser.END_DOCUMENT && header) {


                name = xmlParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:

                        if (name != null && name.equals("item")) {
                            header = false;
                        }

                        break;

                    case XmlPullParser.TEXT:
                        text = xmlParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if (name.equals("title")) {
                            if (header) {
                                rssFeed.setChannelTitle(text);
                            }
                        } else if (name.equals("description")) {
                            if (header) {
                            }
                        } else if (name.equals("link")) {
                            if (header) {
                                rssFeed.setChannelLink(text);
                            }

                        }

                        break;
                }

                event = xmlParser.next();


            }
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
        return rssFeed;


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
