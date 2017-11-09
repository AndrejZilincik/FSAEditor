import javax.swing.*;
import java.awt.event.MouseEvent;

public class AddingPanelState implements PanelState
{
    private FsaPanel panel;
    private String stateName;
    private boolean stateAdded = false;
    private int prevX;
    private int prevY;
    private State state;

    public AddingPanelState(FsaPanel panel, String stateName)
    {
        this.panel = panel;
        this.stateName = stateName;
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        // Change to idle state
        panel.panelState = new IdlePanelState(panel);
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {

    }

    @Override
    public void mouseReleased(MouseEvent event)
    {

    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        try
        {
            if (!stateAdded)
            {
                // Deselect all state icons
                for (StateIcon icon : panel.getStateIcons())
                {
                    icon.isSelected = false;
                }
                panel.repaint();

                // Add state to FSA
                panel.getFsa().newState(stateName, event.getX(), event.getY());
                stateAdded = true;
                prevX = event.getXOnScreen();
                prevY = event.getYOnScreen();
                state = panel.getFsa().findState(stateName);

            }

            // Move state with cursor
            state.moveBy(event.getXOnScreen() - prevX, event.getYOnScreen() - prevY);
            prevX = event.getXOnScreen();
            prevY = event.getYOnScreen();
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());

            // Change to idle state
            panel.panelState = new IdlePanelState(panel);
        }
    }
}