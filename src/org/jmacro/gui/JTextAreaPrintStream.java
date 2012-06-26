package org.jmacro.gui;

import java.io.PrintStream;

import javax.swing.JTextArea;

public class JTextAreaPrintStream extends PrintStream {
    
    public JTextAreaPrintStream(JTextArea aJTextArea, int aHistory, PrintStream anOriginalStream){
        super(new JTextAreaOutputStream(aJTextArea, aHistory, anOriginalStream));
    }
    
    public JTextAreaPrintStream(JTextArea aJTextArea, int aHistory){
        this(aJTextArea, aHistory, null);
    }
    
}
