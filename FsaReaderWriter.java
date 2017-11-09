import java.io.*;

class FsaReaderWriter implements FsaIo {
    public FsaReaderWriter()
    {

    }

    //Read the description of a finite-state automaton from the
    //Reader , r, and transfer it to Fsa, f.
    //If an error is detected, throw an exception that indicates the line
    //where the error was detected, and has a suitable text message
    public void read(Reader r, Fsa f)
            throws IOException, FsaFormatException
    {
        LineNumberReader lnr = new LineNumberReader(r);
        String line = lnr.readLine();
        while (line != null)
        {
            // Ignore empty lines and comments
            if (line.length() > 0 && line.charAt(0) != '#')
            {
                // Split line into words
                String[] words = line.split(" ");
                if (words.length > 0)
                {
                    // State record
                    if (words[0].equals("state"))
                    {
                        // If invalid word count, throw exception
                        if (words.length != 4)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid state record syntax!");
                        }

                        // Try to add state to FSA
                        try
                        {
                            f.newState(words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]));
                        }
                        // If syntax is invalid, throw exception
                        catch (IllegalArgumentException ex)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid state record arguments!");
                        }
                    }
                    // Transition record
                    else if (words[0].equals("transition"))
                    {
                        // If invalid word count, throw exception
                        if (words.length != 4)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid transition record syntax!");
                        }

                        try
                        {
                            // Handle epsilon transitions
                            String eventName = words[2].equals("?") ? null : words[2];

                            // Ensure FSA contains specified from and to states
                            State fromState = f.findState(words[1]);
                            State toState = f.findState(words[3]);
                            if (fromState == null || toState == null)
                            {
                                // State not found, throw exception
                                throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid transition record syntax!");
                            }

                            // Add transition to FSA
                            f.newTransition(fromState, toState, eventName);
                        }
                        catch (IllegalArgumentException ex)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid transition record arguments!");
                        }
                    }
                    // Initial record
                    else if (words[0].equals("initial"))
                    {
                        // If invalid word count, throw exception
                        if (words.length != 2)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid initial record syntax!");
                        }

                        // Ensure FSA contains specified state
                        State state = f.findState(words[1]);
                        if (state == null)
                        {
                            // State not found, throw exception
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": State not found in FSA!");
                        }
                        else
                        {
                            // Mark state as initial state
                            state.setInitial(true);
                        }
                    }
                    // Final record
                    else if (words[0].equals("final"))
                    {
                        // If invalid word count, throw exception
                        if (words.length != 2)
                        {
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid final record syntax!");
                        }

                        // Ensure FSA contains specified state
                        State state = f.findState(words[1]);
                        if (state == null)
                        {
                            // State not found, throw exception
                            throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": State not found in FSA!");
                        }
                        else
                        {
                            // Mark state as final state
                            state.setFinal(true);
                        }
                    }
                    else
                    {
                        throw new FsaFormatException(lnr.getLineNumber(), "Line " + lnr.getLineNumber() + ": Invalid record!");
                    }
                }
            }
            line = lnr.readLine();
        }

        lnr.close();
    }


    //Write a representation of the Fsa, f, to the Writer, w.
    public void write(Writer w, Fsa f)
            throws IOException
    {
        for (State s : f.getStates())
        {
            // Write state records
            w.write("state" + " " + s.getName() + " " + s.getXpos() + " " + s.getYpos() + '\n');
        }

        for (State s : f.getStates())
        {
            // Write transition records
            for (State dest : f.getStates())
            {
                for (Transition t : f.findTransition(s, dest))
                {
                    String event = t.eventName() != null ? t.eventName() : "?";
                    w.write("transition" + " " + t.fromState().getName() + " " + event + " " + t.toState().getName() + '\n');
                }
            }

            // Write initial records
            if (s.isInitial())
            {
                w.write("initial" + " " + s.getName() + '\n');
            }

            // Write final records
            if (s.isFinal())
            {
                w.write("final" + " " + s.getName() + '\n');
            }
        }
    }
}