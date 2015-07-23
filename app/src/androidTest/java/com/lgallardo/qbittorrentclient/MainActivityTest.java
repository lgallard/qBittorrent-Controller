package com.lgallardo.qbittorrentclient;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import java.util.ArrayList;

/**
 * Created by lgallard on 07/07/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {


    private MainActivity mActivity;
    private ListView mLeftDrawer;
    private Solo mSolo;
    private ArrayList<ListView> mListViews;
    int mLeftDrawerIndex;

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


        //	Initiate	the	instance	of	Solo
        mSolo = new Solo(getInstrumentation(), getActivity());


        // Get All ListViews
        mListViews = mSolo.getCurrentViews(ListView.class);

        // Define index for left_drawer list view
        mLeftDrawerIndex = 1;

        // Get drawer list view index
        for (int i = 0; i < mListViews.size(); i++) {
            if (mListViews.get(i).getId() == R.id.left_drawer) {
                mLeftDrawerIndex = i;
                break;
            }
        }


    }

    // Test the All list
    public void testAllListClicked() throws Exception {

        // Click All
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(1, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("All torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[0],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Download list
    public void testDownloadListClicked() {

        // Click Download
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(2, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Download torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[1],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Completed list
    public void testCompletedListClicked() {

        // Click Completed
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(3, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[2],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Paused list
    public void testPausedListClicked() {

        // Click Paused
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(4, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[3],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Active list
    public void testActiveListClicked() {


        // Click Active
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(5, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[4],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Inactive list
    public void testInactiveListClicked() {


        // Click Inactive
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(6, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[5],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test if the Get Pro item appears in the listview
    public void testGetPRO() {


        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {

            assertEquals("Get PRO not in menu drawer",
                    mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[8],
                    ((TextView) mLeftDrawer.getAdapter().getView(8, null, null).findViewById(R.id.textViewName)).getText().toString());

        }
    }

    // Test if RSS Activity is launched
    public void testRSSLaunched() {

        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_rss));
        mSolo.assertCurrentActivity("Can't open RSS Feed activity", RSSFeedActivity.class);

    }

    // Test if Options is launched
    public void testOptionsLaunched() {

        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(7, mLeftDrawerIndex);
        mSolo.assertCurrentActivity("Can't open Options activity", OptionsActivity.class);

    }


    // Test if Settings is launched
    public void testSettingsLaunched() {

        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(8, mLeftDrawerIndex);
        mSolo.assertCurrentActivity("Can't open Settings activity", SettingsActivity.class);

    }


    // Test if Help is launched
    public void testHelpLaunched() {

        mSolo.clickOnActionBarHomeButton();

        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
            mSolo.clickInList(9, mLeftDrawerIndex);
        } else {
            mSolo.clickInList(10, mLeftDrawerIndex);
        }

        mSolo.assertCurrentActivity("Can't open Help activity", HelpActivity.class);

    }

    // Test Add torrent
    public void test1AddTorrent() {


        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_add));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        EditText url = (EditText) mSolo.getView(R.id.url);

        // http://cdimage.debian.org/debian-cd/8.1.0/amd64/bt-cd/debian-8.1.0-amd64-CD-1.iso.torrent
        mSolo.enterText(url, "http://cdimage.debian.org/debian-cd/8.1.0/amd64/bt-cd/debian-8.1.0-amd64-CD-1.iso.torrent");
        mSolo.clickOnButton(mSolo.getString(R.string.ok));

        assertTrue("Torrent not sent", mSolo.waitForText(mSolo.getString(R.string.torrentAdded)));


        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Get item with text "debian"
        assertNotNull("Torrent not on Downloading list", mSolo.getText("debian"));


    }

    // Test Pause torrent
    public void test2PauseTorrentFromCAB() {

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_pause));

        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(4, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Get item with text "debian"
        assertNotNull("Torrent not on Paused list", mSolo.getText("debian"));


    }

    // Test Resume torrent
    public void test2ResumeTorrenttFromCAB() {

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_resume));

        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertNotNull("Torrent not on Downloading list", mSolo.getText("debian"));


    }

    // Test Delete torrent
    public void test2DeleteTorrenttFromCAB() {

        // Add torrent first
        test1AddTorrent();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));

        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertFalse("Torrent not Deleted", mSolo.searchText("debian"));

    }

    // Test Delete with data torrent
    public void test2DeleteDataTorrenttFromCAB() {

        // Add torrent first
        test1AddTorrent();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete_drive));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));

        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInList(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertFalse("Torrent not Deleted with data", mSolo.searchText("debian"));

    }

}