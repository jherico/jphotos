package org.saintandreas.media;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;

public class MediaProcessor {
    private static Set<String> getCachedFiles() throws IOException {
        String PHOTOS_MD5_CACHE = "h:/md5sums.txt";
        Pattern HASHFILE = Pattern.compile("^([a-f0-9]{32})\\.\\w{3,4}$");
        File cacheFile = Paths.get(PHOTOS_MD5_CACHE).toFile();
        if (!cacheFile.exists()) {
            List<String> lines = com.google.common.io.Files.readLines(Paths.get("H:/filenames.txt").toFile(), Charsets.UTF_8);
            List<String> hashes = new ArrayList<String>();
            for (String line : lines) {
                String fileName = Paths.get(line).getFileName().toString();
                Matcher m = HASHFILE.matcher(fileName);
                if (m.matches()) {
                    String hash = m.group(1); 
                    hashes.add(hash);
                }
            }
            
            try (BufferedWriter writer = com.google.common.io.Files.newWriter(cacheFile, Charsets.UTF_8)) {
                for (String hash : hashes) {
                    writer.write(hash);
                    writer.newLine();
                }
            }
            
        } 

        Set<String> result = new HashSet<String>();
        result.addAll(com.google.common.io.Files.readLines(cacheFile, Charsets.UTF_8));
        return result;
    }
    

    public static final String PHOTOS_SOURCE_ROOT = "H:/pics";
    public static final String PHOTOS_DEST_ROOT = "H:/picsout";
    public static final String VIDEOS_SOURCE_ROOT = "H:/videos";
    public static final String VIDEOS_DEST_ROOT = "H:/videosout";

    public static void wipeDuplicates() throws IOException {
        Set<String> existingHashes = getCachedFiles();
        List<Path> files = new ArrayList<Path>();
        int count = 0;
        int deletes = 0;
        
        Files.walk(Paths.get(PHOTOS_SOURCE_ROOT)).filter(Photos::isPhotoPath).forEach(source -> {
            files.add(source);
        });
        
        System.out.println("Found " + files.size() + " files");
        for (Path source : files) {
            try {
                if (0 == count % 1000) {
                    System.out.println();
                    System.out.println("" + count);
                } else if (0 == count % 100) {
                    System.out.print(".");
                }
                count++;
                HashCode code = HashedFiles.getFileHash(source);
                if (existingHashes.contains(code.toString())) {
                    source.toFile().delete();
                    deletes++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("Count " + count + " Deletes " + deletes);
    }
    
    public static void main(String[] args) throws IOException {
        Files.walk(Paths.get(PHOTOS_SOURCE_ROOT)).filter(Photos::isPhotoPath).forEach(source -> {
            try {
                Path dest = HashedFiles.getTargetPath(source, PHOTOS_DEST_ROOT);
                HashedFiles.moveToPath(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.walk(Paths.get(VIDEOS_SOURCE_ROOT)).filter(Videos::isVideoFile).forEach(source -> {
            try {
                Path dest = HashedFiles.getTargetPath(source, VIDEOS_DEST_ROOT);
                HashedFiles.moveToPath(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        Files.walk(Paths.get("H:/testFiles")).filter(Photos::isPhotoPath).forEach(source -> {
//            try {
//                JPEG.metadataExample(new File(source.toString()));
//            } catch (IOException | ImageReadException e) {
//                e.printStackTrace();
//            }
//        });

        // metadataExample
    }

}
