package com.passerelle.mapping;

import com.passerelle.utils.UploadConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MapFile {

    private byte[] bytes;
    private String name;
    private String contentType;
    private long size;

    public MapFile() {}

    public MapFile(String name, String contentType, long size, byte[] bytes) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
    }

    public byte[] getBytes() { return bytes; }
    public void setBytes(byte[] bytes) { this.bytes = bytes; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getText() {
        if (bytes == null) return null;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    public boolean isCsv() {
        if (contentType != null && contentType.equals("text/csv")) return true;
        return name != null && name.toLowerCase().endsWith(".csv");
    }

    public boolean isPdf() {
        if (contentType != null && contentType.equals("application/pdf")) return true;
        return name != null && name.toLowerCase().endsWith(".pdf");
    }

    public String getExtension() {
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        if (dot < 0) return null;
        return name.substring(dot + 1).toLowerCase();
    }

    public String resolveSubDir() {
        if (isImage()) return UploadConfig.getDirImage();
        if (isCsv())   return UploadConfig.getDirCsv();
        if (isPdf())   return UploadConfig.getDirPdf();
        return UploadConfig.getDirOther();
    }

    public File saveAuto() throws IOException {
        if (bytes == null) throw new IOException("Aucun contenu");

        File dir = new File(UploadConfig.getBaseDir(), resolveSubDir());
        if (!dir.exists()) dir.mkdirs();

        File dest = new File(dir, name);
        Files.write(dest.toPath(), bytes);
        return dest;
    }

    public String toBase64() {
        if (bytes == null) return null;
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public String toDataUrl() {
        if (bytes == null) return null;
        String type = (contentType != null) ? contentType : "application/octet-stream";
        return "data:" + type + ";base64," + toBase64();
    }

    @Override
    public String toString() {
        return "MapFile{name='" + name + "', type='" + contentType + "', size=" + size + "}";
    }
}