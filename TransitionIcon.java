import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class TransitionIcon extends JComponent implements TransitionListener
{
    public Transition transition;
    private State fromState;
    private State toState;

    public TransitionIcon(Transition transition)
    {
        this.transition = transition;
        this.fromState = transition.fromState();
        this.toState = transition.toState();
        this.setBounds(0, 0, 1000, 1000);

        // Associate transition with transition icon
        transition.addListener(this);
    }

    @Override
    public void TransitionHasChanged()
    {
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Set position
        this.setBounds(0, 0, 1000, 1000);

        // Draw transition arc
        int size = StateIcon.size;
        double x1 = fromState.getXpos() + size / 2;
        double y1 = fromState.getYpos() + size / 2;
        double x2 = toState.getXpos() + size / 2;
        double y2 = toState.getYpos() + size / 2;
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;
        double xc = xm + (ym - y1) / 2;
        double yc = ym + (x1 - xm) / 2;
        double xb1 = x1 + (size / 2) * (xc - x1) / Math.sqrt((xc - x1) * (xc - x1) + (yc - y1) * (yc - y1));
        double xb2 = x2 + (size / 2) * (xc - x2) / Math.sqrt((xc - x2) * (xc - x2) + (yc - y2) * (yc - y2));
        double yb1 = y1 + (size / 2) * (yc - y1) / Math.sqrt((xc - x1) * (xc - x1) + (yc - y1) * (yc - y1));
        double yb2 = y2 + (size / 2) * (yc - y2) / Math.sqrt((xc - x2) * (xc - x2) + (yc - y2) * (yc - y2));
        QuadCurve2D curve = new QuadCurve2D.Double(xb1, yb1, xc, yc, xb2, yb2);
        ((Graphics2D)g).draw(curve);

        // Draw arrow
        g.drawLine((int)xb2, (int)yb2, (int)((xb1 + 7 * xb2) / 8), (int)((yb1 + 7 * yb2) / 8));
        double xd1 = xc + (xc - xb1) / 3;
        double yd1 = yc + (yc - yb1) / 3;
        g.drawLine((int)xb2, (int)yb2, (int)((xd1 + 3 * xb2) / 4), (int)((yd1 + 3 * yb2) / 4));

        // Draw transition name
        String event = (transition.eventName() == null) ? "eps" : transition.eventName();
        g.drawString(event, (int)(xc + xm) / 2, (int)(yc + ym) / 2);
    }
}