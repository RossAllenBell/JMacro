
package org.jmacro.macroevent;

import java.awt.Robot;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jmacro.Macro;
import org.jmacro.MacroMain;

public abstract class AMacroEvent implements Runnable {

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    protected Robot robot;

    private AMacroEvent nextMacroEvent;

    private AMacroEvent previousMacroEvent;

    private BlockMacroEvent blockParentMacroEvent;

    protected Macro macro;

    private long duration;

    private boolean continuing;

    private long startTime;

    protected boolean paused = false;

    private long predecessorEndTime = -1;

    protected abstract void runBegin();

    protected abstract void runEnd();

    @Override
    public abstract String toString();

    public AMacroEvent(Macro aMacro) {
        this(aMacro, 0);
    }

    public AMacroEvent(Macro aMacro, long aDuration) {
        this(aMacro, aDuration, false);
    }

    public AMacroEvent(Macro aMacro, long aDuration, boolean isContinuing) {
        this.macro = aMacro;
        this.duration = aDuration;
        this.continuing = isContinuing;
        this.nextMacroEvent = null;
        this.robot = MacroMain.ROBOT;
    }

    public void setNextMacroEvent(AMacroEvent aMacroEvent) {
        this.nextMacroEvent = aMacroEvent;
    }

    public void setPreviousMacroEvent(AMacroEvent aMacroEvent) {
        this.previousMacroEvent = aMacroEvent;
    }

    public void setBlockParentMacroEvent(BlockMacroEvent aMacroEvent) {
        this.blockParentMacroEvent = aMacroEvent;
    }

    public AMacroEvent getPreviousMacroEvent() {
        return this.previousMacroEvent;
    }

    public BlockMacroEvent getBlockParentMacroEvent() {
        return this.blockParentMacroEvent;
    }

    @Override
    final public void run() {
        if (this.predecessorEndTime != -1) {
            this.startTime = this.predecessorEndTime;
            this.predecessorEndTime = -1;
        } else {
            this.startTime = System.currentTimeMillis();
        }

        this.startTime += checkForPaused();
        System.out.println(timeFormat.format(new Date()) + "> " + this.toString());
        try {
            runBegin();
            Thread nextMacroEventThread = new Thread(this.nextMacroEvent, "NextMacroEvent");
            if (this.continuing && this.nextMacroEvent != null && !this.macro.isStopped()) {
                this.nextMacroEvent.setPredecessorEndTime(this.startTime);
                nextMacroEventThread.start();
            }

            int lastSecondReported = (int) Math.ceil(this.duration / 1000.0);
            while (!this.macro.isStopped() && System.currentTimeMillis() - this.startTime < this.duration) {
                this.startTime += this.checkForPaused();
                runBegin();
                try {
                    Thread.sleep(20L);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                int thisLastReportedSecond = (int) Math.ceil((this.duration - (System.currentTimeMillis() - this.startTime)) / 1000.0);
                if (lastSecondReported != thisLastReportedSecond) {
                    lastSecondReported = thisLastReportedSecond;
                    if (lastSecondReported != 0) {
                        System.out.println(lastSecondReported + "000...");
                    }
                }
            }

            runEnd();
            if (!this.macro.isStopped() && this.macro.getPauseBetweenEvents() > 0 && !(this instanceof PauseMacroEvent || this.nextMacroEvent instanceof PauseMacroEvent)) {
                this.sleep((this.startTime + this.duration + this.macro.getPauseBetweenEvents()) - System.currentTimeMillis());
            }
            if (this.nextMacroEvent != null && !this.macro.isStopped() && !(this instanceof ExitLoopMacroEvent) && (!(this instanceof IfColorMacroEvent) || !((IfColorMacroEvent) this).blockEscaped)) {
                if (!this.continuing && !this.macro.isStopped()) {
                    long durationWithPause = this.duration;
                    if (!(this instanceof PauseMacroEvent || this.nextMacroEvent instanceof PauseMacroEvent)) {
                        durationWithPause += this.macro.getPauseBetweenEvents();
                    }
                    if (!(this instanceof LoopMacroEvent) && !(this instanceof TimedLoopMacroEvent) && !(this instanceof IfColorMacroEvent)) {
                        this.nextMacroEvent.setPredecessorEndTime(this.startTime + durationWithPause);
                    }
                    nextMacroEventThread.start();
                }
                nextMacroEventThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isContinuing() {
        return this.continuing;
    }

    public void togglePause() {
        this.paused = !this.paused;
        if (this.nextMacroEvent != null) {
            this.nextMacroEvent.togglePause();
        }
    }

    private void sleep(long sleepMillis) {
        if (sleepMillis > 0) {
            long sleepStartTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - sleepStartTime < sleepMillis) {
                sleepStartTime += this.checkForPaused();
                try {
                    Thread.sleep(20L);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private long checkForPaused() {
        long pauseStartTime = System.currentTimeMillis();
        boolean wasPaused = false;
        while (this.paused) {
            wasPaused = true;
            try {
                Thread.sleep(Macro.pauseSleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            if (System.currentTimeMillis() - pauseStartTime >= Macro.pauseTimeout) {
                MacroMain.macroPauseTimeout();
            }
        }
        return wasPaused ? System.currentTimeMillis() - pauseStartTime : 0;
    }

    public void setPredecessorEndTime(long aPredecessorEndTime) {
        this.predecessorEndTime = aPredecessorEndTime;
    }

}
