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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
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

	static boolean nohome = false;

	// constructor
	public JSONParser() {
		this("", "", "", 0, "", "");
	}

	public JSONParser(String hostname, String subfolder, int port, String username, String password) {
		this(hostname, subfolder, "http", port, username, password);
	}

	public JSONParser(String hostname, String subfolder, String protocol, int port, String username, String password) {

		this.hostname = hostname;
		this.subfolder = subfolder;
		this.protocol = protocol;
		this.port = port;
		this.username = username;
		this.password = password;

	}

	public JSONObject getJSONFromUrl(String url) {

		// if server is published in a subfolder, fix url
		if (subfolder != null && subfolder != "") {
			url = subfolder + "/" + url;
		}

		HttpResponse httpResponse;
		DefaultHttpClient httpclient;

		HttpParams httpParameters = new BasicHttpParams();

		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 5000;

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 8000;

		// Set http parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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

			HttpGet httpget = new HttpGet(url);

			httpResponse = httpclient.execute(targetHost, httpget);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			// Log.i("parser", is.toString());

			// Build JSON
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
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
		} catch (IOException e) {
			Log.e("JSON", "IOException: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("JSON", "Generic: " + e.toString());
		}

		finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}

		// return JSON String
		return jObj;
	}

	public JSONArray getJSONArrayFromUrl(String url) {

		// if server is publish in a subfolder, fix url
		if (subfolder != null && subfolder != "") {
			url = subfolder + "/" + url;
		}

		HttpResponse httpResponse;
		DefaultHttpClient httpclient;

		HttpParams httpParameters = new BasicHttpParams();

		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 5000;

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 8000;

		// Set http parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// Making HTTP request
		HttpHost targetHost = new HttpHost(hostname, port, protocol);

		// httpclient = new DefaultHttpClient(httpParameters);
		// httpclient = new DefaultHttpClient();
		httpclient = getNewHttpClient();

		httpclient.setParams(httpParameters);

		try {

			AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

			httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

			HttpGet httpget = new HttpGet(url);

			httpResponse = httpclient.execute(targetHost, httpget);

			// This could help to detect if device is banned
			StatusLine statusLine = httpResponse.getStatusLine();
			int mStatusCode = statusLine.getStatusCode();
			Log.i("Status", "CODE: " + mStatusCode);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			// Log.i("parser", is.toString());

			// Build JSON

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
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
		} catch (IOException e) {
			Log.e("JSON", "IO: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("JSON", "Generic: " + e.toString());
		}

		finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			// Log.i("qbittorrent", "finaly - goodbye!");
			httpclient.getConnectionManager().shutdown();
		}

		// return JSON String
		return jArray;
	}

	public void postCommand(String command, String hash) {

		String key = "hash";

		String urlContentType = "application/x-www-form-urlencoded";

		String limit = "";
		String boundary = null;

		HttpResponse httpResponse;
		DefaultHttpClient httpclient;

		String url = "";

		if ("start".equals(command)) {
			url = "command/resume";
		}
		if ("pause".equals(command)) {
			url = "command/pause";
		}
		if ("delete".equals(command)) {
			url = "command/delete";
			key = "hashes";
		}
		if ("deleteDrive".equals(command)) {
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
		if ("pauseAll".equals(command)) {
			url = "command/pauseall";
		}
		if ("resumeAll".equals(command)) {
			url = "command/resumeall";
		}
		if ("increasePrio".equals(command)) {
			url = "command/increasePrio";
			key = "hashes";
		}
		if ("decreasePrio".equals(command)) {
			url = "command/decreasePrio";
			key = "hashes";
			;
		}
		if ("setQBittorrentPrefefrences".equals(command)) {
			url = "command/setPreferences";
			key = "json";
			// Log.i("setQBittorrentPrefefrences",
			// "setQBittorrentPrefefrences");
		}

		if ("setUploadRateLimit".equals(command)) {
			url = "command/setTorrentUpLimit";

			String[] tmpString = hash.split("&");
			hash = tmpString[0];
			limit = tmpString[1];

			Log.i("upload_rate_limit", "limit: " + limit);
		}

		if ("setDownloadRateLimit".equals(command)) {
			url = "command/setTorrentDlLimit";

			String[] tmpString = hash.split("&");
			hash = tmpString[0];
			limit = tmpString[1];

			Log.i("download_rate_limit", "limit: " + limit);
		}

		// if server is publish in a subfolder, fix url
		if (subfolder != null && subfolder != "") {
			url = subfolder + "/" + url;
		}

		// Making HTTP request
		HttpHost targetHost = new HttpHost(this.hostname, this.port, this.protocol);

		// httpclient = new DefaultHttpClient();
		httpclient = getNewHttpClient();

		try {

			AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);

			httpclient.getCredentialsProvider().setCredentials(authScope, credentials);

			HttpPost httpget = new HttpPost(url);

			// Log.i("qbittorrent", "1");

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
			if ("addTorrent".equals(command) || "increasePrio".equals(command) || "decreasePrio".equals(command)) {
				httpget.setHeader("Content-Type", urlContentType);
				// Log.i("qbittorrent", "urlContentType");
			}

			// Set content type and urls
			if ("addTorrentFile".equals(command)) {

				Log.i("addTorrentFile", "Sending file: " + hash);
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

				// Build entity
				HttpEntity entity = builder.build();

				// Set entity to http post
				httpget.setEntity(entity);

			}

			// Log.i("qbittorrent", "2");

			httpResponse = httpclient.execute(targetHost, httpget);

			// Log.i("qbittorrent", "3");

			HttpEntity httpEntity = httpResponse.getEntity();

			// Log.i("qbittorrent", "4");

			is = httpEntity.getContent();

			// Log.i("qbittorrent", "5");
			//
			// Log.i("parser", is.toString());
			//
			// Log.i("qbittorrent", "url:" + url);
			// Log.i("qbittorrent", "hostname:" + this.hostname);
			// Log.i("qbittorrent", "port:" + this.port);
			// Log.i("qbittorrent", "protocol:" + this.protocol);
			// Log.i("qbittorrent", "username:" + this.username);
			// Log.i("qbittorrent", "password:" + this.password);
			// Log.i("qbittorrent", "hash:" + hash);

		}

		catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
			Log.e("qbittorrent", "Client: " + e.toString());
		} catch (IOException e) {
			Log.e("qbittorrent", "IO: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("qbittorrent", "Generic: " + e.toString());
		}

		finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			// Log.i("qbittorrent", "finaly - goodbye!");
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

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

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
