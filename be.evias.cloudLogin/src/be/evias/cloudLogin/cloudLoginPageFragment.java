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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Toast;
import android.text.TextUtils;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;

import be.evias.cloudLogin.models.User;

public class cloudLoginPageFragment
	extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static final int  SECTION_PROFILE    = 0;
    public static final int  SECTION_HISTORY    = 1;
    public static final int  SECTION_LOGOUT     = 2;

    protected User mCurrentUser;
    private SharedPreferences mPrefs;
    private Context           mContext;

    /**
     * Factory design pattern. Create a new instace of the
     * fragment corresponding to 'sectionNumber' or initialize
     * empty simple page fragment.
     **/
    public static cloudLoginPageFragment createPage(int sectionNumber, User currentUser)
    {
        /* Factory : create correct fragment according to sectionNumber. */
        cloudLoginPageFragment fragment;
        switch (sectionNumber) {
            default :
                fragment = new cloudLoginPageFragment();
                break;

            case SECTION_PROFILE:
                fragment = new FragmentProfile();
                break;
        }

        fragment.mCurrentUser = currentUser;

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public cloudLoginPageFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((cloudLoginMainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        mContext = activity.getBaseContext();
        mPrefs   = activity.getBaseContext()
                           .getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
    }

    public User getCurrentUser()
    {
        return mCurrentUser;
    }

    protected void showMessage(final String msg, final int duration)
    {
        if (TextUtils.isEmpty(msg))
            return;

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(mContext, msg, duration).show();
            }
        });
    }
}
