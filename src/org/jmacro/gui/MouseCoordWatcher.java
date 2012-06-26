package org.jmacro.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jmacro.MacroMain;
import org.jmacro.macroevent.IfColorMacroEvent;

public class MouseCoordWatcher implements Runnable {
    
    private JLabel coordinateLabel;
    
    private JPanel colorSquarePanel;
    
    private JLabel hexColorLabel;
    
    private Robot robot;
    
    public MouseCoordWatcher(JPanel aMouseCoordPanel){
        Dimension d;
        
        this.coordinateLabel = new JLabel();
        this.colorSquarePanel = new JPanel();
        d = new Dimension(15,15);
        this.colorSquarePanel.setSize(d);
        this.colorSquarePanel.setPreferredSize(d);
        this.colorSquarePanel.setMinimumSize(d);
        this.colorSquarePanel.setMaximumSize(d);
        this.colorSquarePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        this.hexColorLabel = new JLabel();
        
        aMouseCoordPanel.setLayout(new GridBagLayout());
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        aMouseCoordPanel.add(this.coordinateLabel,c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        aMouseCoordPanel.add(this.colorSquarePanel,c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        aMouseCoordPanel.add(this.hexColorLabel,c);
        
        this.robot = MacroMain.ROBOT;
    }
    
    @Override
    public void run(){
        while(true){
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if(pointerInfo != null){
                Point point = pointerInfo.getLocation();
                this.coordinateLabel.setText(String.format("[ %d , %d ]    ", Integer.valueOf(point.x), Integer.valueOf(point.y)));
                Color currentPixelColor = this.robot.getPixelColor(point.x, point.y);
                this.colorSquarePanel.setBackground(currentPixelColor);
    
                String colorString = IfColorMacroEvent.getHexValue(currentPixelColor);
                String colorDisplayString = colorString.substring(0, 2) + " " + colorString.substring(2, 4) + " " + colorString.substring(4);
                this.hexColorLabel.setText("    # " + colorDisplayString + "    ");
            }
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
                break;
            }
        }
    }
    
}
