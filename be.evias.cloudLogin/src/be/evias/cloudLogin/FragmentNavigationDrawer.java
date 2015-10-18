package be.evias.cloudLogin;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.content.Context;
import android.util.Log;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.evias.cloudLogin.models.User;
import be.evias.cloudLogin.authentication.AccountBase;
import static be.evias.cloudLogin.authentication.AccountBase.sServerAuthenticate;

public class FragmentNavigationDrawer
    extends Fragment
{
    public static interface NavigationDrawerCallbacks
    {
        void onNavigationDrawerItemSelected(int position);
    }

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_USER_LOGGED_IN = "user_logged_in";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private boolean mUserLoggedIn;
    private SharedPreferences mPrefs;
    private Context           mContext;
    private User              mCurrentUser;

    public FragmentNavigationDrawer()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getBaseContext();
        /* Read in the flag indicating whether or not the user has demonstrated awareness of the
           drawer. See PREF_USER_LEARNED_DRAWER for details. */
        mPrefs   = getActivity().getBaseContext()
                              .getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        /* Indicate that this fragment would like to influence
           the set of actions in the action bar. */
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        retrieveAndStoreUserObject();
        return mDrawerListView;
    }

    public void retrieveAndStoreUserObject()
    {
        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... params)
            {
                Bundle data = new Bundle();
                try {
                    final String accountName = mPrefs.getString("cloudlogin_active_account_name", "");

                    if (accountName.length() == 0)
                        throw new Exception("No active User Session found on this device.");

                    mCurrentUser             = sServerAuthenticate.getUserObject(mContext, accountName);

                    if (mCurrentUser == null)
                        throw new Exception("Could not retrieve User Object (Server Error).");
                }
                catch (Exception e) {
                    Log.d("cloudLogin", "FragmentNavigationDrawer/retrieveAndStoreUserObject: getUserObject error.");
                    e.printStackTrace();
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                if (mCurrentUser != null)
                    initializeNavigationAdapter();
                else {
                    Toast.makeText(getActivity(), mContext.getString(R.string.error_network), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public void initializeNavigationAdapter()
    {
        List<HashMap<String,String>> navigation_items = new ArrayList<HashMap<String,String>>();
        navigation_items.add(new HashMap<String,String>() {{
            put("icon", Integer.toString(R.drawable.icn_profile));
            put("text", mCurrentUser.getName());
            put("desc", mCurrentUser.getEmail());
        }});
        navigation_items.add(new HashMap<String,String>() {{
            put("icon", Integer.toString(R.drawable.icn_checkout));
            put("text", getString(R.string.title_logout));
            put("desc", getString(R.string.desc_logout));
        }});

        String[] keys = {"icon", "text", "desc"};
        int[]    ids  = {R.id.item_icon, R.id.item_text, R.id.item_description};

        SimpleAdapter adapter = new SimpleAdapter(getActionBar().getThemedContext(), navigation_items,
                                                  R.layout.navigation_drawer_item, keys, ids);

        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
    }

    public boolean isDrawerOpen()
    {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public User getCurrentUser()
    {
        return mCurrentUser;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout)
    {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        /* set a custom shadow that overlays the main content when the drawer opens. */
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        /* configure actionbar buttons. */
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                if (!isAdded())
                    return;

                /* calls onPrepareOptionsMenu() */
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                if (!isAdded())
                    return;

                if (!mUserLearnedDrawer) {
                    /* The user manually opened the drawer; store this flag to prevent auto-showing
                       the navigation drawer automatically in the future. */
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                /* calls onPrepareOptionsMenu() */
                getActivity().supportInvalidateOptionsMenu();
            }
        };

        /* If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
           per the navigation drawer design guidelines. */
        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
            mDrawerLayout.openDrawer(mFragmentContainerView);

        /* Defer code dependent on restoration of previous instance state. */
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position)
    {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration to the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar()
    {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
