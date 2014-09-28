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

import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.Activity;

public class TorrentDetailsFragment extends Fragment {

	// Torrent variables
	String name, info, hash, ratio, size, progress, state, leechs, seeds, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
			totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded, eta, downloadSpeed, uploadSpeed = "";
	String hostname;
	String protocol;
	int port;
	String username;
	String password;
	String url;

	int position;

	JSONObject json2;

	private AdView adView;
	private View rootView;

	public TorrentDetailsFragment() {
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		rootView = inflater.inflate(R.layout.torrent_details, container, false);

		// Log.i("TorrentDetails", "Position =>>> " + position);

		savePath = "";
		creationDate = "";
		comment = "";
		uploadRateLimit = "";
		downloadRateLimit = "";
		totalWasted = "";
		totalUploaded = "";
		totalDownloaded = "";
		timeElapsed = "";
		nbConnections = "";
		shareRatio = "";

		try {
			if (MainActivity.lines != null && position != -1) {
				name = MainActivity.lines[position].getFile();
				size = MainActivity.lines[position].getSize();
				hash = MainActivity.lines[position].getHash();
				ratio = MainActivity.lines[position].getRatio();
				progress = MainActivity.lines[position].getProgress();
				state = MainActivity.lines[position].getState();
				leechs = MainActivity.lines[position].getLeechs();
				seeds = MainActivity.lines[position].getSeeds();
				hash = MainActivity.lines[position].getHash();
				priority = MainActivity.lines[position].getPriority();				
				eta = MainActivity.lines[position].getEta();
				
				// Get torrent's extra info
				url = "/json/propertiesGeneral/";

				try {
					
					JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port, MainActivity.username, MainActivity.password);
					
					json2 = jParser.getJSONFromUrl(url + hash);

					// If no data, throw exception
					if (json2.length() == 0) {

						throw (new Exception());

					}

					MainActivity.lines[position].setSavePath(json2.getString(MainActivity.TAG_SAVE_PATH));
					MainActivity.lines[position].setCreationDate(json2.getString(MainActivity.TAG_CREATION_DATE));
					MainActivity.lines[position].setComment(json2.getString(MainActivity.TAG_COMMENT));
					MainActivity.lines[position].setTotalWasted(json2.getString(MainActivity.TAG_TOTAL_WASTED));
					MainActivity.lines[position].setTotalUploaded(json2.getString(MainActivity.TAG_TOTAL_UPLOADED));
					MainActivity.lines[position].setTotalDownloaded(json2.getString(MainActivity.TAG_TOTAL_DOWNLOADED));
					MainActivity.lines[position].setTimeElapsed(json2.getString(MainActivity.TAG_TIME_ELAPSED));
					MainActivity.lines[position].setNbConnections(json2.getString(MainActivity.TAG_NB_CONNECTIONS));
					MainActivity.lines[position].setShareRatio(json2.getString(MainActivity.TAG_SHARE_RATIO));
					MainActivity.lines[position].setUploadLimit(json2.getString(MainActivity.TAG_UPLOAD_LIMIT));
					MainActivity.lines[position].setDownloadLimit(json2.getString(MainActivity.TAG_DOWNLOAD_LIMIT));
				} catch (Exception e) {
					Log.e("MAIN:", e.toString());
				}

				
				savePath = MainActivity.lines[position].getSavePath();
				creationDate = MainActivity.lines[position].getCreationDate();
				comment = MainActivity.lines[position].getComment();
				totalWasted = MainActivity.lines[position].getTotalWasted();
				totalUploaded = MainActivity.lines[position].getTotalUploaded();
				totalDownloaded = MainActivity.lines[position].getTotalDownloaded();
				timeElapsed = MainActivity.lines[position].getTimeElapsed();
				nbConnections = MainActivity.lines[position].getNbConnections();
				shareRatio = MainActivity.lines[position].getShareRatio();
				uploadRateLimit = MainActivity.lines[position].getUploadLimit();
				downloadRateLimit = MainActivity.lines[position].getDownloadLimit();

				TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
				TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
				TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
				TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
				TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
				TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
				TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
				TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);
				TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
				TextView pathTextView = (TextView) rootView.findViewById(R.id.torrentSavePath);
				TextView creationDateTextView = (TextView) rootView.findViewById(R.id.torrentCreationDate);
				TextView commentTextView = (TextView) rootView.findViewById(R.id.torrentComment);
				TextView totalWastedTextView = (TextView) rootView.findViewById(R.id.torrentTotalWasted);
				TextView totalUploadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalUploaded);
				TextView totalDownloadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalDownloaded);
				TextView timeElapsedTextView = (TextView) rootView.findViewById(R.id.torrentTimeElapsed);
				TextView nbConnectionsTextView = (TextView) rootView.findViewById(R.id.torrentNbConnections);
				TextView shareRatioTextView = (TextView) rootView.findViewById(R.id.torrentShareRatio);
				TextView uploadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentUploadRateLimit);
				TextView downloadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentDownloadRateLimit);

				nameTextView.setText(name);
				sizeTextView.setText(size);
				ratioTextView.setText(ratio);
				stateTextView.setText(state);
				leechsTextView.setText(leechs);
				seedsTextView.setText(seeds);
				progressTextView.setText(progress);
				hashTextView.setText(hash);
				priorityTextView.setText(priority);
				pathTextView.setText(savePath);
				creationDateTextView.setText(creationDate);
				commentTextView.setText(comment);
				totalWastedTextView.setText(totalWasted);
				totalUploadedTextView.setText(totalUploaded);
				totalDownloadedTextView.setText(totalDownloaded);
				timeElapsedTextView.setText(timeElapsed);
				nbConnectionsTextView.setText(nbConnections);
				shareRatioTextView.setText(shareRatio);
				uploadRateLimitTextView.setText(uploadRateLimit);
				downloadRateLimitTextView.setText(downloadRateLimit);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("TorrentDetailsFragment - onCreateView", e.toString());
		}

		// Load banner
		loadBanner();
		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.i("FragmentLIst", "Item touched");
	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {

			if (getActivity().findViewById(R.id.one_frame) != null) {
				menu.findItem(R.id.action_refresh).setVisible(false);
			}
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_resume_all).setVisible(false);
			menu.findItem(R.id.action_pause_all).setVisible(false);

			menu.findItem(R.id.action_resume).setVisible(true);
			menu.findItem(R.id.action_pause).setVisible(true);
			menu.findItem(R.id.action_increase_prio).setVisible(true);
			menu.findItem(R.id.action_decrease_prio).setVisible(true);
			menu.findItem(R.id.action_delete).setVisible(true);
			menu.findItem(R.id.action_delete_drive).setVisible(true);
			menu.findItem(R.id.action_download_rate_limit).setVisible(true);
			menu.findItem(R.id.action_upload_rate_limit).setVisible(true);

		}
	}

	// Load Banner
	public void loadBanner() {

		// LinearLayout linearLayout = null;

		// linearLayout.removeView(adView);
		//
		// LinearLayout.LayoutParams adsParams = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// android.view.Gravity.BOTTOM |
		// android.view.Gravity.CENTER_HORIZONTAL);
		//
		//
		// linearLayout.addView(adView, adsParams);
		//

		// Get the adView.
		adView = (AdView) getActivity().findViewById(R.id.adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

	}

}
