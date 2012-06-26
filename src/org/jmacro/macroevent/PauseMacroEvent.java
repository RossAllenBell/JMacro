package org.jmacro.macroevent;

import org.jmacro.Macro;


public class PauseMacroEvent extends AMacroEvent{
    
    public PauseMacroEvent(Macro aMacro, long aDuration){
        super(aMacro,aDuration);
    }
    
    @Override
    protected void runBegin(){
        //no action
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return String.format("pause(%d)", Long.valueOf(this.getDuration()));
    }
    
}
