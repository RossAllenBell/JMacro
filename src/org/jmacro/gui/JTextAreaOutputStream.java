package org.jmacro.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class JTextAreaOutputStream extends ByteArrayOutputStream {
    
    private JTextArea jTextArea;
    
    private int history;
    
    private PrintStream originalStream;
    
    public JTextAreaOutputStream(JTextArea aJTextArea, int aHistory, PrintStream anOriginalStream){
        super();
        this.jTextArea = aJTextArea;
        this.history = aHistory;
        this.originalStream = anOriginalStream;
    }
    
    @Override
    public void write(byte[] b, int off, int len){
        byte[] subArray = new byte[len];
        System.arraycopy(b, off, subArray, 0, len);
        this.writeOutput(new String(subArray));
        if(this.originalStream != null){
            this.originalStream.write(b, off, len);
        }
    }
    
    @Override
    public void write(int b){
        char c = (char) b;
        writeOutput(String.valueOf(c));
        if(this.originalStream != null){
            this.originalStream.write(b);
        }
    }
    
    private void writeOutput(String aLine){
        this.jTextArea.append(aLine);
        if(this.jTextArea.getText().length() > this.history){
            this.jTextArea.setText(this.jTextArea.getText().substring(this.jTextArea.getText().length() - this.history));
        }
        this.jTextArea.setCaretPosition(this.jTextArea.getText().length());
    }
    
}
