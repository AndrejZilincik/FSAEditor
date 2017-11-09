import java.awt.event.MouseEvent;

public class IdlePanelState implements PanelState
{
    private FsaPanel panel;

    public IdlePanelState(FsaPanel panel)
    {
        this.panel = panel;
        panel.hideSelectionRectangle();
        panel.repaint();
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        if (event.getComponent() instanceof StateIcon)
        {
            StateIcon pressedIcon = ((StateIcon)event.getComponent());

            if (!pressedIcon.isSelected)
            {
                // Select selected icon and deselect all other icons
                for (StateIcon icon : panel.getStateIcons())
                {
                    icon.isSelected = icon == pressedIcon;
                    icon.repaint();
                }
            }

            // Change to dragging state
            panel.panelState = new DraggingPanelState(panel, event.getX(), event.getY());
        }
        else
        {
            // Deselect all state icons
            for (StateIcon icon : panel.getStateIcons())
            {
                icon.isSelected = false;
            }
            panel.repaint();

            // Change to selecting state
            panel.panelState = new SelectingPanelState(panel, event.getX(), event.getY());
        }
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

    }
}
