package be.evias.cloudLogin.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class cloudLoginAuthenticatorService
	extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        cloudLoginAuthenticator authenticator = new cloudLoginAuthenticator(this);
        return authenticator.getIBinder();
    }
}
