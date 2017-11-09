import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FsaImpl implements Fsa, FsaSim {
    private HashSet<State> stateSet;
    private HashSet<Transition> transitionSet;
    private HashSet<FsaListener> listeners;
    private boolean isActive;

    public FsaImpl()
    {
        // Initialise variables
        stateSet = new HashSet<>();
        transitionSet = new HashSet<>();
        listeners = new HashSet<>();
        isActive = false;
    }

    //Create a new State and add it to this FSA
    //Returns the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    public State newState(String name, int x, int y)
            throws IllegalArgumentException
    {
        // Check if name is valid
        if (name == null || name.length() < 1 || !Character.isLetter(name.charAt(0)))
        {
            // Name must start with a letter
            throw new IllegalArgumentException("Name must begin with a letter!");
        }
        for (char c : name.toCharArray())
        {
            // Remaining characters must be letters, digits, or underscores
            if (!Character.isLetterOrDigit(c) && c != '_')
            {
                throw new IllegalArgumentException("Only letters, digits and underscores allowed!");
            }
        }

        // Check for duplicate states
        for (State state : stateSet)
        {
            if (state.getName().equals(name))
            {
                throw new IllegalArgumentException("Duplicate state name!");
            }
        }

        // Create new state and add it to state set
        State state = new FsaState(name, new HashSet<>(), new HashSet<>(), x, y);
        stateSet.add(state);

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.statesChanged();
        }

        return state;
    }

    //Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s)
    {
        // Remove outgoing transitions
        for (Transition t : s.transitionsFrom())
        {
            // Remove from state on other side
            t.toState().transitionsTo().remove(t);

            // Remove transition from set
            transitionSet.remove(t);

            // Notify FSA listeners
            for (FsaListener fl : listeners)
            {
                fl.transitionsChanged();
            }
        }

        // Remove incoming transitions
        for (Transition t : s.transitionsTo())
        {
            // Remove from state on other side
            t.fromState().transitionsFrom().remove(t);

            // Remove transition from set
            transitionSet.remove(t);

            // Notify FSA listeners
            for (FsaListener fl : listeners)
            {
                fl.transitionsChanged();
            }
        }

        // Remove state
        stateSet.remove(s);

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.statesChanged();
        }
    }

    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName)
    {
        // Check if state set contains state, if so, return the state
        for (State state : stateSet)
        {
            if (state.getName().equals(stateName))
            {
                return state;
            }
        }

        // If not, return null
        return null;
    }

    //Return a set containing all the states in this Fsa
    public Set<State> getStates()
    {
        return stateSet;
    }

    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //eventName==null specifies an epsilon-transition
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist or
    //  The eventName is invalid or
    //  An identical transition already exists
    public Transition newTransition(State fromState, State toState, String eventName)
            throws IllegalArgumentException
    {
        // Ensure FSA contains the specified from and to states
        if (!stateSet.contains(fromState) || !stateSet.contains(toState))
        {
            throw new IllegalArgumentException("FSA does not contain the specified state!");
        }

        // Check whether event name is valid
        if (eventName != null)
        {
            if (eventName.length() < 1)
            {
                // Event name can not be empty string
                throw new IllegalArgumentException("Event name can not be empty!");
            }
            else
            {
                for (char c : eventName.toCharArray())
                {
                    // Event name must contain only letters
                    if (!Character.isLetter(c))
                    {
                        throw new IllegalArgumentException("Event name must only contain letters!");
                    }
                }
            }
        }

        // Check for duplicate transitions
        for (Transition t : transitionSet)
        {
            if (t.fromState().equals(fromState) &&
                    t.toState().equals(toState) &&
                    ((t.eventName() == null && eventName == null) || t.eventName().equals(eventName)))
            {
                throw new IllegalArgumentException("Duplicate transition!");
            }
        }

        // Create transition and add it to the transition set
        Transition t = new FsaTransition(fromState, toState, eventName);
        transitionSet.add(t);

        // Add transition to start and end state
        fromState.transitionsFrom().add(t);
        toState.transitionsTo().add(t);

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.transitionsChanged();
        }

        return t;
    }

    //Remove a transition from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t)
    {
        // Remove transition
        t.fromState().transitionsFrom().remove(t);
        t.toState().transitionsTo().remove(t);
        transitionSet.remove(t);

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.transitionsChanged();
        }
    }

    //Find all the transitions between two states
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist
    public Set<Transition> findTransition(State fromState, State toState)
    {
        // Check whether the FSA contains the specified from and to states
        if (!stateSet.contains(fromState) || !stateSet.contains(toState))
        {
            throw new IllegalArgumentException("FSA does not contain the specified state!");
        }

        // Filter and return matching transitions
        return transitionSet.stream()
                .filter(t -> t.fromState().equals(fromState) && t.toState().equals(toState))
                .collect(Collectors.toSet());
    }

    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates()
    {
        return stateSet.stream().filter(s -> s.isInitial()).collect(Collectors.toSet());
    }

    //Return the set of final states of this Fsa
    public Set<State> getFinalStates()
    {
        return stateSet.stream().filter(s -> s.isFinal()).collect(Collectors.toSet());
    }

    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates()
    {
        // Check if FSA is active
        if (!isActive)
        {
            return new HashSet<>();
        }

        return stateSet.stream().filter(s -> s.isCurrent()).collect(Collectors.toSet());
    }

    //Return a string describing this Fsa
    //Returns a string that contains (in this order):
    //for each state in the FSA, a line (terminated by \n) containing
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n) containing
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n) containing
    //  INITIAL followed the name of the state
    //for each final state in the FSA, a line (terminated by \n) containing
    //  FINAL followed the name of the state
    public String toString()
    {
        String output = "";
        for (State state : stateSet)
        {
            output += "STATE" + " " + state.toString() + "\n";
        }
        for (Transition transition : transitionSet)
        {
            output += "TRANSITION" + " " + transition.toString() + "\n";
        }
        for (State initialState : getInitialStates())
        {
            output += "INITIAL" + " " + initialState.getName() + "\n";
        }
        for (State finalState : getFinalStates())
        {
            output += "FINAL" + " " + finalState.getName() + "\n";
        }
        return output;
    }

    //Add a listener to this FSA
    public void addListener(FsaListener fl)
    {
        listeners.add(fl);
    }

    //Remove a listener from this FSA
    public void removeListener(FsaListener fl)
    {
        listeners.remove(fl);
    }

    //Reset the simulation to its initial state(s)
    public void reset()
    {
        // Activate FSA
        isActive = true;

        for (State s : stateSet)
        {
            // Set all initial states as current states and all non-initial states as non-current states
            if ((s.isInitial() && !s.isCurrent()) || (!s.isInitial() && s.isCurrent()))
            {
                ((FsaState)s).setCurrent(!s.isCurrent());
            }
        }

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.otherChanged();
        }
    }

    //Take one step in the simulation
    public void step(String event)
    {
        // Follow epsilon transitions
        followEpsilon();

        // Follow regular event transitions
        if (event != null)
        {
            Set<State> states = getCurrentStates();
            for (State s : states)
            {
                // Remove current states
                ((FsaState)s).setCurrent(false);
            }
            // For each state that was a current state
            for (State s : states)
            {
                // For each transition from the state
                for (Transition t : s.transitionsFrom())
                {
                    // If transition matches event name
                    if (t.eventName() != null && t.eventName().equals(event))
                    {
                        // Set destination state as current state
                        State dest = t.toState();
                        ((FsaState)dest).setCurrent(true);
                    }
                }
            }
        }

        // Notify FSA listeners
        for (FsaListener fl : listeners)
        {
            fl.otherChanged();
        }
    }

    // Follow epsilon transitions
    private void followEpsilon()
    {
        // Get list of current states
        Set<State> currentStates = getCurrentStates();
        while (!currentStates.isEmpty())
        {
            Set<State> newStates = new HashSet<>();

            // For each current state
            for (State s : currentStates)
            {
                // For each transition of current state
                for (Transition t : s.transitionsFrom())
                {
                    // If transition is epsilon transition
                    if (t.eventName() == null)
                    {
                        // Set destination state as current state
                        State dest = t.toState();
                        if (!dest.isCurrent())
                        {
                            ((FsaState)dest).setCurrent(true);
                            newStates.add(dest);
                        }
                    }
                }
            }

            // Repeat with newly added states
            currentStates = newStates;
        }
    }

    //Returns true if the simulation has recognised
    //the sequence of events it has been given
    public boolean isRecognised()
    {
        // Check if FSA is active
        if (!isActive)
        {
            return false;
        }

        // Follow epsilon transitions
        followEpsilon();

        // Check if any current state is also a final state
        return getCurrentStates().stream().anyMatch(s -> s.isFinal());
    }
}