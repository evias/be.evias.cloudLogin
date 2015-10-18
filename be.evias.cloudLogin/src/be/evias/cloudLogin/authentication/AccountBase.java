package be.evias.cloudLogin.authentication;

public class AccountBase
{
    public static final String ACCOUNT_TYPE = "be.evias.cloudLogin";
    public static final String ACCOUNT_NAME = "cloudLogin Account";

    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an cloudLogin account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an cloudLogin account";

    public static final AuthenticationInterface sServerAuthenticate = new ParseComAPIClient();
}
