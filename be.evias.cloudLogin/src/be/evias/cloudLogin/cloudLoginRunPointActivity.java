package be.evias.cloudLogin;

/**
 * LICENSE
 *
 Copyright 2015 Grégory Saive (greg@evias.be)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
**/

import static be.evias.cloudLogin.authentication.AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

import be.evias.cloudLogin.authentication.AccountBase;
import be.evias.cloudLogin.authentication.AuthenticatorActivity;

public class cloudLoginRunPointActivity
    extends Activity
{
    private static final String STATE_DIALOG     = "state_dialog";

    private String TAG = this.getClass().getSimpleName();
    private CharSequence        mTitle;
    private AccountManager      mAccountManager;
    private AlertDialog         mAlertDialog;
    private Context             mContext;
    private SharedPreferences   mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runpoint);

        mAccountManager = AccountManager.get(this);
        mContext        = getBaseContext();
        mPrefs          = mContext.getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);

        findViewById(R.id.create_select_account).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displayAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS);
            }
        });

        displayAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS);

        if (savedInstanceState != null) {
            boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
            if (showDialog)
                displayAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        boolean isActiveAccount = mPrefs.getBoolean("cloudlogin_active_account", false);

        super.onSaveInstanceState(outState);
        if ((mAlertDialog != null && mAlertDialog.isShowing())
            || ! isActiveAccount)
            outState.putBoolean(STATE_DIALOG, true);
    }

    /**
     * Add new account to the account manager for the cloudLogin
     * account type.
     *
     * @param accountType   String
     * @param authTokenType String
     */
    private void addNewAccount(String accountType, String authTokenType)
    {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>()
        {
            @Override
            public void run(AccountManagerFuture<Bundle> future)
            {
                try {
                    Bundle bnd = future.getResult();
                    showMessage(getBaseContext().getString(R.string.message_account_created), Toast.LENGTH_SHORT);
                    Log.d("cloudLogin", "AddNewAccount Bundle is " + bnd);

                    final Account account = new Account(bnd.getString(AccountManager.KEY_ACCOUNT_NAME), bnd.getString(AccountManager.KEY_ACCOUNT_TYPE));

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("cloudlogin_active_account", true);
                    editor.putString("cloudlogin_active_account_name", account.name);
                    editor.commit();

                    displayNavigationDrawer(bnd, account);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(), Toast.LENGTH_LONG);

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("cloudlogin_active_account", false);
                    editor.commit();
                }
            }
        }, null);
    }

    /**
     * Show all the accounts registered on the account manager.
     * Request an auth token upon user select.
     *
     * @param authTokenType     String
     */
    private void displayAccountPicker(final String authTokenType)
    {
        final boolean isActiveAccount     = mPrefs.getBoolean("cloudlogin_active_account", false);
        final String  activeAccountName   = mPrefs.getString("cloudlogin_active_account_name", "");
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountBase.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, getBaseContext().getString(R.string.message_no_accounts_yet), Toast.LENGTH_SHORT).show();

            addNewAccount(AccountBase.ACCOUNT_TYPE, AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
        else if (! isActiveAccount) {
            /* accounts are available but login is needed. */
            showMessage(getBaseContext().getString(R.string.message_session_expired), Toast.LENGTH_SHORT);

            /* user must re-login for more security (token is invalidated.) */
            addNewAccount(AccountBase.ACCOUNT_TYPE, AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
        else if (availableAccounts.length == 1) {
            /* single account, try to auto-login. */
            getAccountAuthToken(availableAccounts[0], authTokenType);
        }
        else {
            /* more than one account registered. Display a list
               of available accounts for the user to pick. */

            /* no account is currently active on the device. Display
               a list of available cloudLogin accounts. */
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++)
                name[i] = availableAccounts[i].name;

            mAlertDialog = new AlertDialog.Builder(this).setTitle(getBaseContext().getString(R.string.title_pick_account)).setAdapter(
                new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name),
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (TextUtils.equals(activeAccountName, availableAccounts[which].name))
                            getAccountAuthToken(availableAccounts[which], authTokenType);
                        else
                            addNewAccount(AccountBase.ACCOUNT_TYPE, AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS);
                    }
                }).create();
            mAlertDialog.show();
        }
    }

    /**
     * Get the auth token for an existing account on the AccountManager.
     *
     * @param account       Account
     * @param authTokenType String
     */
    private void getAccountAuthToken(final Account account, String authTokenType)
    {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    Log.d("cloudLogin", "cloudLoginRunPointActivity/getAccountAuthToken: User still logged in. Bundle: " + bnd);

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("cloudlogin_active_account", true);
                    editor.putString("cloudlogin_active_account_name", account.name);
                    editor.commit();

                    displayNavigationDrawer(bnd, account);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(), Toast.LENGTH_LONG);

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("cloudlogin_active_account", false);
                    editor.commit();
                }
            }
        }).start();
    }

    private void displayNavigationDrawer(Bundle bnd, Account act)
    {
        Intent act_main = new Intent(getBaseContext(), cloudLoginMainActivity.class);
        act_main.putExtra(AccountManager.KEY_ACCOUNT_NAME, act.name);
        startActivity(act_main);
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

}
