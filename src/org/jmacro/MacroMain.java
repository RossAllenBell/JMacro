
package org.jmacro;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jmacro.compiler.MacroParseException;
import org.jmacro.gui.JFileChooserOverwritePrompt;
import org.jmacro.gui.JTextAreaPrintStream;
import org.jmacro.gui.MacroFrame;
import org.jmacro.gui.MouseCoordWatcher;

public class MacroMain {

    public static final Pattern loopOverride = Pattern.compile("^\\/\\/OVERRIDE_LOOP (TRUE|FALSE)$", Pattern.CASE_INSENSITIVE);

    public static final Pattern initialDelayOverride = Pattern.compile("^\\/\\/OVERRIDE_INITIAL_DELAY (\\d+)$", Pattern.CASE_INSENSITIVE);

    public static final Pattern eventDelayOverride = Pattern.compile("^\\/\\/OVERRIDE_EVENT_DELAY (\\d+)$", Pattern.CASE_INSENSITIVE);

    private static String macroString = "";

    private static long initialDelay = 0L;

    public static long initialGUIDelay = 5000L;

    private static long eventDelay = 1000L;

    private static boolean loop = false;

    private static MacroFrame macroFrame;

    private static Macro currentMacro;

    private static Thread currentMacroThread;

    private static boolean graphicalMode = false;

    private static final String jmacroFileDescriotion = "JMacro script";
    private static final String jmacroFileExtension = "jmacro";

    public static final String version = "1.7.1";

    public static final int defaultServerPort = 10500;

//    private static HashMap<Integer, ServerSocket> serverSockets;
    
    public static Robot ROBOT;

    public static void main(String[] args) {
        try{
            ROBOT = new Robot();
        } catch (AWTException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        
        if (args.length != 0) {
            processArgs(args);
            startMacro();
        } else {
            graphicalMode = true;
            macroFrame = new MacroFrame();
            System.setErr(new JTextAreaPrintStream(macroFrame.getOutputJTextArea(), 2000, System.err));
            System.setOut(new JTextAreaPrintStream(macroFrame.getOutputJTextArea(), 2000, System.out));
            System.out.println("Welcome to JMacro!\n");
            Thread mouseCoordWatcher = new Thread(new MouseCoordWatcher(macroFrame.getMouseCoordsPanel()), "MouseCoordWatcher");
            mouseCoordWatcher.start();
        }
    }

    public static void startStopButtonPressed() {
        if (currentMacro != null && !currentMacro.isStopped()) {
            currentMacro.stopMacro();
            macroStopped();
        } else {
            macroFrame.clearOutput();
            String[] args = macroFrame.getArgs();
            if (processArgs(args)) {
                startMacro();
            }
        }
    }

    public static void pauseResumeButtonPressed() {
        boolean paused = currentMacro.togglePause();
        if (paused) {
            macroFrame.getPauseResumeButton().setText("Resume");
            macroFrame.getPauseResumeButton().setForeground(Color.GREEN);
            System.out.println("--PAUSED--");
        } else {
            macroFrame.getPauseResumeButton().setText("Pause");
            macroFrame.getPauseResumeButton().setForeground(Color.BLACK);
            System.out.println("--RESUMED--");
        }
    }

    public static void macroStopped() {
        if (macroFrame != null) {
            macroFrame.setStartStopButtonText("Start");
            macroFrame.getPauseResumeButton().setText("Pause");
            macroFrame.getPauseResumeButton().setForeground(Color.BLACK);
            macroFrame.getPauseResumeButton().setEnabled(false);
            if (currentMacro != null && currentMacro.isPaused()) {
                currentMacro.togglePause();
            }
        }
//        MacroMain.closeServerSockets();
    }

    private static void startMacro() {
        try {
            currentMacro = new Macro(macroString, loop, eventDelay, initialDelay);
            currentMacroThread = new Thread(currentMacro, "CurrentMacro");
            currentMacroThread.start();
            macroFrame.getPauseResumeButton().setEnabled(true);
            macroFrame.setStartStopButtonText("Stop");
        } catch (MacroParseException e) {
            System.err.println(e.getMessage());
            macroStopped();
        }  catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            macroStopped();
        }
    }

    public static void windowGainedFocus() {
        if (currentMacro != null && !currentMacro.isStopped() && !currentMacro.isPaused()) {
            pauseResumeButtonPressed();
        }
    }
    
    public static void macroPauseTimeout() {
        if (currentMacro != null && !currentMacro.isStopped() && currentMacro.isPaused()) {
            System.out.println("--PAUSE TIMEOUT--");
            startStopButtonPressed();
        }
    }

