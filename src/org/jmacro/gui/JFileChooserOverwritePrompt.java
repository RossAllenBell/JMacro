
package org.jmacro.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class JFileChooserOverwritePrompt extends JFileChooser {
    
    private static final long serialVersionUID = 3L;

    public JFileChooserOverwritePrompt(String currentDirectoryPath){
        super(currentDirectoryPath);
    }

    @Override
    public void approveSelection() {
        File selectedFile = getSelectedFile();
        if (selectedFile != null && selectedFile.exists()) {
            int lResponse = JOptionPane.showConfirmDialog(this,
                    "The file " + selectedFile.getName() + " already exists. Do you want to replace the existing file?",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (lResponse != JOptionPane.YES_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }
}
