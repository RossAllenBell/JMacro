package org.jmacro.macroevent;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.jmacro.Macro;



public class InputMacroEvent extends AMacroEvent {
    
    private String input;
    
    private boolean mouseClicked = false;
    
    public InputMacroEvent(String anInput, Macro aMacro){
        this(anInput, aMacro, 0);
    }
    
    public InputMacroEvent(String anInput, Macro aMacro, long aDuration){
        this(anInput, aMacro, aDuration, false);
    }
    
    public InputMacroEvent(String anInput, Macro aMacro, long aDuration, boolean isContinuing){
        super(aMacro,aDuration,isContinuing);
        this.input = anInput;
    }
    
    @Override
    protected void runBegin(){
        if(this.input.matches("mouse.+")){
            if(!this.mouseClicked){
                this.robot.mousePress(InputMacroEvent.getButton(this.input));
                this.mouseClicked = true;
            }
        } else {
            this.robot.keyPress(InputMacroEvent.getButton(this.input));
        }
    }
    
    @Override
    protected void runEnd(){
        if(this.input.matches("mouse.+")){
            this.robot.mouseRelease(InputMacroEvent.getButton(this.input));
            this.mouseClicked = false;
        } else {
            this.robot.keyRelease(InputMacroEvent.getButton(this.input));
        }
    }
    
