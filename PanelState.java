import java.awt.event.MouseEvent;

public interface PanelState
{
    public void mousePressed(MouseEvent event);
    public void mouseDragged(MouseEvent event);
    public void mouseReleased(MouseEvent event);
    public void mouseMoved(MouseEvent event);
}