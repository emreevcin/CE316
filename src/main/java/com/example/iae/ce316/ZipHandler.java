package com.example.iae.ce316;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHandler {
    public static void unzip(File zippedFile, int option, String dirName) throws IOException {
        // option 0 for configurations , option 1 for submissions
        String dir = option == 0 ? "src\\main\\configurations\\" : "src\\main\\projects\\";
        byte[] buffer = new byte[2048];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                continue;
            } else {
                File newFile = new File(dir);

                if (newFile.exists() && newFile.isDirectory()) {
                    File[] files = newFile.listFiles();
                    for (File file : files) {
                        if (file.getName().equals(dirName)) {
                            file.delete();
                        }
                    }
                } else {
                    System.err.println("Directory does not exist: " + dir);
                    // Handle the error accordingly
                }
                newFile = new File(dir + dirName);
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
