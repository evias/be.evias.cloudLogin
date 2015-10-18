package be.evias.cloudLogin.models;

import be.evias.cloudLogin.models.User;

public class ArrayOfUser
    extends SimpleResult
{
    private User[] results;

    public void setResults(User[] results)
    {
        this.results = results;
    }

    public User[] getResults()
    {
        return results;
    }
}