package org.jmacro.macroevent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.jmacro.Macro;
import org.jmacro.MacroMain;

public class SynchronizeEvent extends AMacroEvent {
    
    private String remoteIPAdress;
    
    private int localPort;
    
    private int remotePort;
    
    public SynchronizeEvent(String anIPAddress, Macro aMacro){
        this(anIPAddress, MacroMain.defaultServerPort, MacroMain.defaultServerPort, aMacro);
    }
    
    public SynchronizeEvent(String anIPAddress, int aLocalPort, int aRemotePort, Macro aMacro) throws IllegalArgumentException {        
        super(aMacro);
        
        try{
            InetAddress thisIp = InetAddress.getLocalHost();
            InetAddress specifiedIp = InetAddress.getByName(anIPAddress);
            if(thisIp.getHostAddress().equals(specifiedIp.getHostAddress()) && aLocalPort == aRemotePort){
                throw new IllegalArgumentException("The supplied IP address, \"" + anIPAddress + "\", is the same as this machine's local host. This is only valid if the synchronize command is supplied with different local and remote ports");
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("The supplied IP address, \"" + anIPAddress + "\", is an unknown host");
        }
        
        this.remoteIPAdress = anIPAddress;
        this.localPort = aLocalPort;
        this.remotePort = aRemotePort;
    }

    @Override
    protected void runBegin() {
        Socket socket = null;
        try{
            System.out.println("Attemping to synchronize");
            socket = new Socket(this.remoteIPAdress, this.remotePort);
            socket.close();
            System.out.println("Synchronized with remote JMacro that was already waiting");
        } catch (IOException e){
            try{
                System.out.println("Beat remote JMacro to synchronization point");
                if(socket != null){
                    socket.close();
                }
                ServerSocket serverSocket = new ServerSocket(this.localPort);
                System.out.println("Waiting for remote JMacro to reach synchronization point");
                serverSocket.accept();
                serverSocket.close();
                System.out.println("Synchronized with remote JMacro");
            } catch (SocketException e2){
                e2.printStackTrace();
            } catch (IOException e2){
                e2.printStackTrace();
            }          
        }
    }

    @Override
    protected void runEnd() {
        //no action
    }

    @Override
    public String toString() {
        StringBuilder rVal = new StringBuilder();
        
        rVal.append("synchronize(");
        rVal.append(this.remoteIPAdress);
        if(this.localPort != MacroMain.defaultServerPort || this.remotePort != MacroMain.defaultServerPort){
            rVal.append(',');
            rVal.append(this.localPort);
            rVal.append(',');
            rVal.append(this.remotePort);
        }        
        rVal.append(')');  
        
        return rVal.toString();
    }

}
