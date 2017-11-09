import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FsaEditor
{
    private static JFrame frame;
    private static FsaPanel mainPanel;
    private static JLabel recognisedIndicator;
    private static JTextField eventInput;

    public static void main(String[] args)
    {
        initUI();
    }

    private static void initUI()
    {
        // Initialise window
        frame = new JFrame();
        frame.setTitle("FSA Editor v0.1");
        frame.setBounds(200, 200, 800, 600);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialise menu bar
        JMenuBar menuBar = new JMenuBar();
        frame.add(menuBar, BorderLayout.PAGE_START);

        // Initialise menu item click event listener
        MenuItemClickListener micl = new MenuItemClickListener();

        // Initialise file menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Initialise file menu items
        createMenuItem(fileMenu, "Open...", 'O', micl);
        createMenuItem(fileMenu, "Save As...", 'S', micl);
        createMenuItem(fileMenu, "Quit", 'Q', micl);

        // Initialise edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        // Initialise edit menu items
        createMenuItem(editMenu, "New State", ' ', micl);
        createMenuItem(editMenu, "New Transition", ' ', micl);
        createMenuItem(editMenu, "Set Initial", ' ', micl);
        createMenuItem(editMenu, "Unset Initial", ' ', micl);
        createMenuItem(editMenu, "Set Final", ' ', micl);
        createMenuItem(editMenu, "Unset Final", ' ', micl);
        createMenuItem(editMenu, "Delete", ' ', micl);

        // Initialise main panel
        mainPanel = new FsaPanel();
        frame.add(mainPanel, BorderLayout.CENTER);

        // Initialise control bar
        JPanel controlPanel = new JPanel();
        frame.add(controlPanel, BorderLayout.PAGE_END);

        // Initialise control bar elements
        ButtonClickListener bcl = new ButtonClickListener();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(bcl);
        controlPanel.add(resetButton);
        JLabel eventLabel = new JLabel("             Event: ");
        controlPanel.add(eventLabel);
        eventInput = new JTextField("Input event name...");
        controlPanel.add(eventInput);
        JLabel separator = new JLabel("   ");
        controlPanel.add(separator);
        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(bcl);
        controlPanel.add(stepButton);
        JLabel recognisedLabel = new JLabel("             Recognised: ");
        controlPanel.add(recognisedLabel);
        recognisedIndicator = new JLabel("o");
        recognisedIndicator.setForeground(Color.RED);
        controlPanel.add(recognisedIndicator);

        // Display window
        frame.setVisible(true);
    }

    // Initialise menu item
    private static void createMenuItem(JMenu parent, String name, char shortcutKey, ActionListener listener)
    {
        JMenuItem menuItem = new JMenuItem(name);
        if (shortcutKey != ' ')
        {
            // Set keyboard shortcut
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcutKey, ActionEvent.CTRL_MASK));
        }
        if (listener != null)
        {
            // Set event handler
            menuItem.addActionListener(listener);
        }

        // Add menu item to menu
        parent.add(menuItem);
    }

    // Open FSA file and read data from it
    public static void openFsa()
    {
        // Prompt user for filename
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("FSA files (*.fsa)", "fsa");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showOpenDialog(frame);

        // Cancelled by user
        if (result != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        // Read FSA from file
        try (FileReader fr = new FileReader(fileChooser.getSelectedFile()))
        {
            // Try to read FSA
            FsaReaderWriter rw = new FsaReaderWriter();
            mainPanel.setFsa(new FsaImpl());
            rw.read(fr, mainPanel.getFsa());
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());

            // Clear FSA
            mainPanel.setFsa(new FsaImpl());
        }
    }

    // Save FSA data to a file
    public static void saveFsa()
    {
        // Prompt user for filename
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("FSA files (*.fsa)", "fsa");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showSaveDialog(frame);

        // Cancelled by user
        if (result != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        // Save FSA to file
        try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile()))
        {
            // Try to save FSA
            FsaReaderWriter rw = new FsaReaderWriter();
            rw.write(fw, mainPanel.getFsa());
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    // Add a state to the FSA
    public static void newState()
    {
        String stateName = JOptionPane.showInputDialog("Enter a state name: ");
        if (stateName == null)
        {
            return;
        }

        try
        {
            mainPanel.panelState = new AddingPanelState(mainPanel, stateName);
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    // Add a transition to the FSA
    public static void newTransition()
    {
        String transitionName = JOptionPane.showInputDialog("Enter a transition name: ");
        if (transitionName == null)
        {
            return;
        }

        try
        {
            // TODO: Implement adding transitions
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public static FsaPanel getMainPanel()
    {
        return mainPanel;
    }

    // Get typed event name
    public static String getInputEventName()
    {
        return eventInput.getText();
    }

    // Set value of recognised indicator
    public static void setRecognised(boolean value)
    {
//        recognisedIndicator.setText(value ? "YES" : "NO ");
        recognisedIndicator.setForeground(value ? Color.GREEN : Color.RED);
    }
}

// Click event handler for menu items
class MenuItemClickListener implements ActionListener
{
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        try
        {
            if (command.equals("Quit"))
            {
                System.exit(0);
            }
            else if (command.equals("Open..."))
            {
                FsaEditor.openFsa();
            }
            else if (command.equals("Save As..."))
            {
                FsaEditor.saveFsa();
            }
            else if (command.equals("Set Initial"))
            {
                FsaEditor.getMainPanel().setSelectedInitial(true);
            }
            else if (command.equals("Unset Initial"))
            {
                FsaEditor.getMainPanel().setSelectedInitial(false);
            }
            else if (command.equals("Set Final"))
            {
                FsaEditor.getMainPanel().setSelectedFinal(true);
            }
            else if (command.equals("Unset Final"))
            {
                FsaEditor.getMainPanel().setSelectedFinal(false);
            }
            else if (command.equals("New State"))
            {
                FsaEditor.newState();
            }
            else if (command.equals("Delete"))
            {
                FsaEditor.getMainPanel().deleteSelected();
            }
            else if (command.equals("New Transition"))
            {
                FsaEditor.newTransition();
            }
            else
            {
                System.out.println(command);
                JOptionPane.showMessageDialog(null, "Unknown command: " + command);
            }
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}

class ButtonClickListener implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        try
        {
            if (command.equals("Reset"))
            {
                FsaEditor.getMainPanel().resetFsa();
            }
            else if (command.equals("Step"))
            {
                FsaEditor.getMainPanel().stepFsa(FsaEditor.getInputEventName());
            }
            else
            {
                System.out.println(command);
                JOptionPane.showMessageDialog(null, "Unknown command: " + command);
            }
        }
        catch (Exception ex)
        {
            // Display error message
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}