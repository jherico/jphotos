package org.saintandreas.media;

import java.nio.file.Path;

public class Photos extends FileFilter {

    public static final String[] EXTENSIONS = { ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".tif" };

    public static boolean isPhotoPath(Path path) {
        return hasExtension(path, EXTENSIONS);
    }

}
