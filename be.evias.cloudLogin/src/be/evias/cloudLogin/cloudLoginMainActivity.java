package be.evias.cloudLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;

import be.evias.cloudLogin.models.User;
import be.evias.cloudLogin.authentication.AccountBase;
import be.evias.cloudLogin.services.ServiceBase;
import static be.evias.cloudLogin.authentication.AccountBase.sServerAuthenticate;

public class cloudLoginMainActivity
    extends ActionBarActivity
    implements FragmentNavigationDrawer.NavigationDrawerCallbacks
{
    public static final int     NAVIGATION_PROFILE    = 0;
    public static final int 	NAVIGATION_LOGOUT 	  = 1;

    public static String		ARG_ACCOUNT_INDEX = "account_index";
    public static final String 	KEY_ERROR_MESSAGE = "ERR_MSG";

    private FragmentNavigationDrawer mNavigationDrawerFragment;
    private CharSequence    mTitle;
    private Context 		mContext;
    private String 			mAccountName;
    private Account 		mAccount;
    private AccountManager 	mAccountManager;
    private SharedPreferences mPrefs;
    private User            mCurrentUser;
    private Boolean         mIsConnectionUp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();

        mAccountManager = AccountManager.get(this);
        mAccountName = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        mAccount     = new Account(mAccountName, AccountBase.ACCOUNT_TYPE);
        mContext 	 = getBaseContext();
        mPrefs       = mContext.getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
        mNavigationDrawerFragment = (FragmentNavigationDrawer)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        /* Set up the drawer. */
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * First thing when the activity is created is to check if
     * the connection to the server is UP.
     * If not finish the activity and tell the user.
     **/
    public void checkConnection()
    {
        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... params)
            {
                Bundle data = new Bundle();
                try {
                    ServiceBase service = new ServiceBase();
                    mIsConnectionUp     = service.ping(mContext);
                }
                catch (Exception e) {}

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                if (mIsConnectionUp == false) {
                    showMessage(mContext.getString(R.string.error_network), Toast.LENGTH_LONG);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position)
    {
        if (position == NAVIGATION_LOGOUT)
        	/* log out mAccount. */
        	logoutAccount(mAccount);
        else {
            /* open page */
            new AsyncTask<String, Void, Intent>()
            {
                @Override
                protected Intent doInBackground(String... params)
                {
                    Bundle data = new Bundle();
                    try {
                        SharedPreferences sp = getBaseContext().getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
                        String name  = sp.getString("cloudlogin_active_account_name", "");

                        mCurrentUser = sServerAuthenticate.getUserObject(getBaseContext(), name);

                        if (mCurrentUser == null)
                            throw new Exception("Could not retrieve User Object (Server Error).");
                    }
                    catch (Exception e) {
                        Log.d("cloudLogin", "cloudloginMainActivity/onNavigationDrawerItemSelected: getUserObject error.");
                        e.printStackTrace();
                    }

                    final Intent res = new Intent();
                    res.putExtras(data);
                    return res;
                }

                @Override
                protected void onPostExecute(Intent intent)
                {
                    if (mCurrentUser != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, cloudLoginPageFragment.createPage(position, mCurrentUser))
                                .commit();
                    }
                }
            }.execute();
        }
    }

    private void logoutAccount(final Account account)
    {
    	final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS, null, this, null,null);

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);

                    processLogoutServerSide(account);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }).start();
    }

    private void processLogoutServerSide(final Account account)
    {
    	new AsyncTask<String, Void, Intent>()
    	{
            @Override
            protected Intent doInBackground(String... params) {

                Bundle data = new Bundle();
                try {
                    sServerAuthenticate.userSignOut(mContext, account.name);
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);

                    Log.d("cloudLogin", "cloudloginMainActivity/logoutAccount: userSignOut successful.");
                    showMessage(mContext.getString(R.string.message_logout_success), Toast.LENGTH_SHORT);
                }
                catch (Exception e) {
                    Log.d("cloudLogin", "cloudloginMainActivity/logoutAccount: userSignOut error.");
                    e.printStackTrace();
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                else
                    finishLogout(intent);
            }
        }.execute();
    }

    private void finishLogout(Intent intent)
    {
    	String uName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        Log.d("cloudLogin", "cloudloginMainActivity/finishLogout: Account '" + uName + "' logged out.");

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("cloudlogin_active_account", false);
        ed.remove("cloudlogin_active_account_name");
        ed.remove("cloudlogin_active_account_email");
        ed.commit();

        setResult(RESULT_OK, intent);
        finish();
    }

    private void showMessage(final String msg, final int duration)
    {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getBaseContext(), msg, duration).show();
            }
        });
    }

    public void onSectionAttached(int number)
    {
        switch (number) {
        	default:
                mTitle = getString(R.string.app_name);
                break;
            case NAVIGATION_PROFILE:
                mTitle = getString(R.string.title_profile);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            /* Only show items in the action bar relevant to this screen
               if the drawer is not showing. Otherwise, let the drawer
               decide what to show in the action bar. */
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /* Handle action bar item clicks here.  */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
