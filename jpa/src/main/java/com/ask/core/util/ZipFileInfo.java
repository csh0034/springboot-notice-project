package com.ask.core.util;

import java.io.File;
import java.io.Serializable;

public class ZipFileInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1992213013270452427L;
    private String folderName;
    private String fileName;
    private File file;

    public ZipFileInfo(String folderName, String fileName, File file) {
        this.folderName = folderName;
        this.fileName = fileName;
        this.file = file;
    }

    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
}
