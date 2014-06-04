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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TorrentActionsActivity extends Activity {

	// Torrent variables
	String name, info, hash, ratio, size, progress, state, leechs, seeds, priority;

	String hostname;
	String protocol;
	int port;
	String username;
	String password;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.torrent_details);

		Intent intent = getIntent();
		name = intent.getStringExtra(MainActivity.TAG_NAME);
		info = intent.getStringExtra(MainActivity.TAG_INFO);
		hash = intent.getStringExtra(MainActivity.TAG_HASH);
		ratio = intent.getStringExtra(MainActivity.TAG_RATIO);
		size = intent.getStringExtra(MainActivity.TAG_SIZE);
		progress = intent.getStringExtra(MainActivity.TAG_PROGRESS);
		state = intent.getStringExtra(MainActivity.TAG_STATE);
		leechs = intent.getStringExtra(MainActivity.TAG_NUMLEECHS);
		seeds = intent.getStringExtra(MainActivity.TAG_NUMSEEDS);
		priority = intent.getStringExtra(MainActivity.TAG_PRIORITY);
		

		hostname = intent.getStringExtra("hostname");
		protocol = intent.getStringExtra("protocol");
		port = intent.getIntExtra("port", 0);
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");

		Log.i("TorrentAA", "port: " + port);

		TextView nameTextView = (TextView) this.findViewById(R.id.torrentName);
		TextView infoTextView = (TextView) this.findViewById(R.id.torrentSize);
		TextView ratioTextView = (TextView) this.findViewById(R.id.torrentRatio);
		TextView progressTextView = (TextView) this.findViewById(R.id.torrentProgress);
		TextView stateTextView = (TextView) this.findViewById(R.id.torrentState);
		TextView leechsTextView = (TextView) this.findViewById(R.id.torrentLeechs);
		TextView seedsTextView = (TextView) this.findViewById(R.id.torrentSeeds);
		TextView hashTextView = (TextView) this.findViewById(R.id.torrentHash);
		TextView priorityTextView = (TextView) this.findViewById(R.id.torrentPriority);

		nameTextView.setText(name);

		progressTextView.setText("Progress: " + progress);
		infoTextView.setText("Size: " + size);
		ratioTextView.setText("Ratio: " + ratio);
		stateTextView.setText("State: " + state);
		leechsTextView.setText("Leechs: " + leechs);
		seedsTextView.setText("Seeds: " + seeds);
		hashTextView.setText("Hash: " + hash);
		priorityTextView.setText("Priority: "+priority);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.torrent_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.action_about:
		// // About option clicked.
		// return true;
		case R.id.action_play:
			// Refresh option clicked.
			startTorrent();
			return true;
		case R.id.action_pause:
			// Refresh option clicked.
			pauseTorrent();
			return true;
		case R.id.action_delete:
			// Refresh option clicked.
			deleteTorrent();
			return true;
		case R.id.action_delete_drive:
			// Refresh option clicked.
			deleteDriveTorrent();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startTorrent() {
		
		// Intent to return data
		// Create a new intent and save data
		Intent data = new Intent();

		data.putExtra(MainActivity.TAG_ACTION, MainActivity.START_CODE);
		data.putExtra(MainActivity.TAG_HASH, hash);
		
		// Set Activity's result with result code RESULT_OK
		setResult(RESULT_OK, data);

		// Return
		finish();

	}

	public void pauseTorrent() {

		// Intent to return data
		// Create a new intent and save data
		Intent data = new Intent();

		data.putExtra(MainActivity.TAG_ACTION, MainActivity.PAUSE_CODE);
		data.putExtra(MainActivity.TAG_HASH, hash);
		
		// Set Activity's result with result code RESULT_OK
		setResult(RESULT_OK, data);

		// Return
		finish();

	}

	public void deleteTorrent() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				TorrentActionsActivity.this);

		// Message
		builder.setMessage(R.string.dm_deleteTorrent).setTitle(
				R.string.dt_deleteTorrent);

		// OK
		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// Cancel
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User accepted the dialog
						
						// Intent to return data
						// Create a new intent and save data
						Intent data = new Intent();

						data.putExtra(MainActivity.TAG_ACTION, MainActivity.DELETE_CODE);
						data.putExtra(MainActivity.TAG_HASH, hash);
						
						// Set Activity's result with result code RESULT_OK
						setResult(RESULT_OK, data);

						// Return
						finish();

					}
				});

		// Create dialog
		AlertDialog dialog = builder.create();

		// Show dialog
		dialog.show();

	}
	public void deleteDriveTorrent() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				TorrentActionsActivity.this);

		// Message
		builder.setMessage(R.string.dm_deleteDriveTorrent).setTitle(
				R.string.dt_deleteDriveTorrent);

		// Cancel
		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// OK
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User accepted the dialog

						// Intent to return data
						// Create a new intent and save data
						Intent data = new Intent();

						data.putExtra(MainActivity.TAG_ACTION, MainActivity.DELETE_DRIVE_CODE);
						data.putExtra(MainActivity.TAG_HASH, hash);
						
						// Set Activity's result with result code RESULT_OK
						setResult(RESULT_OK, data);

						// Return
						finish();
					}
				});

		// Create dialog
		AlertDialog dialog = builder.create();

		// Show dialog
		dialog.show();

	}

}
