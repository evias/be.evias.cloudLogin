package be.evias.cloudLogin.authentication;

import android.content.Context;
import be.evias.cloudLogin.models.User;

public interface AuthenticationInterface
{
    public String userSignUp(final Context context, final String name, final String email, final String pass, String authType)
    	throws Exception;

    public String userSignIn(final Context context, final String user, final String pass, String authType)
    	throws Exception;

    public String userSignOut(final Context context, final String user)
    	throws Exception;

    public User getUserObject(final Context context, final String name)
        throws Exception;
}
