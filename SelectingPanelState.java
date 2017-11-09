import java.awt.event.MouseEvent;

public class SelectingPanelState implements PanelState
{
    private FsaPanel panel;
    private int initX;
    private int initY;

    public SelectingPanelState(FsaPanel panel, int initX, int initY)
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
        // Calculate selection rectangle bounds
        int currX = event.getX();
        int currY = event.getY();
        int minX = initX < currX ? initX : currX;
        int minY = initY < currY ? initY : currY;
        int maxX = initX > currX ? initX : currX;
        int maxY = initY > currY ? initY : currY;

        // Display selection rectangle
        panel.showSelectionRectangle(minX, minY, maxX - minX, maxY - minY);
        panel.repaint();
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
