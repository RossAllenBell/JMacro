package org.jmacro.macroevent;

import org.jmacro.Macro;


public abstract class BlockMacroEvent extends AMacroEvent {
    
    protected AMacroEvent rootMacroEvent;
    
    protected boolean blockEscaped = false;

    public BlockMacroEvent(Macro aMacro, AMacroEvent aRootMacroEvent) {
        super(aMacro);
        this.rootMacroEvent = aRootMacroEvent;
        this.rootMacroEvent.setBlockParentMacroEvent(this);
    }
    
    public void escapeBlock(){
        this.blockEscaped = true;
    }
    
    @Override
    final public void togglePause(){
        super.togglePause();
        this.rootMacroEvent.togglePause();
    }
    
    @Override
    protected void runBegin(){
        this.blockEscaped = false;
    }

}
