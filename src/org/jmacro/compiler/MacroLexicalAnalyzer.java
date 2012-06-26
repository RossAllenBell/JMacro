package org.jmacro.compiler;

import java.util.regex.Pattern;

class MacroLexicalAnalyzer {
    
    public static final Pattern whitespace = Pattern.compile("\\s");
    
    public static final Pattern numeric = Pattern.compile("\\d");
    
    public static final String delimeters = ",()";
    
    private StringBuilder parsed;
    
    private StringBuffer toParse;
    
    private int blockLevel;
    
    public MacroLexicalAnalyzer(String macroChars) {
        this(new StringBuffer(macroChars));
    }
    
    public MacroLexicalAnalyzer(StringBuffer macroChars) {
        this.toParse = macroChars;
        this.parsed = new StringBuilder();
        this.blockLevel = 0;
    }

    public boolean lexiacallyAnalyzeMacro() throws MacroParseException {
        if(this.toParse.length() == 0){
            throw new MacroParseException("Empty macros are not allowed");
        }
        while(this.toParse.length() > 0){
            this.base();
        }
        if(this.blockLevel != 0){
            throwMacroParseException("Unclosed '('.");
        }
        return true;
    }
    
    private void base() throws MacroParseException {
        char nextChar;
        if((nextChar = getNextChar()) != 0){
            if((nextChar >= 'A' && nextChar <= 'Z') || (nextChar >= '0' && nextChar <= '9')){
                inputMacro();
            } else if (nextChar == ')') {
                if(this.blockLevel == 0){
                    throwMacroParseException("Encountered ')', which is out of place here.");
                }
                this.blockLevel--;
            } else {
                switch (nextChar) {
                    case 'm': m(); break;
                    case 'f': f(); break;
                    case 'e': e(); break;
                    case 's': s(); break;
                    case 'c': c(); break;
                    case 'a': a(); break;
                    case 'u': u(); break;
                    case 'd': d(); break;
                    case 'l': l(); break;
                    case 'r': r(); break;
                    case 'p': p(); break;
                    case 't': t(); break;
                    case 'b': b(); break;
                    case 'n': n(); break;
                    case 'i': i(); break;
                    case 'h': h(); break;
                    default:
                        throwMacroParseException("Encountered '" + nextChar + "', which is out of place here.");
                }
            }
        }
        if(!isDelimited()){
            throwMacroParseException("Expected separation of command identifier");
        }
    }
    
    private void inputMacro() throws MacroParseException {
        char peakedNextChar = peakNextChar();
        if(peakedNextChar == '('){
            getNextChar();
            inputMacroParams();
        }
    }
    
