package com.example.iae.ce316;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHandler {
    public static void unzip(File zippedFile, int option) throws IOException {
        // option 0 for configurations , option 1 for submissions
        String dir = option == 0 ? "src\\main\\configurations\\" : "src\\main\\submissions\\";
        byte[] buffer = new byte[2048];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                continue;
            } else {
                String zipFilePath = zippedFile.getAbsolutePath();
                String zipFileName = zipFilePath.substring(zipFilePath.lastIndexOf("\\") + 1, zipFilePath.lastIndexOf("."));
                File newFile = new File(dir + zipFileName);
                if (newFile.exists()) {
                    newFile.delete();
                }
                newFile.mkdir();
                System.out.println(newFile.getAbsolutePath() + "\\" + zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(newFile.getAbsolutePath() + "\\" + zipEntry.getName());
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();

            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
}
