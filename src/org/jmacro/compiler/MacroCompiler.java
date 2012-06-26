package org.jmacro.compiler;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jmacro.Macro;
import org.jmacro.macroevent.AMacroEvent;
import org.jmacro.macroevent.ExitLoopMacroEvent;
import org.jmacro.macroevent.IfColorMacroEvent;
import org.jmacro.macroevent.InputMacroEvent;
import org.jmacro.macroevent.LoopMacroEvent;
import org.jmacro.macroevent.MouseMoveMacroEvent;
import org.jmacro.macroevent.PauseMacroEvent;
import org.jmacro.macroevent.StopMacroEvent;
import org.jmacro.macroevent.SynchronizeEvent;
import org.jmacro.macroevent.TimedLoopMacroEvent;


public class MacroCompiler {
    
    public static final Set<String> inputCommands = new HashSet<String>(){
        private static final long serialVersionUID = 0L;
        {
            this.add("mouseLeft");
            this.add("mouseRight");
            this.add("mouseMiddle");
            this.add("enter");
            this.add("space");
            this.add("esc");
            this.add("tab");
            this.add("shift");
            this.add("ctrl");
            this.add("alt");
            this.add("up");
            this.add("down");
            this.add("left");
            this.add("right");            
            this.add("scrollLock");
            this.add("break");
            this.add("numLock");
            this.add("capsLock");
            this.add("backspace");
            this.add("insert");
            this.add("delete");
            this.add("home");
            this.add("end");
            this.add("pageUp");
            this.add("pageDown");
        }
    };
    
    private static final Pattern alphaNumeric = Pattern.compile("[A-Z\\d]");
    
    private static final Pattern functionKey = Pattern.compile("f\\d{1,2}");
    
    private Macro macro;
    
    private StringBuffer toParse;
    
    public MacroCompiler(Macro aMacro, String aMacroString){
        this.macro = aMacro;
        this.toParse = new StringBuffer(aMacroString);
    }
    
    public AMacroEvent compile() throws MacroParseException, IllegalArgumentException {
        new MacroLexicalAnalyzer(this.toParse.toString()).lexiacallyAnalyzeMacro();
        this.toParse = new StringBuffer(this.toParse.toString().replaceAll("\\s+", ""));
        
        return returnRootMacroEvent();
    }
    
    private AMacroEvent returnRootMacroEvent() {
        AMacroEvent rVal = null;
        AMacroEvent previousEvent = null;
        while(this.toParse.length() > 0){
            AMacroEvent macroEvent = getNextMacroEvent();
            if(rVal == null){
                rVal = macroEvent;
            } else if(previousEvent != null){
                previousEvent.setNextMacroEvent(macroEvent);
                macroEvent.setPreviousMacroEvent(previousEvent);
            }
            previousEvent = macroEvent;
        }        
        return rVal;
    }
    
