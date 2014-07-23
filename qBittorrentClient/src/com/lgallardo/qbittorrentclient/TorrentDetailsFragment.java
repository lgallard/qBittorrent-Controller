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
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;

public class TorrentDetailsFragment extends Fragment {

	// Torrent variables
	String name, info, hash, ratio, size, progress, state, leechs, seeds,
			priority, savePath, creationDate, comment, totalWasted,
			totalUploaded, totalDownloaded, timeElapsed, nbConnections,
			shareRatio = "";

	String hostname;
	String protocol;
	int port;
	String username;
	String password;

	int position;

	public TorrentDetailsFragment() {
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.torrent_details, container,
				false);

		Log.i("TorrentDetails", "Position =>>> " + position);

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
			savePath = MainActivity.lines[position].getSavePath();

			creationDate = MainActivity.lines[position].getCreationDate();
			comment = MainActivity.lines[position].getComment();
			totalWasted = MainActivity.lines[position].getTotalWasted();
			totalUploaded = MainActivity.lines[position].getTotalUploaded();
			totalDownloaded = MainActivity.lines[position].getTotalDownloaded();
			timeElapsed = MainActivity.lines[position].getTimeElapsed();
			nbConnections = MainActivity.lines[position].getNbConnections();
			shareRatio = MainActivity.lines[position].getShareRatio();

			TextView nameTextView = (TextView) rootView
					.findViewById(R.id.torrentName);
			TextView sizeTextView = (TextView) rootView
					.findViewById(R.id.torrentSize);
			TextView ratioTextView = (TextView) rootView
					.findViewById(R.id.torrentRatio);
			TextView progressTextView = (TextView) rootView
					.findViewById(R.id.torrentProgress);
			TextView stateTextView = (TextView) rootView
					.findViewById(R.id.torrentState);
			TextView leechsTextView = (TextView) rootView
					.findViewById(R.id.torrentLeechs);
			TextView seedsTextView = (TextView) rootView
					.findViewById(R.id.torrentSeeds);
			TextView hashTextView = (TextView) rootView
					.findViewById(R.id.torrentHash);
			TextView priorityTextView = (TextView) rootView
					.findViewById(R.id.torrentPriority);
			TextView pathTextView = (TextView) rootView
					.findViewById(R.id.torrentSavePath);
			TextView creationDateTextView = (TextView) rootView
					.findViewById(R.id.torrentCreationDate);
			TextView commentTextView = (TextView) rootView
					.findViewById(R.id.torrentComment);
			TextView totalWastedTextView = (TextView) rootView
					.findViewById(R.id.torrentTotalWasted);
			TextView totalUploadedTextView = (TextView) rootView
					.findViewById(R.id.torrentTotalUploaded);
			TextView totalDownloadedTextView = (TextView) rootView
					.findViewById(R.id.torrentTotalDownloaded);
			TextView timeElapsedTextView = (TextView) rootView
					.findViewById(R.id.torrentTimeElapsed);
			TextView nbConnectionsTextView = (TextView) rootView
					.findViewById(R.id.torrentNbConnections);
			TextView shareRatioTextView = (TextView) rootView
					.findViewById(R.id.torrentShareRatio);

			nameTextView.setText(name);
			sizeTextView.setText("Size: " + size);
			ratioTextView.setText("Ratio: " + ratio);
			progressTextView.setText("Progress: " + progress);
			stateTextView.setText("State: " + state);
			leechsTextView.setText("Leechs: " + leechs);
			seedsTextView.setText("Seeds: " + seeds);
			hashTextView.setText("Hash: " + hash);
			priorityTextView.setText("Priority: " + priority);
			pathTextView.setText("Save Path: " + savePath);	
			creationDateTextView.setText("Create Date: " + creationDate);
			commentTextView.setText("Comment: " + comment);
			totalWastedTextView.setText("Total Wasted: " + totalWasted);
			totalUploadedTextView.setText("Tota lUploaded: " + totalUploaded);
			totalDownloadedTextView.setText("Total Downloaded: " + totalDownloaded);
			timeElapsedTextView.setText("Time Elapsed: " + timeElapsed);
			nbConnectionsTextView.setText("Num. Connections: " + nbConnections);
			shareRatioTextView.setText("Share Ratio: " + shareRatio);
			
		}

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

		}
	}

}
