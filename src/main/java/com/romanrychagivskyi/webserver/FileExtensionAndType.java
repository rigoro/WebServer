package com.romanrychagivskyi.webserver;

/**
 * All supported extensions
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

    /**
     * Checks if file is allowed by file extension
     *
     * @param fileName
     * @return
     */
    public static boolean isAllowed(String fileName) {
        for (FileExtensionAndType ext : FileExtensionAndType.values()) {
            if (fileName.toLowerCase().endsWith(ext.ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets content type by file extension
     *
     * @param fileName
     * @return
     */
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
