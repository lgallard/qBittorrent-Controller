/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lgallardo.qbittorrentclient.qBittorrentClient.myAdapter;
import com.lgallardo.qbittorrentclient.qBittorrentClient.myObject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class qBittorrentBinder extends Binder {
	static InputStream is = null;
	private JSONObject jObj = null;
	private JSONArray jArray = null;
	private String json = "";
	protected String hostname;
	protected int port;
	protected String protocol;
	protected String username;
	protected String password;
	protected static boolean oldVersion;

	static boolean nohome = false;

	// JSON Node Names
	protected static final String TAG_USER = "user";
	protected static final String TAG_ID = "id";
	protected static final String TAG_ALTDWLIM = "alt_dl_limit";
	protected static final String TAG_DWLIM = "dl_limit";

	// Torrent Info TAGs
	protected static final String TAG_NAME = "name";
	protected static final String TAG_SIZE = "size";
	protected static final String TAG_PROGRESS = "progress";
	protected static final String TAG_STATE = "state";
	protected static final String TAG_HASH = "hash";
	protected static final String TAG_DLSPEED = "dlspeed";
	protected static final String TAG_UPSPEED = "upspeed";
	protected static final String TAG_NUMLEECHS = "num_leechs";
	protected static final String TAG_NUMSEEDS = "num_seeds";
	protected static final String TAG_RATIO = "ratio";

	protected static final String TAG_INFO = "info";

	protected static final String TAG_ACTION = "action";
	protected static final String TAG_START = "start";
	protected static final String TAG_PAUSE = "pause";
	protected static final String TAG_DELETE = "delete";
	protected static final String TAG_DELETE_DRIVE = "deleteDrive";

	protected static final int ACTION_CODE = 0;
	protected static final int START_CODE = 1;
	protected static final int PAUSE_CODE = 2;
	protected static final int DELETE_CODE = 3;
	protected static final int DELETE_DRIVE_CODE = 4;

	// constructor
	public qBittorrentBinder() {

	}
	
	public void getTorrenList(String url, String hostname, String protocol, String port,
			String username, String password, qBittorrentListener listener){
		
		qBittorrentTask qtt = new qBittorrentTask(listener);
		
		qtt.execute(new String[] {url, hostname, protocol, port,username,password});
		
		
	}

	// This will be the new postCommand method, similar to getTorrenList	
	public void postCommand(){
		
	}

	// To be deprecated
	public JSONObject getJSONFromUrl(String url) {

		HttpResponse httpResponse;
		DefaultHttpClient httpclient;

		// Making HTTP request
		HttpHost targetHost = new HttpHost(this.hostname, this.port,
				this.protocol);

		httpclient = new DefaultHttpClient();
		try {

			AuthScope authScope = new AuthScope(targetHost.getHostName(),
					targetHost.getPort());
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					this.username, this.password);

			httpclient.getCredentialsProvider().setCredentials(authScope,
					credentials);

			HttpGet httpget = new HttpGet(url);

			httpResponse = httpclient.execute(targetHost, httpget);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			Log.i("parser", is.toString());

			// Build JSON
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
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

	// To be deprecated
	public void postCommand(String command, String hash) {

		String key = "hash";

		String urlContentType = "application/x-www-form-urlencoded";

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

		Log.i("qbittorrent", "url:" + url);
		Log.i("qbittorrent", "hostname:" + this.hostname);
		Log.i("qbittorrent", "port:" + this.port);
		Log.i("qbittorrent", "protocol:" + this.protocol);
		Log.i("qbittorrent", "username:" + this.username);
		Log.i("qbittorrent", "password:" + this.password);
		Log.i("qbittorrent", "hash:" + hash);

		// Making HTTP request
		HttpHost targetHost = new HttpHost(this.hostname, this.port,
				this.protocol);

		httpclient = new DefaultHttpClient();
		try {

			AuthScope authScope = new AuthScope(targetHost.getHostName(),
					targetHost.getPort());

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					this.username, this.password);

			httpclient.getCredentialsProvider().setCredentials(authScope,
					credentials);

			HttpPost httpget = new HttpPost(url);

			Log.i("qbittorrent", "1");

			// In order to pass the has we must set the pair name value

			BasicNameValuePair bnvp = new BasicNameValuePair(key, hash);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(bnvp);
			httpget.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			// Set content type and urls
			if ("addTorrent".equals(command)) {
				httpget.setHeader("Content-Type", urlContentType);
			}

			Log.i("qbittorrent", "2");

			httpResponse = httpclient.execute(targetHost, httpget);

			Log.i("qbittorrent", "3");

			HttpEntity httpEntity = httpResponse.getEntity();

			Log.i("qbittorrent", "4");

			is = httpEntity.getContent();

			Log.i("qbittorrent", "5");

			Log.i("parser", is.toString());

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
			httpclient.getConnectionManager().shutdown();
		}

	}

	// Here is where the action happens
	protected class qBittorrentTask extends
			AsyncTask<String, Integer, myObject[]> {

		qBittorrentListener listener = null;
		qBittorrentBinder binder;
		String url = null;

		String name, size, info, progress, state, hash, ratio, leechs, seeds;

		myObject[] objects = null;

		public qBittorrentTask(qBittorrentListener listener) {
			this.listener = listener;
		}

		@Override
		protected myObject[] doInBackground(String... params) {

			// Fetch JSON

			url = params[0];

			hostname = params[1];
			protocol = params[2];
			port = Integer.parseInt(params[3]);
			username = params[4];
			password = params[5];

			HttpResponse httpResponse;
			DefaultHttpClient httpclient;

			// Log.i("getJSONArrayFromUrl", "url:" + url);
			// Log.i("getJSONArrayFromUrl", "hostname:" + this.hostname);
			// Log.i("getJSONArrayFromUrl", "password:" + this.password);
			// Log.i("getJSONArrayFromUrl", "port:" + this.port);
			// Log.i("getJSONArrayFromUrl", "protocol:" + this.protocol);

			// Making HTTP request
			HttpHost targetHost = new HttpHost(hostname, port, protocol);

			httpclient = new DefaultHttpClient();
			try {

				AuthScope authScope = new AuthScope(targetHost.getHostName(),
						targetHost.getPort());
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
						username, password);

				httpclient.getCredentialsProvider().setCredentials(authScope,
						credentials);

				HttpGet httpget = new HttpGet(url);

				httpResponse = httpclient.execute(targetHost, httpget);

				StatusLine statusLine = httpResponse.getStatusLine();
				int mStatusCode = statusLine.getStatusCode();
				Log.i("Status", "CODE: " + mStatusCode);

				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				Log.i("parser", is.toString());

				// Build JSON

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();

				// Creat array from JSON
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
				httpclient.getConnectionManager().shutdown();
			}

			// Handle JSON Array result (jArray)

			if (jArray != null) {

				Log.i("jArray length", "" + jArray.length());

				try {

					objects = new myObject[jArray.length()];

					qBittorrentClient.names = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						name = json.getString(TAG_NAME);
						size = json.getString(TAG_SIZE);
						progress = String.format("%.2f",
								json.getDouble(TAG_PROGRESS) * 100)
								+ "%";
						info = size + " | D:" + json.getString(TAG_DLSPEED)
								+ " | U:" + json.getString(TAG_UPSPEED) + " | "
								+ progress;
						state = json.getString(TAG_STATE);
						hash = json.getString(TAG_HASH);
						ratio = json.getString(TAG_RATIO);
						leechs = json.getString(TAG_NUMLEECHS);
						seeds = json.getString(TAG_NUMSEEDS);

						objects[i] = new myObject(name, size, state, hash,
								info, ratio, progress, leechs, seeds);

						qBittorrentClient.names[i] = name;
					}
				} catch (JSONException e) {
					Log.e("MAIN:", e.toString());
				}

			}
			return objects;

		}

		@Override
		protected void onPostExecute(myObject[] result) {

			if (listener != null && result != null) {
				listener.updateUI(result);
			}

		}

	}
	
	// Here is where the action happens
	private class qBittorrentCommand extends AsyncTask<String, Integer, String>
	{

		private qBittorrentListener listener;
		
		public qBittorrentCommand(qBittorrentListener listener) {
			this.listener = listener;
		}

		@Override
		protected String doInBackground(String... params) {

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, port, username,
												password);

			jParser.postCommand(params[0], params[1]);

			// move postCommand code to here
			
			return params[0];

		}

		@Override
		protected void onPostExecute(String result) {
			
			// TODO: A getTorrentList should be invoked from the Activity
			// to update the UI
			
			// Send message to the activity
			
			int messageId = R.string.connection_error;

			if (result == null) {
				messageId = R.string.connection_error;
			}

			if ("start".equals(result)) {
				messageId = R.string.torrentStarted;
			}

			if ("pause".equals(result)) {
				messageId = R.string.torrentPaused;
			}

			if ("delete".equals(result)) {
				messageId = R.string.torrentDeleled;
			}

			if ("deleteDrive".equals(result)) {
				messageId = R.string.torrentDeletedDrive;
			}

			if ("addTorrent".equals(result)) {
				messageId = R.string.torrentAdded;
			}
			
			if (listener != null) {
				listener.sendCommandResult(result);
			}
			
		    // this should be moved to the Activity

//			Toast.makeText(getApplicationContext(), messageId,
//						   Toast.LENGTH_LONG).show();
//
		}
	}
	

	class myObject {

		private String file;
		private String size;
		private String info;
		private String state;
		private String hash;
		private String downloadSpeed;
		private String ratio;
		private String progress;
		private String leechs;
		private String seeds;

		public myObject(String file, String size, String state, String hash,
				String info, String ratio, String progress, String leechs,
				String seeds) {
			this.file = file;
			this.size = size;
			this.state = state;
			this.hash = hash;
			this.info = info;
			this.ratio = ratio;
			this.progress = progress;
			this.leechs = leechs;
			this.seeds = seeds;
		}

		public String getFile() {
			return this.file;
		}

		public String getSize() {
			return this.size;
		}

		public String getState() {
			return this.state;
		}

		public String getHash() {
			return this.hash;
		}

		public String getInfo() {
			return this.info;
		}

		public String getRatio() {
			return this.ratio;
		}

		public String getProgress() {
			return this.progress;
		}

		public String getLeechs() {
			return this.leechs;
		}

		public String getSeeds() {
			return this.seeds;
		}

		public void setInfo(String info) {
			this.info = info;
		}

	}

}
