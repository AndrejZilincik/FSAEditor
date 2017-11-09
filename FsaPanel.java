import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FsaPanel extends JPanel implements FsaListener
{
    private FsaImpl fsa;
    private HashSet<State> states;
    private HashSet<Transition> transitions;
    public PanelState panelState = new IdlePanelState(this);
    private boolean drawSelectionRectangle = false;
    private int selRectX;
    private int selRectY;
    private int selRectWidth;
    private int selRectHeight;

    public FsaPanel()
    {
        // Set display properties
        this.setLayout(null);
        this.setBackground(Color.GRAY);

        // Initialise FSA and state set
        fsa = new FsaImpl();
        states = new HashSet<>();
        transitions = new HashSet<>();

        // Add mouse event listeners
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseMotionListener);
    }

    // Gets the Fsa used by the panel
    public Fsa getFsa()
    {
        return fsa;
    }
    // Sets the Fsa used by the panel
    public void setFsa(FsaImpl fsa)
    {
        // Clear panel
        this.removeAll();
        this.repaint();

        // Update Fsa, add listener
        this.fsa = fsa;
        fsa.addListener(this);
    }

    @Override
    public void statesChanged()
    {
        // Add missing states
        for (State state : fsa.getStates())
        {
            if (!states.contains(state))
            {
                states.add(state);
                addStateIcon(state);
            }
        }

        this.repaint();
    }

    @Override
    public void transitionsChanged()
    {
        // Add missing transitions
        for (State state : fsa.getStates())
        {
            for (Transition transition : state.transitionsFrom())
            {
                if (!transitions.contains(transition))
                {
                    transitions.add(transition);
                    addTransitionIcon(transition);
                }
            }
            for (Transition transition : state.transitionsTo())
            {
                if (!transitions.contains(transition))
                {
                    transitions.add(transition);
                    addTransitionIcon(transition);
                }
            }
        }

        this.repaint();
    }

    @Override
    public void otherChanged()
    {

    }

    // Add an icon to the panel representing the given state
    private void addStateIcon(State state)
    {
        // Create state icon
        StateIcon icon = new StateIcon(state);

        // Add mouse event handlers
        icon.addMouseListener(mouseListener);
        icon.addMouseMotionListener(mouseMotionListener);

        // Add icon to panel
        this.add(icon);
    }

    // Add an icon to the panel representing the given transition
    private void addTransitionIcon(Transition transition)
    {
        // Create transition icon
        TransitionIcon icon = new TransitionIcon(transition);

        // Add icon to panel
        this.add(icon);
    }

    // Returns all of the state icons inside the panel
    public HashSet<StateIcon> getStateIcons()
    {
        HashSet<StateIcon> icons = new HashSet<>();

        for (Component component : this.getComponents())
        {
            if (component instanceof StateIcon)
            {
                icons.add((StateIcon)component);
            }
        }

        return icons;
    }

    // Returns all of the transition icons inside the panel
    public HashSet<TransitionIcon> getTransitionIcons()
    {
        HashSet<TransitionIcon> icons = new HashSet<>();

        for (Component component : this.getComponents())
        {
            if (component instanceof TransitionIcon)
            {
                icons.add((TransitionIcon)component);
            }
        }

        return icons;
    }

    // Returns all currently selected states
    public Set<State> getSelectedStates()
    {
        return getStateIcons().stream().filter(i -> i.isSelected).map(i -> i.state).collect(Collectors.toSet());
    }

    // Set/unset all selected states as initial
    public void setSelectedInitial(boolean value)
    {
        for (State state : getSelectedStates())
        {
            state.setInitial(value);
        }
    }

    // Set/unset all selected states as final
    public void setSelectedFinal(boolean value)
    {
        for (State state : getSelectedStates())
        {
            state.setFinal(value);
        }
    }

    // Delete selected states
    public void deleteSelected()
    {
        for (StateIcon icon : getStateIcons())
        {
            if (icon.isSelected)
            {
                // Delete attached transition icons
                for (TransitionIcon transIcon : getTransitionIcons())
                {
                    if (icon.state.transitionsTo().contains(transIcon.transition) ||
                        icon.state.transitionsFrom().contains(transIcon.transition))
                    {
                        this.remove(transIcon);
                    }
                }

                // Delete state and state icon
                fsa.removeState(icon.state);
                this.remove(icon);
            }
        }
    }

    // Make selection rectangle visible and set its position and size
    public void showSelectionRectangle(int x, int y, int width, int height)
    {
        drawSelectionRectangle = true;
        selRectX = x;
        selRectY = y;
        selRectWidth = width;
        selRectHeight = height;
    }

    // Make selection rectangle hidden
    public void hideSelectionRectangle()
    {
        drawSelectionRectangle = false;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (drawSelectionRectangle)
        {
            // Draw selection rectangle
            g.setColor(Color.DARK_GRAY);
            g.drawRect(selRectX, selRectY, selRectWidth, selRectHeight);

            // Select state icons within rectangle, deselect icons outside rectangle
            for (StateIcon icon : getStateIcons())
            {
                icon.isSelected = icon.getBounds().intersects(selRectX, selRectY, selRectWidth, selRectHeight);
            }
        }
    }

    // Reset FSA to initial state
    public void resetFsa()
    {
        fsa.reset();
        FsaEditor.setRecognised(fsa.isRecognised());
    }

    // Take a step in the simulation
    public void stepFsa(String event)
    {
        fsa.step(event);
        FsaEditor.setRecognised(fsa.isRecognised());
    }

    // Define mouse listener
    MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e)
        {

        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            panelState.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            panelState.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {

        }

        @Override
        public void mouseExited(MouseEvent e)
        {

        }
    };

    // Define mouse motion listener
    MouseMotionListener mouseMotionListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e)
        {
            panelState.mouseDragged(e);
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            panelState.mouseMoved(e);
        }
    };
}