package org.jmacro.macroevent;

import java.awt.Color;

import org.jmacro.Macro;


public class IfColorMacroEvent extends BlockMacroEvent {
    
    private int x;
    
    private int y;
    
    private String colorHexValue;
    
    private boolean not;
    
    public IfColorMacroEvent(int anX, int aY, String aColorHexValue, AMacroEvent aRootMacroEvent, Macro aMacro){
        this(anX, aY, aColorHexValue, aRootMacroEvent, aMacro, false);
    }
    
    public IfColorMacroEvent(int anX, int aY, String aColorHexValue, AMacroEvent aRootMacroEvent, Macro aMacro, boolean isNot){
        super(aMacro, aRootMacroEvent);
        this.x = anX;
        this.y = aY;
        this.colorHexValue = aColorHexValue;
        this.not = isNot;
    }
    
    @Override
    protected void runBegin(){
        super.runBegin();
        String currentColorHexValue = IfColorMacroEvent.getHexValue(this.robot.getPixelColor(this.x, this.y));
        System.out.println("    Found color: #" + currentColorHexValue);
        if(currentColorHexValue.equals(this.colorHexValue) ^ this.not){
            System.out.println("    Proceeding...");
            this.rootMacroEvent.run();
        } else {
            System.out.println("    Skipping...");
        }
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return String.format("if%sColor(%d, %d, #%s, ... )", (this.not? "Not" : ""), Integer.valueOf(this.x), Integer.valueOf(this.y), this.colorHexValue);
    }
    
    public static String getHexValue(Color aColor){
        String hexColor = Integer.toHexString(aColor.getRGB());
        hexColor = hexColor.toUpperCase();
        hexColor = hexColor.substring(Math.max(0, 2 - (8 - hexColor.length())));
        return hexColor;
    }

}
