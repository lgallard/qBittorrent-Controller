package com.lgallardo.qbittorrentclient;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by lgallard on 07/07/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {


    private MainActivity mActivity;
    private ListView mLeftDrawer;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // Set off the	touch mode in the device (to avoid ignoring key	events)
        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        mLeftDrawer = (ListView) mActivity.findViewById(R.id.left_drawer);
    }

    // Test the All list
    @UiThreadTest
    public void testAllListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(0, null, null),
                0,
                mLeftDrawer.getAdapter().getItemId(0));

        assertEquals("All torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[0],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Download list
    @UiThreadTest
    public void testDownloadListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(1, null, null),
                1,
                mLeftDrawer.getAdapter().getItemId(1));

        assertEquals("Download torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[1],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Completed list
    @UiThreadTest
    public void testCompletedListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(2, null, null),
                2,
                mLeftDrawer.getAdapter().getItemId(2));

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[2],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Paused list
    @UiThreadTest
    public void testPausedListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(3, null, null),
                3,
                mLeftDrawer.getAdapter().getItemId(3));

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[3],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Active list
    @UiThreadTest
    public void testActiveListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(4, null, null),
                4,
                mLeftDrawer.getAdapter().getItemId(4));

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[4],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Inactive list
    @UiThreadTest
    public void testInactiveListClicked() {


        // Click download
        mLeftDrawer.performItemClick(mLeftDrawer.getAdapter().getView(5, null, null),
                5,
                mLeftDrawer.getAdapter().getItemId(5));

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[5],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test if the Get Pro item appears in the listview
    @UiThreadTest
    public void testGetPRO() {


        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {

            assertEquals("Get PRO not in menu drawer",
                    mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[8],
                    ((TextView) mLeftDrawer.getAdapter().getView(8, null, null).findViewById(R.id.textViewName)).getText().toString());

        }
    }

}