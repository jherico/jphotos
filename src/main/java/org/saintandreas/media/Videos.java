package org.saintandreas.media;

import java.nio.file.Path;

public class Videos extends FileFilter {
    public static final String[] EXTENSIONS = { ".3gp", ".mov", ".mp4", ".m4v", ".mpg", ".mpeg", ".avi", ".wmv" };

    public static boolean isVideoFile(Path path) {
        hasExtension(path, EXTENSIONS);
        String pathString = path.toString().toLowerCase();
        for (String ext : EXTENSIONS) {
            if (pathString.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