    private static int getButton(String anInput){
        int button = -1;
        if(anInput.matches("mouse.+")){
            if(anInput.equals("mouseLeft")){
                button = InputEvent.BUTTON1_MASK;
            } else if(anInput.equals("mouseRight")){
                button = InputEvent.BUTTON3_MASK;
            } else if(anInput.equals("mouseMiddle")){
                button = InputEvent.BUTTON2_MASK;
            }
        } else {
            if(anInput.equals("A")){
                button = KeyEvent.VK_A;
            } else if(anInput.equals("B")){
                button = KeyEvent.VK_B;
            } else if(anInput.equals("C")){
                button = KeyEvent.VK_C;
            } else if(anInput.equals("D")){
                button = KeyEvent.VK_D;
            } else if(anInput.equals("E")){
                button = KeyEvent.VK_E;
            } else if(anInput.equals("F")){
                button = KeyEvent.VK_F;
            } else if(anInput.equals("G")){
                button = KeyEvent.VK_G;
            } else if(anInput.equals("H")){
                button = KeyEvent.VK_H;
            } else if(anInput.equals("I")){
                button = KeyEvent.VK_I;
            } else if(anInput.equals("J")){
                button = KeyEvent.VK_J;
            } else if(anInput.equals("K")){
                button = KeyEvent.VK_K;
            } else if(anInput.equals("L")){
                button = KeyEvent.VK_L;
            } else if(anInput.equals("M")){
                button = KeyEvent.VK_M;
            } else if(anInput.equals("N")){
                button = KeyEvent.VK_N;
            } else if(anInput.equals("O")){
                button = KeyEvent.VK_O;
            } else if(anInput.equals("P")){
                button = KeyEvent.VK_P;
            } else if(anInput.equals("Q")){
                button = KeyEvent.VK_Q;
            } else if(anInput.equals("R")){
                button = KeyEvent.VK_R;
            } else if(anInput.equals("S")){
                button = KeyEvent.VK_S;
            } else if(anInput.equals("T")){
                button = KeyEvent.VK_T;
            } else if(anInput.equals("U")){
                button = KeyEvent.VK_U;
            } else if(anInput.equals("V")){
                button = KeyEvent.VK_V;
            } else if(anInput.equals("W")){
                button = KeyEvent.VK_W;
            } else if(anInput.equals("X")){
                button = KeyEvent.VK_X;
            } else if(anInput.equals("Y")){
                button = KeyEvent.VK_Y;
            } else if(anInput.equals("Z")){
                button = KeyEvent.VK_Z;
            } else if(anInput.equals("0")){
                button = KeyEvent.VK_0;
            } else if(anInput.equals("1")){
                button = KeyEvent.VK_1;
            } else if(anInput.equals("2")){
                button = KeyEvent.VK_2;
            } else if(anInput.equals("3")){
                button = KeyEvent.VK_3;
            } else if(anInput.equals("4")){
                button = KeyEvent.VK_4;
            } else if(anInput.equals("5")){
                button = KeyEvent.VK_5;
            } else if(anInput.equals("6")){
                button = KeyEvent.VK_6;
            } else if(anInput.equals("7")){
                button = KeyEvent.VK_7;
            } else if(anInput.equals("8")){
                button = KeyEvent.VK_8;
            } else if(anInput.equals("9")){
                button = KeyEvent.VK_9;
            } else if(anInput.equals("enter")){
                button = KeyEvent.VK_ENTER;
            } else if(anInput.equals("space")){
                button = KeyEvent.VK_SPACE;
            } else if(anInput.equals("esc")){
                button = KeyEvent.VK_ESCAPE;
            } else if(anInput.equals("tab")){
                button = KeyEvent.VK_TAB;
            } else if(anInput.equals("shift")){
                button = KeyEvent.VK_SHIFT;
            } else if(anInput.equals("ctrl")){
                button = KeyEvent.VK_CONTROL;
            } else if(anInput.equals("alt")){
                button = KeyEvent.VK_ALT;
            } else if(anInput.equals("up")){
                button = KeyEvent.VK_UP;
            } else if(anInput.equals("down")){
                button = KeyEvent.VK_DOWN;
            } else if(anInput.equals("left")){
                button = KeyEvent.VK_LEFT;
            } else if(anInput.equals("right")){
                button = KeyEvent.VK_RIGHT;
            } else if(anInput.equals("f1")){
                button = KeyEvent.VK_F1;
            } else if(anInput.equals("f2")){
                button = KeyEvent.VK_F2;
            } else if(anInput.equals("f3")){
                button = KeyEvent.VK_F3;
            } else if(anInput.equals("f4")){
                button = KeyEvent.VK_F4;
            } else if(anInput.equals("f5")){
                button = KeyEvent.VK_F5;
            } else if(anInput.equals("f6")){
                button = KeyEvent.VK_F6;
            } else if(anInput.equals("f7")){
                button = KeyEvent.VK_F7;
            } else if(anInput.equals("f8")){
                button = KeyEvent.VK_F8;
            } else if(anInput.equals("f9")){
                button = KeyEvent.VK_F9;
            } else if(anInput.equals("f10")){
                button = KeyEvent.VK_F10;
            } else if(anInput.equals("f11")){
                button = KeyEvent.VK_F11;
            } else if(anInput.equals("f12")){
                button = KeyEvent.VK_F12;
            } else if(anInput.equals("scrollLock")){
                button = KeyEvent.VK_SCROLL_LOCK;
            } else if(anInput.equals("break")){
                button = KeyEvent.VK_PAUSE;
            } else if(anInput.equals("numLock")){
                button = KeyEvent.VK_NUM_LOCK;
            } else if(anInput.equals("capsLock")){
                button = KeyEvent.VK_CAPS_LOCK;
            } else if(anInput.equals("backspace")){
                button = KeyEvent.VK_BACK_SPACE;
            } else if(anInput.equals("insert")){
                button = KeyEvent.VK_INSERT;
            } else if(anInput.equals("delete")){
                button = KeyEvent.VK_DELETE;
            } else if(anInput.equals("home")){
                button = KeyEvent.VK_HOME;
            } else if(anInput.equals("end")){
                button = KeyEvent.VK_END;
            } else if(anInput.equals("pageUp")){
                button = KeyEvent.VK_PAGE_UP;
            } else if(anInput.equals("pageDown")){
                button = KeyEvent.VK_PAGE_DOWN;
            }
        }
        return button;
    }
    
    @Override
    public String toString(){
        StringBuilder rVal = new StringBuilder();
        
        rVal.append(this.input);
        if(this.getDuration() > 0){
            rVal.append('(');
            rVal.append(this.getDuration());
            
            if(this.isContinuing()){
                rVal.append(", continue");
            }
            
            rVal.append(')');
        }        
        
        return rVal.toString();
    }
    
}