    private AMacroEvent getNextMacroEvent(){
        AMacroEvent rVal = null;
        char nextChar;
        StringBuilder currentCommand = new StringBuilder();
        while(rVal == null && (nextChar = getNextChar()) != 0){
            currentCommand.append(nextChar);
            if(MacroCompiler.alphaNumeric.matcher(currentCommand.toString()).matches() ||
                    MacroCompiler.functionKey.matcher(currentCommand.toString()).matches() ||
                    MacroCompiler.inputCommands.contains(currentCommand.toString())){
                String argumentsString = getArgumentsString();
                String[] arguments = {};
                if(argumentsString.length() > 0){
                    arguments = argumentsString.split(",");
                }
                if(arguments.length == 0){
                    rVal =  new InputMacroEvent(currentCommand.toString(), this.macro);
                } else if (arguments.length == 1){
                    rVal =  new InputMacroEvent(currentCommand.toString(), this.macro, Integer.parseInt(arguments[0]));
                } else if (arguments.length == 2){
                    rVal =  new InputMacroEvent(currentCommand.toString(), this.macro, Integer.parseInt(arguments[0]), true);
                }
            } else if("pause".equals(currentCommand.toString())){
                String[] arguments = getArgumentsString().split(",");
                rVal = new PauseMacroEvent(this.macro, Integer.parseInt(arguments[0]));
            } else if("mouse".equals(currentCommand.toString()) && peakNextChar() == '('){
                String[] arguments = getArgumentsString().split(",");
                rVal = new MouseMoveMacroEvent(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), arguments.length > 2 && arguments[2].equals("smooth"), this.macro);
            } else if("loop".equals(currentCommand.toString())){
                String argumentsString = getArgumentsString();
                int firstComma = argumentsString.indexOf(',');
                AMacroEvent innerLoopRootEvent = new MacroCompiler(this.macro, argumentsString.substring(firstComma + 1)).returnRootMacroEvent();
                rVal = new LoopMacroEvent(Integer.parseInt(argumentsString.substring(0, firstComma)), innerLoopRootEvent, this.macro);
            } else if("timedLoop".equals(currentCommand.toString())){
                String argumentsString = getArgumentsString();
                int firstComma = argumentsString.indexOf(',');
                AMacroEvent innerLoopRootEvent = new MacroCompiler(this.macro, argumentsString.substring(firstComma + 1)).returnRootMacroEvent();
                rVal = new TimedLoopMacroEvent(Integer.parseInt(argumentsString.substring(0, firstComma)), innerLoopRootEvent, this.macro);
            } else if("synchronize".equals(currentCommand.toString())){
                String[] arguments = getArgumentsString().split(",");
                if(arguments.length == 1){
                    rVal = new SynchronizeEvent(arguments[0], this.macro);                    
                } else {
                    rVal = new SynchronizeEvent(arguments[0], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), this.macro); 
                }
            } else if("ifColor".equals(currentCommand.toString())){
                String argumentsString = getArgumentsString();
                String[] arguments = argumentsString.split(",");
                int commaBeforeInnerMacro = argumentsString.indexOf(',', argumentsString.indexOf('#'));
                AMacroEvent innerLoopRootEvent = new MacroCompiler(this.macro, argumentsString.substring(commaBeforeInnerMacro + 1)).returnRootMacroEvent();
                rVal = new IfColorMacroEvent(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), arguments[2].substring(1), innerLoopRootEvent, this.macro);
            } else if("ifNotColor".equals(currentCommand.toString())){
                String argumentsString = getArgumentsString();
                String[] arguments = argumentsString.split(",");
                int commaBeforeInnerMacro = argumentsString.indexOf(',', argumentsString.indexOf('#'));
                AMacroEvent innerLoopRootEvent = new MacroCompiler(this.macro, argumentsString.substring(commaBeforeInnerMacro + 1)).returnRootMacroEvent();
                rVal = new IfColorMacroEvent(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), arguments[2].substring(1), innerLoopRootEvent, this.macro, true);
            } else if("exitLoop".equals(currentCommand.toString())){
                rVal = new ExitLoopMacroEvent(this.macro);
            } else if("stop".equals(currentCommand.toString())){
                rVal = new StopMacroEvent(this.macro);
            }
        }
        return rVal;
    }
    
    private char peakNextChar(){
        char rVal = ' ';
        int i = 0;
        while(MacroLexicalAnalyzer.whitespace.matcher(String.valueOf(rVal)).matches() && this.toParse.length() > i){
            rVal = this.toParse.charAt(i++);
        }
        return MacroLexicalAnalyzer.whitespace.matcher(String.valueOf(rVal)).matches()? 0 : rVal;
    }
    
    private char getNextChar(){
        char rVal = 0;
        if(this.toParse.length() > 0){
            rVal = this.toParse.charAt(0);
            this.toParse.deleteCharAt(0);
        }
        return MacroLexicalAnalyzer.whitespace.matcher(String.valueOf(rVal)).matches()? getNextChar() : rVal;
    }
    
    private String getArgumentsString(){
        StringBuilder rVal = new StringBuilder();
        int unclosedLeftParens = 0;
        if(peakNextChar() == '('){
            getNextChar();
            unclosedLeftParens++;
        }
        while(unclosedLeftParens != 0){
            char nextChar = getNextChar();
            if(nextChar == '('){
                unclosedLeftParens++;
            } else if(nextChar == ')'){
                unclosedLeftParens--;
            }
            rVal.append(nextChar);
        }
        if(rVal.length() == 0){
            return "";
        }
        return rVal.substring(0, rVal.length() - 1);
    }

}
