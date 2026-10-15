package com.passerelle.utils;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UploadConfig {

    private static String baseDirName = "uploads";
    private static String dirImage = "images";
    private static String dirCsv = "csv";
    private static String dirPdf = "pdf";
    private static String dirOther = "others";
    private static long maxSize = 10485760L;

    private static String baseDirAbsolute;

    /**
     * Charge la config et cree les dossiers dans le projet web.
     * @param context Le ServletContext (fournit getRealPath)
     */
    public static void load(ServletContext context) {
        Properties props = loadFromClasspath("utils/upload.properties");

        if (props != null) {
            baseDirName = props.getProperty("upload.base.dir", baseDirName);
            dirImage    = props.getProperty("upload.dir.image", dirImage);
            dirCsv      = props.getProperty("upload.dir.csv", dirCsv);
            dirPdf      = props.getProperty("upload.dir.pdf", dirPdf);
            dirOther    = props.getProperty("upload.dir.other", dirOther);
            maxSize     = Long.parseLong(props.getProperty("upload.max.size", String.valueOf(maxSize)));
        }

        // determiner le chemin de projet 
        String realPath = context.getRealPath("/");
        if (realPath == null) {
            realPath = System.getProperty("user.dir");
        }
        baseDirAbsolute = new File(realPath, baseDirName).getAbsolutePath();

        // creation du dossier uploads
        createDirs();

    }

    private static void createDirs() {
        File base = new File(baseDirAbsolute);
        if (!base.exists()) base.mkdirs();

        new File(base, dirImage).mkdirs();
        new File(base, dirCsv).mkdirs();
        new File(base, dirPdf).mkdirs();
        new File(base, dirOther).mkdirs();
    }

    private static Properties loadFromClasspath(String fileName) {
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName)) {

            if (in == null) return null;

            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getBaseDir()  { return baseDirAbsolute; }
    public static String getDirImage() { return dirImage; }
    public static String getDirCsv()   { return dirCsv; }
    public static String getDirPdf()   { return dirPdf; }
    public static String getDirOther() { return dirOther; }
    public static long getMaxSize()    { return maxSize; }
}