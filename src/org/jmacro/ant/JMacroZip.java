package org.jmacro.ant;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.jmacro.MacroMain;

public class JMacroZip extends Zip {

    @Override
    public void setDestFile(File destFile){
        String newZipFilePath = destFile.getAbsolutePath() + "_" + MacroMain.version.replaceAll("\\.", "_") + ".zip";
        log("Setting zip file to: " + newZipFilePath, Project.MSG_INFO);
        super.setDestFile(new File(newZipFilePath));
    }

}
