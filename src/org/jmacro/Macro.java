package org.jmacro;

import org.jmacro.compiler.MacroCompiler;
import org.jmacro.compiler.MacroParseException;
import org.jmacro.macroevent.AMacroEvent;


public class Macro implements Runnable {
    
    public static final long defaultPauseBetweenEvents = 1000;
    
    public static final long pauseSleepDuration  = 100;
    
    public static final long pauseTimeout = 600000; //10 minutes
    
    private long pauseBetweenEvents;
    
    private boolean looping;
    
    private long initialDelay;
    
    private AMacroEvent rootMacroEvent;
    
    private boolean stopped = true;
    
    private boolean paused = false;
    
    public Macro(String aMacroString, boolean isLooping, long aPauseBetweenEvents, long anInitialDelay) throws MacroParseException, IllegalArgumentException {
        String noCommentsString = aMacroString.replaceAll("//.*", "");
        noCommentsString = noCommentsString.replaceAll("/\\*[\\S\\s]*?\\*/", "");
        this.rootMacroEvent = new MacroCompiler(this, noCommentsString).compile();
        this.looping = isLooping;
        this.pauseBetweenEvents = aPauseBetweenEvents;
        this.initialDelay = anInitialDelay;
    }
    
    @SuppressWarnings("boxing")
    @Override
    public void run(){
        if(this.stopped){
            this.stopped = false;
            try{
                System.out.println("Initial Start Delay: " + this.initialDelay);
                int lastSecondReported = (int) Math.ceil(this.initialDelay / 1000.0);
                long startTime = System.currentTimeMillis();
                while(true && !this.stopped){
                    long currentTime = System.currentTimeMillis();
                    if(currentTime >= startTime + this.initialDelay){
                        break;
                    }
                    if(lastSecondReported != (int) Math.ceil((this.initialDelay - (currentTime - startTime)) / 1000.0)){
                        lastSecondReported = (int) Math.ceil((this.initialDelay - (currentTime - startTime)) / 1000.0);
                        System.out.println(lastSecondReported + "000...");
                    }
                    this.sleep(20);                    
                }
                while(!this.stopped){
                    ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
                    ThreadGroup parentGroup;
                    while ((parentGroup = rootGroup.getParent()) != null) {
                        rootGroup = parentGroup;
                    }
                    
                    Runtime runtime = Runtime.getRuntime ();
                    
                    System.out.println(String.format("JVM Memory [Total: %d] [Used: %d] [Free: %d] Threads [Total: %d] [Groups: %d]", runtime.totalMemory(), runtime.totalMemory() - runtime.freeMemory(), runtime.freeMemory(), rootGroup.activeCount(), rootGroup.activeGroupCount()));
                    System.gc();
                    
                    checkForPaused();
                    Thread rootMacroEventThread = new Thread(this.rootMacroEvent, "MacroRootEventString");
                    rootMacroEventThread.start();
                    rootMacroEventThread.join();
                    if(!this.looping){
                        stopMacro();
                        break;
                    }
                }
            } catch (InterruptedException e){
                stopMacro();
            }
        }
    }
    
    public void stopMacro(){
        this.stopped = true;
        if(this.paused){
            togglePause();
        }
        MacroMain.macroStopped();
    }
    
    public boolean togglePause(){
        if(!this.stopped || this.paused){
            this.paused = !this.paused;
            this.rootMacroEvent.togglePause();
        }
        return this.paused;
    }
    
    public long getPauseBetweenEvents(){
        return this.pauseBetweenEvents;
    }
    
    public boolean isStopped(){
        return this.stopped;
    }
    
    public boolean isPaused(){
        return this.paused;
    }
    
    private void sleep(long sleepMillis){
        long sleptMillis = 0;
        while(sleptMillis < sleepMillis){
            this.checkForPaused();
            sleptMillis+=20;
            try{
                Thread.sleep(20L);
            } catch (InterruptedException e){
                e.printStackTrace(System.err);
            }
        }
    }
    
    private void checkForPaused(){
        if(this.paused){
            long pauseStart = System.currentTimeMillis();
            while(this.paused){
                try{
                    Thread.sleep(pauseSleepDuration);
                } catch (InterruptedException e){
                    e.printStackTrace(System.err);
                }
                if(System.currentTimeMillis() - pauseStart >= pauseTimeout){
                    MacroMain.macroPauseTimeout();
                }
            }
        }
    }

}
