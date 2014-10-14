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

import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ItemstFragment extends ListFragment {

	int secondContainer;
	TorrentDetailsFragment detailFragment;;

	public ItemstFragment() {
	}

	public void setSecondFragmentContainer(int container) {

		this.secondContainer = container;

	}

	public int getSecondFragmentContainer() {

		return this.secondContainer;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.activity_main_original, container, false);

		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		ListItemClicked(position);

	}

	public void ListItemClicked(int position) {

		ListView lv = this.getListView();

		int count = lv.getCount();

		if (count == 1 && lv.getItemAtPosition(0).equals(getString(R.string.no_results))) {

			return;
		}

		detailFragment = new TorrentDetailsFragment();

		detailFragment.setPosition(position);

		if (detailFragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(this.getSecondFragmentContainer(), detailFragment).addToBackStack("secondFragment").commit();
		}

	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.main, menu);
		// super.onCreateOptionsMenu(menu, inflater);

		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(true);
			menu.findItem(R.id.action_search).setVisible(true);
			menu.findItem(R.id.action_resume_all).setVisible(true);
			menu.findItem(R.id.action_pause_all).setVisible(true);
			menu.findItem(R.id.action_add).setVisible(true);

			if (menu.findItem(R.id.action_resume) != null) {
				menu.findItem(R.id.action_resume).setVisible(false);
			}
			if (menu.findItem(R.id.action_pause) != null) {
				menu.findItem(R.id.action_pause).setVisible(false);
			}
			if (menu.findItem(R.id.action_increase_prio) != null) {
				menu.findItem(R.id.action_increase_prio).setVisible(false);
			}
			if (menu.findItem(R.id.action_decrease_prio) != null) {
				menu.findItem(R.id.action_decrease_prio).setVisible(false);

			}
			if (menu.findItem(R.id.action_delete) != null) {
				menu.findItem(R.id.action_delete).setVisible(false);
			}
			if (menu.findItem(R.id.action_delete_drive) != null) {
				menu.findItem(R.id.action_delete_drive).setVisible(false);
			}

			if (menu.findItem(R.id.action_upload_rate_limit) != null) {
				menu.findItem(R.id.action_upload_rate_limit).setVisible(false);
			}

			if (menu.findItem(R.id.action_download_rate_limit) != null) {
				menu.findItem(R.id.action_download_rate_limit).setVisible(false);
			}

		}
	}

}
