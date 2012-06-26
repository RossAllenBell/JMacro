package org.jmacro.macroevent;

import org.jmacro.Macro;


public class LoopMacroEvent extends BlockMacroEvent {
    
    private long loopCount;
    
    public LoopMacroEvent(long aLoopCount, AMacroEvent aRootMacroEvent, Macro aMacro){
        super(aMacro, aRootMacroEvent);
        this.loopCount = aLoopCount;
    }
    
    @Override
    protected void runBegin(){
        super.runBegin();
        for(int i=0; i<this.loopCount && !this.macro.isStopped() && !this.blockEscaped; i++){
            System.out.println("Loop iteration: " + (i + 1));
            this.rootMacroEvent.run();
        }
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return String.format("loop(%d, ... )", Long.valueOf(this.loopCount));
    }

}