    private void inputMacroParams() throws MacroParseException {
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected integer here");
        }
        char nextChar = getNextChar();
        switch (nextChar) {
            case ')': break;
            case ',': inputMacroParamsSecond(); break;
            default: throwMacroParseException("Expected ')' or ','");
        }
    }
    
    private void inputMacroParamsSecond() throws MacroParseException {
        String next8 = getNextNChars(8);
        if(!next8.equals("continue")){
            throwMacroParseException("Expected \"continue\"");
        }
        if(getNextChar() != ')'){
            throwMacroParseException("Expected ')'");
        }
    }
    
    private void synchronize() throws MacroParseException {
        if(getNextChar() == '('){
            while(true){
                char nextChar = getNextChar();
                if(nextChar == ')'){
                    break;
                } else if(nextChar == ','){
                    if(!testForAndRemoveNextInt()){
                        throwMacroParseException("Expected integer here");
                    }
                    if(getNextChar() != ','){
                        throwMacroParseException("Expected ','");
                    }
                    if(!testForAndRemoveNextInt()){
                        throwMacroParseException("Expected integer here");
                    }
                    if(getNextChar() != ')'){
                        throwMacroParseException("Expected ')'");
                    }
                    break;
                } else if(nextChar == 0){
                    throwMacroParseException("Expected \"synchronize(...)\"");
                }
            }
        } else {
            throwMacroParseException("Expected \"synchronize(...)\"");
        }
    }
    
    private void m() throws MacroParseException {
        String next4 = getNextNChars(4);
        if(!next4.equals("ouse")){
            throwMacroParseException("Expected \"mouse...\" ");
        }
        char nextChar = getNextChar();
        switch(nextChar){
            case '(': mouse(); break;
            case 'L': mouseL(); break;
            case 'R': mouseR(); break;
            case 'M': mouseM(); break;
            default: throwMacroParseException("Expected \"mouse...\"");
        }
    }
    
    private void mouse() throws MacroParseException {
        char nextChar;
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected \"mouse([integer],[integer])\"");
        }
        nextChar = getNextChar();
        if(nextChar != ','){
            throwMacroParseException("Expected \"mouse([integer],[integer])\"");
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected \"mouse([integer],[integer])\"");
        }
        nextChar = getNextChar();
        if(nextChar == ','){
            String next6 = getNextNChars(6);
            if(!next6.equals("smooth") || getNextChar() != ')'){
                throwMacroParseException("Expected \"mouse([integer],[integer],smooth)\"");
            }            
        } else if(nextChar != ')'){
            throwMacroParseException("Expected \"mouse([integer],[integer])\"");
        }
    }
    
    private void mouseL() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("eft")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"mouseLeft\"");
        }
    }
    
    private void mouseR() throws MacroParseException {
        String next4 = getNextNChars(4);
        if(next4.equals("ight")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"mouseRight\"");
        }
    }
    
    private void mouseM() throws MacroParseException {
        String next5 = getNextNChars(5);
        if(next5.equals("iddle")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"mouseMiddle\"");
        }
    }
    
    private void f() throws MacroParseException {
        if(testForAndRemoveNextInt()){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"f[1-12]\"");
        }
    }
    
    private void e() throws MacroParseException {
        String next2 = getNextNChars(2);
        if(next2.equals("sc") || next2.equals("nd")){
            inputMacro();
        } else if (next2.equals("nt")){
            if(getNextNChars(2).equals("er")){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"enter\"");
            }
        } else if(next2.equals("xi")){
            if(!getNextNChars(5).equals("tLoop")){
                throwMacroParseException("Expected \"exitLoop\"");
            }
        } else {
            throwMacroParseException("Expected \"esc\" or \"enter\" or \"end\" or \"exitLoop\"");
        }
    }
    
    private void s() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("pac") || next3.equals("hif")){
            char c = getNextChar();
            if(c == 'e' || c == 't'){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"space\" or \"shift\"");
            }
        } else if(next3.equals("cro")){
            if(getNextNChars(6).equals("llLock")){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"scrollLock\"");
            }
        } else if(next3.equals("ync")){
            if(getNextNChars(7).equals("hronize")){
                synchronize();
            } else {
                throwMacroParseException("Expected \"synchronize(...)\"");
            }
        } else if(!next3.equals("top")) {
            throwMacroParseException("Expected \"space\" or \"shift\" or \"scrollLock\" or \"stop\" or \"synchronize(...)\"");
        }
    }
    
    private void c() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("trl")){
            inputMacro();
        } else if(next3.equals("aps")){
            if(getNextNChars(4).equals("Lock")){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"capsLock\"");
            }
        } else {
            throwMacroParseException("Expected \"ctrl\" or \"capsLock\"");
        }
    }
    
    private void a() throws MacroParseException {
        String next2 = getNextNChars(2);
        if(next2.equals("lt")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"alt\"");
        }
    }
    
    private void u() throws MacroParseException {
        char nextChar = getNextChar();
        if(nextChar == 'p'){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"up\"");
        }
    }
    
    private void d() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("own")){
            inputMacro();
        } else if(next3.equals("ele")){
            if(getNextNChars(2).equals("te")){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"delete\"");
            }
        } else {
            throwMacroParseException("Expected \"down\" or \"delete\"");
        }
    }
    
    private void l() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("eft")){
            inputMacro();
        } else if (next3.equals("oop")){
            loop();
        } else {
            throwMacroParseException("Expected \"left\" or \"loop(...)\"");
        }
    }
    
    private void loop() throws MacroParseException {
        if(getNextChar() != '('){
            throwMacroParseException("Expected \"loop( [integer], [command(s)] )\"");
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected \"loop( [integer], [command(s)] )\"");
        }
        if(getNextChar() != ','){
            throwMacroParseException("Expected \"loop( [integer], [command(s)] )\"");
        } 
        if(peakNextChar() == ')'){
            throwMacroParseException("Expected \"loop( [integer], [command(s)] )\"");
        } 
        this.blockLevel++;
    }
    
    private void timedLoop() throws MacroParseException {
        if(getNextChar() != '('){
            throwMacroParseException("Expected \"timedLoop( [integer], [command(s)] )\"");
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected \"timedLoop( [integer], [command(s)] )\"");
        }
        if(getNextChar() != ','){
            throwMacroParseException("Expected \"timedLoop( [integer], [command(s)] )\"");
        } 
        if(peakNextChar() == ')'){
            throwMacroParseException("Expected \"timedLoop( [integer], [command(s)] )\"");
        } 
        this.blockLevel++;
    }
    
    private void ifColor(boolean isNot) throws MacroParseException {
        String exceptionString = "Expected \"if" + (isNot? "Not" : "") + "Color( [integer], [integer], #[color], [command(s)] )\"";
        
        if(getNextChar() != '('){
            throwMacroParseException(exceptionString);
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException(exceptionString);
        }
        if(getNextChar() != ','){
            throwMacroParseException(exceptionString);
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException(exceptionString);
        }
        if(getNextChar() != ','){
            throwMacroParseException(exceptionString);
        }
        if(getNextChar() != '#'){
            throwMacroParseException(exceptionString);
        }
        if(!getNextNChars(6).matches("[A-F0-9]{6}")){
            throwMacroParseException(exceptionString);
        }
        if(getNextChar() != ','){
            throwMacroParseException(exceptionString);
        }
        if(peakNextChar() == ')'){
            throwMacroParseException(exceptionString);
        } 
        this.blockLevel++;
    }
    
    private void r() throws MacroParseException {
        String next4 = getNextNChars(4);
        if(next4.equals("ight")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"right\"");
        }
    }
    
    private void p() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("age")){
            String next2 = getNextNChars(2);
            if(next2.equals("Up")){
                inputMacro();
            } else if(next2.equals("Do")){
                if(getNextNChars(2).equals("wn")){
                    inputMacro();
                }
            } else {
                throwMacroParseException("Expected \"pageUp\" or \"pageDown\"");
            }
        } else if(next3.equals("aus")){
            if(getNextNChars(1).equals("e")){
                pause();
            }
        } else {
            throwMacroParseException("Expected \"pause(...)\" or \"pageUp\" or \"pageDown\"");
        }
    }
    
    private void pause() throws MacroParseException {
        if(getNextChar() != '('){
            throwMacroParseException("Expected \"pause(...)\"");
        }
        if(!testForAndRemoveNextInt()){
            throwMacroParseException("Expected \"pause(...)\"");
        }
        if(getNextChar() != ')'){
            throwMacroParseException("Expected \"pause(...)\"");
        }
    }
    
    private void t() throws MacroParseException {
        String next2 = getNextNChars(2);
        if(next2.equals("ab")){
            inputMacro();
        } else if (next2.equals("im")){
            String next6 = getNextNChars(6);
            if(next6.equals("edLoop")){
                timedLoop();
            } else {
                throwMacroParseException("Expected \"timedLoop(...)\"");
            }
        } else {
            throwMacroParseException("Expected \"tab\" or \"timedLoop(...)\"");
        }
    }
    
    private void b() throws MacroParseException {
        String next4 = getNextNChars(4);
        if(next4.equals("reak")){
            inputMacro();
        } else if(next4.equals("acks")){
            if(getNextNChars(4).equals("pace")){
                inputMacro();
            } else {
                throwMacroParseException("Expected \"backspace\"");
            }
        } else {
            throwMacroParseException("Expected \"break\" or \"backspace\"");
        }
    }
    
    private void n() throws MacroParseException {
        String next6 = getNextNChars(6);
        if(next6.equals("umLock")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"numLock\"");
        }
    }
    
    private void i() throws MacroParseException {
        String next5 = getNextNChars(5);
        if(next5.equals("nsert")){
            inputMacro();
        } else if(next5.equals("fColo")) {
            if(getNextChar() == 'r'){
                ifColor(false);                
            } else {
                throwMacroParseException("Expected \"ifColor\"");
            }
        } else if(next5.equals("fNotC")) {
            if(getNextNChars(4).equals("olor")){
                ifColor(true);                
            } else {
                throwMacroParseException("Expected \"ifNotColor\"");
            }
        } else {
            throwMacroParseException("Expected \"insert\" or \"ifColor\" or \"ifNotColor\"");
        }
    }
    
    private void h() throws MacroParseException {
        String next3 = getNextNChars(3);
        if(next3.equals("ome")){
            inputMacro();
        } else {
            throwMacroParseException("Expected \"home\"");
        }
    }
    
    private char getNextChar(){
        char rVal = 0;
        if(this.toParse.length() > 0){
            rVal = this.toParse.charAt(0);
            this.toParse.deleteCharAt(0);
            this.parsed.append(rVal);
        }
        return whitespace.matcher(String.valueOf(rVal)).matches()? getNextChar() : rVal;
    }
    
    private String getNextNChars(int n){
        StringBuilder nextN = new StringBuilder();
        for(int i=0; this.toParse.length() > 0 && i < n; i++){
            char nextChar = getNextChar();
            if(nextChar != 0){
                nextN.append(nextChar);
            }
        }
        return nextN.toString();
    }
    
    private boolean testForAndRemoveNextInt(){
        boolean rVal = false;
        while(numeric.matcher(String.valueOf(peakNextChar())).matches()){
            getNextChar();
            rVal = true;
        }
        return rVal;
    }
    
    private boolean isDelimited(){
        return isNextDelimitingCharacter() || isLastDelimitingCharacter();
    }
    
    private boolean isNextDelimitingCharacter(){
        return this.toParse.length() == 0 ||
            whitespace.matcher(String.valueOf(this.toParse.charAt(0))).matches() ||
            delimeters.indexOf(this.toParse.charAt(0)) != -1;
    }
    
    private boolean isLastDelimitingCharacter(){
        if(this.parsed.length() == 0){
            return true;
        }
        char lastCharacter = this.parsed.charAt(this.parsed.length() - 1);
        return numeric.matcher(String.valueOf(lastCharacter)).matches() ||
        delimeters.indexOf(lastCharacter) != -1;
    }
    
    private char peakNextChar(){
        char rVal = ' ';
        int i = 0;
        while(whitespace.matcher(String.valueOf(rVal)).matches() && this.toParse.length() > i){
            rVal = this.toParse.charAt(i++);
        }
        return whitespace.matcher(String.valueOf(rVal)).matches()? 0 : rVal;
    }
    
    private void throwMacroParseException(String errorMessage) throws MacroParseException {
        throw new MacroParseException(this.parsed + "\n\n" + errorMessage);
    }
    
}
