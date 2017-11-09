import java.util.HashSet;

public class FsaState implements State
{
    private String name;
    private HashSet<Transition> from;
    private HashSet<Transition> to;

    private int xPos;
    private int yPos;

    private boolean initial;
    private boolean final_;
    private boolean current;

    private HashSet<StateListener> listeners;

    public FsaState(String name, HashSet<Transition> from, HashSet<Transition> to, int x, int y)
    {
        this.name = name;
        this.from = from;
        this.to = to;
        this.xPos = x;
        this.yPos = y;
        this.listeners = new HashSet<>();
    }

    //Add a listener to this state
    public void addListener(StateListener sl)
    {
        listeners.add(sl);
    }

    //Add a listener to this state
    public void removeListener(StateListener sl)
    {
        listeners.remove(sl);
    }

    //Return a set containing all transitions FROM this state
    public HashSet<Transition> transitionsFrom()
    {
        return from;
    }

    //Return a set containing all transitions TO this state
    public HashSet<Transition> transitionsTo()
    {
        return to;
    }

    //Move the position of this state
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy)
    {
        // Update position
        xPos += dx;
        yPos += dy;

        // Notify state listeners
        for (StateListener sl : listeners)
        {
            sl.StateHasChanged();
        }
    }

    //Return a string containing information about this state
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)jk"
    //where j is 1/0 if this state is/is-not an initial state
    //where k is 1/0 if this state is/is-not a final state
    public String toString()
    {
        String output = getName() + "(" + getXpos() + "," + getYpos() + ")";
        output += isInitial() ? "1" : "0";
        output += isFinal() ? "1" : "0";
        return output;
    }

    //Return the name of this state
    public String getName()
    {
        return name;
    }

    //Return the X position of this state
    public int getXpos()
    {
        return xPos;
    }

    //Return the Y position of this state
    public int getYpos()
    {
        return yPos;
    }

    //Set/clear this state as an initial state
    public void setInitial(boolean b)
    {
        initial = b;

        // Notify state listeners
        for (StateListener sl : listeners)
        {
            sl.StateHasChanged();
        }
    }

    //Indicate if this is an initial state
    public boolean isInitial()
    {
        return initial;
    }

    //Set/clear this state as a final state
    public void setFinal(boolean b)
    {
        final_ = b;

        // Notify state listeners
        for (StateListener sl : listeners)
        {
            sl.StateHasChanged();
        }
    }

    //Indicate if this is a final state
    public boolean isFinal()
    {
        return final_;
    }

    // Set/clear this state as a current state
    public void setCurrent(boolean b)
    {
        current = b;

        // Notify state listeners
        for (StateListener sl : listeners)
        {
            sl.StateHasChanged();
        }
    }

    //Indicate if this is a current state
    public boolean isCurrent()
    {
        return current;
    }
}
