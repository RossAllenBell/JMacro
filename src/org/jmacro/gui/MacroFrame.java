package org.jmacro.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.jmacro.Macro;
import org.jmacro.MacroMain;

public class MacroFrame extends JFrame implements ActionListener, WindowFocusListener{
    
    private static final long serialVersionUID = 1L;
    
    private static final String windowName = "JMacro";
    
    private MacroFramePanel macroFramePanel;
    
    public MacroFrame(){
        super(windowName + ' ' + MacroMain.version);
        
        URL imageURL = MacroFrame.class.getResource("/images/myicon.png");
        this.setIconImage(new ImageIcon(imageURL).getImage());
        
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        JMenuItem saveItem = new JMenuItem("Save As");
        saveItem.setMnemonic('S');
        saveItem.setActionCommand("menuSave");
        saveItem.addActionListener(this);
        file.add(saveItem); 
        JMenuItem openItem = new JMenuItem("Open");
        openItem.setMnemonic('O');
        openItem.setActionCommand("menuOpen");
        openItem.addActionListener(this);
        file.add(openItem); 
        file.addSeparator();
        JMenuItem IPAddress = new JMenuItem("IP Address");
        IPAddress.setMnemonic('I');
        IPAddress.setActionCommand("menuIPAddress");
        IPAddress.addActionListener(this);
        file.add(IPAddress); 
        file.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.setActionCommand("menuExit");
        exitItem.addActionListener(this);
        file.add(exitItem);
        
        JMenu help = new JMenu("About");
        help.setMnemonic('A');
//        JMenuItem openReadme = new JMenuItem("Open local readme.html...");
//        openReadme.setMnemonic('R');
//        openReadme.setActionCommand("openReadme");
//        openReadme.addActionListener(this);
//        help.add(openReadme);
        JMenuItem openJMacroDotOrg = new JMenuItem("JMacro.org...");
        openJMacroDotOrg.setMnemonic('J');
        openJMacroDotOrg.setActionCommand("openJMacroDotOrg");
        openJMacroDotOrg.addActionListener(this);
        help.add(openJMacroDotOrg);
        
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(file);
        bar.add(help);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.macroFramePanel = new MacroFramePanel();
        this.getContentPane().add(this.macroFramePanel);
        this.setSize(new Dimension(500,600));
        this.setLocation(50, 50);
        this.setVisible(true);
        this.addWindowFocusListener(this);
    }
    
    public JTextArea getOutputJTextArea(){
        return this.macroFramePanel.getOutputJTextArea();
    }
    
    public JPanel getMouseCoordsPanel(){
        return this.macroFramePanel.getMouseCoordsPanel();
    }
    
    public void setStartStopButtonText(String aLabel){
        this.macroFramePanel.setStartStopButtonText(aLabel);
    }
    
    public String[] getArgs(){
        return this.macroFramePanel.getArgs();
    }
    
    public void clearOutput(){
        this.macroFramePanel.clearOutput();
    }
    
    public JButton getPauseResumeButton(){
        return this.macroFramePanel.getPauseResumeButton();
    }
    
    public void setMacroInput(String aMacroString){
        this.macroFramePanel.setMacroInput(aMacroString);
    }
    
    @SuppressWarnings("unused")
    @Override
    public void windowGainedFocus(WindowEvent e) {
        MacroMain.windowGainedFocus();
    }
    
    @SuppressWarnings("unused")
    @Override
    public void windowLostFocus(WindowEvent e) {/*DO NOTHING*/}
    
