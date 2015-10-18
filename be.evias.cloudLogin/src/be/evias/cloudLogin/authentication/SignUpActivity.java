package be.evias.cloudLogin.authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import be.evias.cloudLogin.R;

import static be.evias.cloudLogin.authentication.AccountBase.sServerAuthenticate;
import static be.evias.cloudLogin.authentication.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static be.evias.cloudLogin.authentication.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static be.evias.cloudLogin.authentication.AuthenticatorActivity.PARAM_USER_PASS;

public class SignUpActivity
    extends Activity
{
    private String TAG = getClass().getSimpleName();
    private String mAccountType;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mContext     = getBaseContext();

        setContentView(R.layout.activity_signup);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        new AsyncTask<String, Void, Intent>() {

            String name = ((TextView) findViewById(R.id.name)).getText().toString().trim();
            String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
            String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                Log.d("cloudLogin", TAG + "> Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = sServerAuthenticate.userSignUp(mContext, name, accountName, accountPassword, AccountBase.AUTHTOKEN_TYPE_FULL_ACCESS);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                }
                else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
