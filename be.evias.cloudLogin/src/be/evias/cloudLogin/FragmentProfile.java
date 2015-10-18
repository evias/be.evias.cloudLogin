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
import android.view.ContextThemeWrapper;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.content.Context;
import android.util.Log;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.evias.cloudLogin.models.User;

public class FragmentProfile
    extends cloudLoginPageFragment
{
    private SharedPreferences mPrefs;
    private Context           mContext;

    public FragmentProfile()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getBaseContext();
        mPrefs   = getActivity().getBaseContext()
                              .getSharedPreferences("cloudlogin", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContext = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(mContext);
        View rootView = localInflater.inflate(R.layout.fragment_profile, container, false);

        TextView email_field = (TextView) rootView.findViewById(R.id.profileEmail);
        TextView name_field  = (TextView) rootView.findViewById(R.id.profileName);

        email_field.setText(getCurrentUser().getEmail());
        name_field.setText(getCurrentUser().getName());

        return rootView;
    }
}
