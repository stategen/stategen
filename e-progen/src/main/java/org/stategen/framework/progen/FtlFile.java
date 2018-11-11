package org.stategen.framework.progen;

import java.io.File;

public class FtlFile {
    private File tempPathFile =null;
    private File TempFile =null;
    
    public FtlFile(File tempPathFile, File tempFile) {
        super();
        this.tempPathFile = tempPathFile;
        TempFile = tempFile;
    }

    public File getTempFile() {
        return TempFile;
    }
    
    public File getTempPathFile() {
        return tempPathFile;
    }
    
}
