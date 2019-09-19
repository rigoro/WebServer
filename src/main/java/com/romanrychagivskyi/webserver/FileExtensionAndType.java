package com.romanrychagivskyi.webserver;

/**
 * Enum class with all supported extensions
 */
enum FileExtensionAndType {

    TXT(".txt", "text/plain"),
    HTML(".html", "text/html"),
    HTM(".htm", "text/html"),
    JPG(".jpg", "image/jpg"),
    JPEG(".jpeg", "image/jpeg"),
    GIF(".gif", "image/gif");

    private final String ext;
    private final String contentType;

    FileExtensionAndType(String ext, String contentType) {
        this.ext = ext;
        this.contentType = contentType;
    }

    public static boolean isAllowed(String fileName) {
        for (FileExtensionAndType ext : FileExtensionAndType.values()) {
            if (fileName.toLowerCase().endsWith(ext.ext)) {
                return true;
            }
        }
        return false;
    }

    public static String getContentType(String fileName) {
        for (FileExtensionAndType ext : FileExtensionAndType.values()) {
            if (fileName.toLowerCase().endsWith(ext.ext)) {
                return ext.contentType;
            }
        }
        return HTML.contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
