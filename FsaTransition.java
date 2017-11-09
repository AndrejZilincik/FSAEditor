import java.util.HashSet;

public class FsaTransition implements Transition
{
    private State from;
    private State to;
    private String event;
    private HashSet<TransitionListener> listeners;

    public FsaTransition(State from, State to, String event)
    {
        this.from = from;
        this.to = to;
        this.event = event;
        this.listeners = new HashSet<>();
    }

    //Add a listener to this Transition
    public void addListener(TransitionListener tl)
    {
        listeners.add(tl);
    }

    //Remove a listener tfrom this Transition
    public void removeListener(TransitionListener tl)
    {
        listeners.remove(tl);
    }

    //Return the from-state of this transition
    public State fromState()
    {
        return from;
    }

    //Return the to-state of this transition
    public State toState()
    {
        return to;
    }

    //Return the name of the event that causes this transition
    public String eventName()
    {
        return event;
    }

    //Return a string containing information about this transition
    //in the form (without quotes, of course!):
    //"fromStateName(eventName)toStateName"
    public String toString()
    {
        String _event = (event == null) ? "" : event;
        return fromState().getName() + "(" + _event + ")" + toState().getName();
    }
}
