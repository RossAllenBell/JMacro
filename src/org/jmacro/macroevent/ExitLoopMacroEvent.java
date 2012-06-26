package org.jmacro.macroevent;

import org.jmacro.Macro;


public class ExitLoopMacroEvent extends AMacroEvent{
    
    public ExitLoopMacroEvent(Macro aMacro){
        super(aMacro);
    }
    
    @Override
    protected void runBegin(){
        AMacroEvent predecessor = this;
        while(predecessor != null){
            if(predecessor.getBlockParentMacroEvent() != null){
                predecessor.getBlockParentMacroEvent().escapeBlock();
                predecessor = predecessor.getBlockParentMacroEvent();
                if(predecessor instanceof LoopMacroEvent || predecessor instanceof TimedLoopMacroEvent){
                    break;
                }
            }
            predecessor = predecessor.getPreviousMacroEvent();
        }
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return "exitLoop";
    }
    
}
