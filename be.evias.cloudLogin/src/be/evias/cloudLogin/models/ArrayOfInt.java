package be.evias.cloudLogin.models;

public class ArrayOfInt
    extends SimpleResult
{
	private int[] items;

	public void setItems(int[] items)
	{
		this.items = items;
	}

	public int[] getItems()
	{
		return items;
	}
}