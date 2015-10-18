package be.evias.cloudLogin.models;

import java.io.Serializable;

public class SimpleResult
    implements Serializable
{
	protected Boolean result;
    protected int     id;

    public Boolean getResult()
    {
        return result;
    }

    public void setResult(Boolean result)
    {
        this.result = result;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
