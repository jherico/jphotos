package org.saintandreas.media;

import java.nio.file.Path;

public class FileFilter {
    
    public static boolean hasExtension(Path path, String[] extensions) {
        String pathString = path.toString().toLowerCase();
        for (String ext : extensions) {
            if (pathString.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

}
