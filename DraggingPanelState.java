import java.awt.event.MouseEvent;

public class DraggingPanelState implements PanelState
{
    private FsaPanel panel;
    private int initX;
    private int initY;

    public DraggingPanelState(FsaPanel panel, int initX, int initY)
    {
        this.panel = panel;
        this.initX = initX;
        this.initY = initY;
    }

    @Override
    public void mousePressed(MouseEvent event)
    {

    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        // Move all selected state icons
        for (State state : panel.getSelectedStates())
        {
            state.moveBy(event.getX() - initX, event.getY() - initY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        // Change to idle state
        panel.panelState = new IdlePanelState(panel);
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {

    }
}
