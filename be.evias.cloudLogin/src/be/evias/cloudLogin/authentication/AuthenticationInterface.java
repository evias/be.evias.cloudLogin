package be.evias.cloudLogin.authentication;

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
