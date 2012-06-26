package org.jmacro.macroevent;

import java.awt.MouseInfo;
import java.awt.Point;

import org.jmacro.Macro;



public class MouseMoveMacroEvent extends AMacroEvent {
    
    private int x;
    
    private int y;
    
    private boolean smooth;
    
    public MouseMoveMacroEvent(int anX, int aY, boolean isSmooth, Macro aMacro){
        super(aMacro);
        this.x = anX;
        this.y = aY;
        this.smooth = isSmooth;
    }
    
    @Override
    protected void runBegin(){        
        if(!this.smooth){
            this.robot.mouseMove(this.x, this.y);
        } else {
            Point mouseOrigin = MouseInfo.getPointerInfo().getLocation();
            if(mouseOrigin.x != this.x || mouseOrigin.y != this.y){
                double length = Math.sqrt(Math.pow(this.x-mouseOrigin.x,2) + Math.pow(this.y-mouseOrigin.y,2));
                double xDelta = (this.x - mouseOrigin.x) / length;
                double yDelta = (this.y - mouseOrigin.y) / length;
                double currentX = mouseOrigin.x;
                double currentY = mouseOrigin.y;
                while((int)currentX != this.x || (int)currentY != this.y){
                    if((int)currentX != this.x){
                        currentX += xDelta;
                    }
                    if((int)currentY != this.y){
                        currentY += yDelta;
                    }
                    this.robot.mouseMove((int) currentX, (int) currentY);
                }
            }
        }
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return String.format("mouse(%d, %d%s)", Integer.valueOf(this.x), Integer.valueOf(this.y), this.smooth? ", smooth":"");
    }
    
}