    private static boolean processArgs(String[] someArgs) {
        boolean rVal = true;
        for (String arg : someArgs) {
            if (arg.matches("macroString=[\\S\\s]*")) {
                macroString = arg.substring(12);
            } else if (arg.matches("initialDelay=.+")) {
                try {
                    initialDelay = Long.parseLong(arg.substring(13));
                    if (initialDelay < 0) {
                        System.err.println("Illegal initial delay: " + arg.substring(13));
                        rVal = false;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Illegal initial delay: " + arg.substring(13));
                    rVal = false;
                }
            } else if (arg.matches("eventDelay=.+")) {
                try {
                    eventDelay = Long.parseLong(arg.substring(11));
                    if (eventDelay < 0) {
                        System.err.println("Illegal event delay: " + arg.substring(11));
                        rVal = false;
                    } else if (eventDelay < 100){
                        System.out.println("WARNING: event delays less than 100 can result in odd behavior in JMacro with respect to execution timings");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Illegal event delay: " + arg.substring(11));
                    rVal = false;
                }
            } else if (arg.matches("loop=.+")) {
                loop = Boolean.parseBoolean(arg.substring(5));
            } else {
                System.err.println("Unknown command line argument: " + arg);
                System.out.println("usage: macroString=\"(macro string)\" [initialDelay=(milliseconds)] [eventDelay=(milliseconds)] [loop=true]");
                System.out.println("OR\tno command line arguments for GUI mode");
                rVal = false;
            }
        }

        if (macroString == null && someArgs.length != 0) {
            System.err.println("usage: macroString=\"(macro string)\" [initialDelay=(milliseconds)] [eventDelay=(milliseconds)] [loop=true]");
            System.err.println("\tOR\tno command line arguments for GUI mode");
            rVal = false;
        }

        if (!rVal && !graphicalMode) {
            System.exit(1);
        }

        Matcher matcher;
        for (String line : macroString.split("\\n")) {
            if ((matcher = loopOverride.matcher(line)).matches()) {
                loop = Boolean.parseBoolean(matcher.group(1));
                System.out.println("Setting loop to: " + loop);
            }

            if ((matcher = initialDelayOverride.matcher(line)).matches()) {
                initialDelay = Long.parseLong(matcher.group(1));
                System.out.println("Setting initial delay to: " + initialDelay);
            }

            if ((matcher = eventDelayOverride.matcher(line)).matches()) {
                eventDelay = Long.parseLong(matcher.group(1));
                System.out.println("Setting event delay to: " + eventDelay);
            }
        }

        return rVal;
    }

    public static void saveScript() {
        StringBuilder textToWrite = new StringBuilder();

        String[] args = macroFrame.getArgs();

        boolean foundLoopOverride = false;
        boolean foundInitialDelayOverride = false;
        boolean foundEventDelayOverride = false;
        for (String line : args[3].substring(12).split("\\n")) {
            if (loopOverride.matcher(line).matches()) {
                foundLoopOverride = true;
            }

            if (initialDelayOverride.matcher(line).matches()) {
                foundInitialDelayOverride = true;
            }

            if (eventDelayOverride.matcher(line).matches()) {
                foundEventDelayOverride = true;
            }
        }

        if (!foundLoopOverride) {
            textToWrite.append("//OVERRIDE_LOOP ");
            textToWrite.append(args[0].substring(5));
            textToWrite.append("\n");
        }
        if (!foundEventDelayOverride) {
            textToWrite.append("//OVERRIDE_EVENT_DELAY ");
            textToWrite.append(args[1].substring(11));
            textToWrite.append("\n");
        }
        if (!foundInitialDelayOverride) {
            textToWrite.append("//OVERRIDE_INITIAL_DELAY ");
            textToWrite.append(args[2].substring(13));
            textToWrite.append("\n");
        }

        textToWrite.append(args[3].substring(12));

        JFileChooser fileChooser = new JFileChooserOverwritePrompt(".");
        FileFilter filter = new FileNameExtensionFilter(jmacroFileDescriotion, jmacroFileExtension);
        fileChooser.addChoosableFileFilter(filter);
        if (fileChooser.showSaveDialog(macroFrame) == JFileChooser.APPROVE_OPTION) {
            macroFrame.clearOutput();
            try {
                File saveFile = fileChooser.getSelectedFile();
                if (!saveFile.getAbsolutePath().matches(".+\\." + jmacroFileExtension)) {
                    saveFile = new File(saveFile.getAbsolutePath() + '.' + jmacroFileExtension);
                }
                FileWriter fstream = new FileWriter(saveFile);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(textToWrite.toString());
                out.close();
                System.err.println("Macro saved");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

    }

    public static void openScript() {
        JFileChooser fileChooser = new JFileChooser(".");
        FileFilter filter = new FileNameExtensionFilter(jmacroFileDescriotion, jmacroFileExtension);
        fileChooser.addChoosableFileFilter(filter);
        if (fileChooser.showOpenDialog(macroFrame) == JFileChooser.APPROVE_OPTION) {
            macroFrame.clearOutput();
            try {
                File openFile = fileChooser.getSelectedFile();
                FileReader fstream = new FileReader(openFile);
                BufferedReader in = new BufferedReader(fstream);
                StringBuilder loadedMacroString = new StringBuilder();
                while (in.ready()) {
                    loadedMacroString.append((char) in.read());
                }
                in.close();
                macroFrame.setMacroInput(loadedMacroString.toString());
                System.err.println("Macro loaded");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

//    public static ServerSocket getServerSocket(int portNumber) throws IOException {
//        if(serverSockets == null){
//            serverSockets = new HashMap<Integer, ServerSocket>();
//        }
//        
//        Integer key = new Integer(portNumber);
//        if (!MacroMain.serverSockets.containsKey(key)) {
//            MacroMain.serverSockets.put(key, new ServerSocket(key.intValue()));
//        }
//        return MacroMain.serverSockets.get(key);
//    }
//    
//    public static void closeServerSockets(){
//        if(serverSockets != null){
//            for (ServerSocket serverSocket : MacroMain.serverSockets.values()) {
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace(System.err);
//                }
//            }
//            MacroMain.serverSockets.clear();
//        }
//    }

}
