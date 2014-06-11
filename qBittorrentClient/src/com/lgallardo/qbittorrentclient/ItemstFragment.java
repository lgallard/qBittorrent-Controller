package com.lgallardo.qbittorrentclient;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ItemstFragment extends ListFragment {

	int secondContainer;

	public ItemstFragment() {
	}

	public void setSecondFragmentContainer(int container) {

		this.secondContainer = container;

	}

	public int getSecondFragmentContainer() {

		return this.secondContainer;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.activity_main_original,
				container, false);

		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		TorrentDetailsFragment fragment = new TorrentDetailsFragment();

		fragment.setPosition(position);

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(this.getSecondFragmentContainer(), fragment)
					.addToBackStack(null).commit();
		}

	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.main, menu);
		// super.onCreateOptionsMenu(menu, inflater);

		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(true);
			menu.findItem(R.id.action_add).setVisible(true);
			menu.findItem(R.id.action_resume_all).setVisible(true);
			menu.findItem(R.id.action_pause_all).setVisible(true);

			if (menu.findItem(R.id.action_resume) != null) {
				menu.findItem(R.id.action_resume).setVisible(false);
			}
			if (menu.findItem(R.id.action_pause) != null) {
				menu.findItem(R.id.action_pause).setVisible(false);
			}
			if (menu.findItem(R.id.action_delete) != null) {
				menu.findItem(R.id.action_delete).setVisible(false);
			}
			if (menu.findItem(R.id.action_delete_drive) != null) {
				menu.findItem(R.id.action_delete_drive).setVisible(false);

			}

		}
	}

}