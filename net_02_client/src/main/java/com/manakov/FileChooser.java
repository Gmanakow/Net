package com.manakov;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileChooser {
    private File file;
    private FileInputStream fileInputStream = null;

    public FileChooser(String location) throws IOException{
        this.file = new File(location);
    }

    public FileInputStream getFileInputStream() throws IOException{
        return new FileInputStream(
                this.file
        );
    }

    public String getFileName(){
        return this.file.getName();
    }

    public Long getFileLength(){
        return this.file.length();
    }
}
