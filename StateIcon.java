import javax.swing.*;
import java.awt.*;

public class StateIcon extends JComponent implements StateListener
{
    public static int size = 100;
    public static int border = 2;

    public State state;
    private int x;
    private int y;
    public boolean isSelected;

    public StateIcon(State state)
    {
        this.state = state;
        this.x = state.getXpos();
        this.y = state.getYpos();
        this.setBounds(x, y, size, size);

        // Associate state with state icon
        state.addListener(this);
    }

    @Override
    public void StateHasChanged()
    {
        x = state.getXpos();
        y = state.getYpos();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Set position
        this.setBounds(x, y, size, size);

        // Draw outer border
        g.setColor(Color.DARK_GRAY);
        g.fillOval(0, 0, size, size);

        // Draw circle
        g.setColor(isSelected ? Color.YELLOW : Color.LIGHT_GRAY);
        g.fillOval(2 * border, 2 * border, size - 4 * border, size - 4 * border);

        // Draw secondary border and circle for final states
        if (state.isFinal())
        {
            g.setColor(Color.DARK_GRAY);
            g.fillOval(4 * border, 4 * border, size - 8 * border, size - 8 * border);

            g.setColor(isSelected ? Color.YELLOW : Color.LIGHT_GRAY);
            g.fillOval(6 * border, 6 * border, size - 12 * border, size - 12 * border);
        }

        // Draw lightning bolt for initial states
        if (state.isInitial())
        {
            g.setColor(Color.YELLOW);
            g.drawLine(0, 0, 0, size / 8);
            g.drawLine(0, size / 8, size / 12, size / 12);
            g.drawLine(size / 12, size / 12, size / 12, size / 4);
            g.drawLine(size / 12, size / 4, size / 9, size / 6);
            g.drawLine(size / 12, size / 4, size / 18, size / 6);
        }

        // Draw state name
        g.setColor(Color.DARK_GRAY);
        g.drawString(state.getName(), 5 * size / 12, 5 * size / 12);

        // Draw current state indicator dot
        if (state.isCurrent())
        {
            g.fillOval(4 * size / 9, 5 * size / 9, size / 9, size / 9);
        }
    }
}