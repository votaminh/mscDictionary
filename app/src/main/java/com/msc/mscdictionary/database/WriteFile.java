package com.msc.mscdictionary.database;

import com.msc.mscdictionary.util.Constant;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class WriteFile {

    public static void unZipFile(String srcFile, String desFile, boolean deleteSrc){
        try {
            File src = new File(srcFile);
            ZipFile zipFile = new ZipFile(src);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(Constant.PASS_ZIP);
            }
            zipFile.extractAll(desFile);

            if(deleteSrc){
                src.delete();
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
