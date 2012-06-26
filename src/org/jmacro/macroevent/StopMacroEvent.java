
package org.jmacro.macroevent;

import org.jmacro.Macro;
import org.jmacro.MacroMain;

public class StopMacroEvent extends AMacroEvent {

    public StopMacroEvent(Macro aMacro) {
        super(aMacro);
    }

    @Override
    protected void runBegin() {
        MacroMain.startStopButtonPressed();
    }

    @Override
    protected void runEnd() {
        // no action
    }

    @Override
    public String toString() {
        return "stop";
    }

}