    @Override
    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();        
        if(actionCommand.equalsIgnoreCase("menuExit")){
            System.exit(0);
        } else if(actionCommand.equalsIgnoreCase("openJMacroDotOrg")){
            try{
                java.awt.Desktop.getDesktop().browse(new URI("http://jmacro.org"));
            } catch(Exception ex) {
                clearOutput();
                System.err.println(ex.toString());
                System.err.println("Could not open http://JMacro.org");
            }
        } else if(actionCommand.equalsIgnoreCase("menuSave")){
            MacroMain.saveScript();
        } else if(actionCommand.equalsIgnoreCase("menuOpen")){
            MacroMain.openScript();
        } else if(actionCommand.equalsIgnoreCase("menuIPAddress")){
            try{
                InetAddress thisIp = InetAddress.getLocalHost();
                System.out.println("Local IP Address: " + thisIp.getHostAddress());
            } catch (UnknownHostException e2) {
                e2.printStackTrace();
            }
        }        
    }
    
    protected class MacroFramePanel extends JPanel implements ActionListener{
        
        static final long serialVersionUID = 2L;
        
        private JCheckBox loopToggle;
        
        private JTextField eventDelay;
        
        private JTextField initialDelay;
        
        private JPanel mouseCoords;
        
        private JTextArea macroInput;
        
        private JTextArea applicationOutput;
        
        private JButton startStopButton;
        
        private JButton pauseResumeButton;
        
        public MacroFramePanel(){
            this.setLayout(new GridBagLayout());
            GridBagConstraints c;
            Dimension dimension;
            
            this.loopToggle = new JCheckBox("Loop");            
            
            this.eventDelay = new JTextField(String.valueOf(Macro.defaultPauseBetweenEvents));
            this.eventDelay.setHorizontalAlignment(SwingConstants.RIGHT);
            dimension = new Dimension(50,20);
            this.eventDelay.setSize(dimension);
            this.eventDelay.setMaximumSize(dimension);
            this.eventDelay.setMinimumSize(dimension);
            this.eventDelay.setPreferredSize(dimension);
            
            this.initialDelay = new JTextField(String.valueOf(MacroMain.initialGUIDelay));
            this.initialDelay.setHorizontalAlignment(SwingConstants.RIGHT);
            dimension = new Dimension(50,20);
            this.initialDelay.setSize(dimension);
            this.initialDelay.setMaximumSize(dimension);
            this.initialDelay.setMinimumSize(dimension);
            this.initialDelay.setPreferredSize(dimension);
            
            this.mouseCoords = new JPanel();
            
            this.macroInput = new JTextArea();
            this.macroInput.setFont(new Font("Monospaced",Font.PLAIN, 16));
            this.macroInput.setTabSize(4);
            
            JScrollPane macroInputScrollPane = new JScrollPane(this.macroInput);
            macroInputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            macroInputScrollPane.setPreferredSize(new Dimension(100,100));
            
            this.applicationOutput = new JTextArea();
            this.applicationOutput.setFont(new Font("Monospaced",Font.PLAIN, 16));
            this.applicationOutput.setEditable(false);
            this.applicationOutput.setBackground(Color.LIGHT_GRAY);
            this.applicationOutput.setTabSize(4);
            
            JScrollPane applicationOutputScrollPane = new JScrollPane(this.applicationOutput);
            applicationOutputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            applicationOutputScrollPane.setPreferredSize(new Dimension(100,100));
            
            this.startStopButton = new JButton("Start");
            dimension = new Dimension(100,30);
            this.startStopButton.setSize(dimension);
            this.startStopButton.setMaximumSize(dimension);
            this.startStopButton.setMinimumSize(dimension);
            this.startStopButton.setPreferredSize(dimension);
            this.startStopButton.setActionCommand("startStopButton");
            this.startStopButton.addActionListener(this);
            
            this.pauseResumeButton = new JButton("Pause");
            this.pauseResumeButton.setEnabled(false);
            dimension = new Dimension(100,30);
            this.pauseResumeButton.setSize(dimension);
            this.pauseResumeButton.setMaximumSize(dimension);
            this.pauseResumeButton.setMinimumSize(dimension);
            this.pauseResumeButton.setPreferredSize(dimension);
            this.pauseResumeButton.setActionCommand("pauseResumeButton");
            this.pauseResumeButton.addActionListener(this);
            
            JPanel topPanel = new JPanel(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.WEST;
            topPanel.add(this.loopToggle,c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.EAST;
            topPanel.add(new JLabel("Event Delay "),c);
            c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.WEST;
            topPanel.add(this.eventDelay,c);
            c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = 0;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.EAST;
            topPanel.add(new JLabel("Initial Start Delay "),c);
            c = new GridBagConstraints();
            c.gridx = 4;
            c.gridy = 0;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.WEST;
            topPanel.add(this.initialDelay,c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = 0.0;
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(topPanel,c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 0.75;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.NORTHWEST;
            this.add(macroInputScrollPane,c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 0.25;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.NORTHWEST;
            this.add(applicationOutputScrollPane,c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.weighty = 0.0;
            this.add(this.startStopButton,c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.weighty = 0.0;
            c.anchor = GridBagConstraints.EAST;
            this.add(this.mouseCoords,c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.weighty = 0.0;
            c.anchor = GridBagConstraints.WEST;
            this.add(this.pauseResumeButton,c);
            
        }
        
        public JTextArea getOutputJTextArea(){
            return this.applicationOutput;
        }
        
        public JPanel getMouseCoordsPanel(){
            return this.mouseCoords;
        }
        
        public void setStartStopButtonText(String aLabel){
            this.startStopButton.setText(aLabel);
        }
        
        public String[] getArgs(){
            String[] rVal = new String[4];
            
            rVal[0] = "loop=" + String.valueOf(this.loopToggle.isSelected());
            
            rVal[1] = "eventDelay=" + this.eventDelay.getText();
            
            rVal[2] = "initialDelay=" + this.initialDelay.getText();
            
            rVal[3] = "macroString=" + this.macroInput.getText();
            
            return rVal;
        }
        
        public void clearOutput(){
            this.applicationOutput.setText("");
        }
        
        public JButton getPauseResumeButton(){
            return this.pauseResumeButton;
        }
        
        public void setMacroInput(String aMacroString){
            this.macroInput.setText(aMacroString);
        }
        
        @Override
        public void actionPerformed(ActionEvent e){
            String actionCommand = e.getActionCommand();        
            if(actionCommand.equalsIgnoreCase("startStopButton")){
                MacroMain.startStopButtonPressed();
            } else if(actionCommand.equalsIgnoreCase("pauseResumeButton")){
                MacroMain.pauseResumeButtonPressed();
            }
        }
        
    }
    
}
