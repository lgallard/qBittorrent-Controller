/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {

	public HelpFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.tablet_help, container, false);

		return rootView;
	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.main, menu);
		// super.onCreateOptionsMenu(menu, inflater);

		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(true);
			menu.findItem(R.id.action_search).setVisible(true);
			menu.findItem(R.id.action_add).setVisible(true);

			if (menu.findItem(R.id.action_resume_all) != null) {
				menu.findItem(R.id.action_resume_all).setVisible(false);
			}

			if (menu.findItem(R.id.action_pause_all) != null) {
				menu.findItem(R.id.action_pause_all).setVisible(false);
			}

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

			if (menu.findItem(R.id.action_force_start) != null) {
				menu.findItem(R.id.action_force_start).setVisible(false);
			}

			if (menu.findItem(R.id.action_upload_rate_limit) != null) {
				menu.findItem(R.id.action_upload_rate_limit).setVisible(false);
			}

			if (menu.findItem(R.id.action_download_rate_limit) != null) {
				menu.findItem(R.id.action_download_rate_limit).setVisible(false);
			}

            if (menu.findItem(R.id.action_recheck) != null) {
                menu.findItem(R.id.action_recheck).setVisible(false);
            }

			if (menu.findItem(R.id.action_first_last_piece_prio) != null) {
				menu.findItem(R.id.action_first_last_piece_prio).setVisible(false);
			}

            if (menu.findItem(R.id.action_sequential_download) != null) {
                menu.findItem(R.id.action_sequential_download).setVisible(false);
            }

            if (menu.findItem(R.id.action_priority_menu) != null) {
                menu.findItem(R.id.action_priority_menu).setVisible(false);
            }

			if (menu.findItem(R.id.action_add_tracker) != null) {
				menu.findItem(R.id.action_add_tracker).setVisible(false);
			}

			if (menu.findItem(R.id.action_set_category) != null) {
				menu.findItem(R.id.action_label_menu).setVisible(false);
				menu.findItem(R.id.action_set_category).setVisible(false);
				menu.findItem(R.id.action_delete_category).setVisible(false);

			}


        }
	}

}
