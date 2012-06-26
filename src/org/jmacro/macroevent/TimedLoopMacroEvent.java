package org.jmacro.macroevent;

import org.jmacro.Macro;


public class TimedLoopMacroEvent extends BlockMacroEvent {
    
    private long loopDuration;
    
    public TimedLoopMacroEvent(long aLoopDuration, AMacroEvent aRootMacroEvent, Macro aMacro){
        super(aMacro, aRootMacroEvent);
        this.loopDuration = aLoopDuration;
    }
    
    @Override
    protected void runBegin(){
        super.runBegin();
        long startTime = System.currentTimeMillis();
        while(startTime + this.loopDuration > System.currentTimeMillis() && !this.blockEscaped){
            System.out.println("Timed Loop: " + (System.currentTimeMillis() - startTime) + " of " + this.loopDuration);
            this.rootMacroEvent.run();
        }
    }
    
    @Override
    protected void runEnd(){
        //no action
    }
    
    @Override
    public String toString(){     
        return String.format("timedLoop(%d, ... )", Long.valueOf(this.loopDuration));
    }

}
