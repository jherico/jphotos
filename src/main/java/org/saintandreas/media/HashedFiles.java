package org.saintandreas.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class HashedFiles {
    
    @SuppressWarnings("deprecation")
    private static HashFunction MD5 = Hashing.md5();
    
    private static final int MAX_INLINE_HASH_SIZE = 1 << 30; 
    
    public static HashCode getFileHash(Path source) throws IOException {
        File sourceFile = new File(source.toString());
        if (sourceFile.length() > MAX_INLINE_HASH_SIZE) {
            return getLargeFileHash(sourceFile);
        } 
        
        return MD5.hashBytes(Files.readAllBytes(source));
    }

    private static HashCode getLargeFileHash(File source) throws IOException  {
        Hasher hasher = MD5.newHasher(MAX_INLINE_HASH_SIZE);

        byte[] buffer = new byte[MAX_INLINE_HASH_SIZE];
        int readCount = 0;
        
        try(FileInputStream in = new FileInputStream(source)) {
            for (readCount = in.read(buffer); readCount != -1; readCount = in.read(buffer)) {
                hasher.putBytes(buffer, 0, readCount);
            }
        } catch (IOException e) {
            throw e;
        }
        
        return hasher.hash();
    };
    
    public static Path getTargetPath(Path source, String dest) throws IOException {
        return getTargetPath(source, getFileHash(source), dest);
    }

    public static boolean migrateToHashedPath(Path source, String dest) {
        try {
            moveToPath(source, HashedFiles.getTargetPath(source, dest));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Path getTargetPath(Path source, byte[] data, String dest) throws IOException {
        return getTargetPath(source, MD5.hashBytes(data), dest);
    }
    
    public static Path getTargetPath(Path source, HashCode hash, String dest) throws IOException {
        String hashString = hash.toString();
        String prefix = Joiner.on('/').join(Splitter.fixedLength(1).split(hashString.substring(0, 4)));
        
        String pathString = source.toString().toLowerCase();
        String extension = pathString.substring(pathString.lastIndexOf('.'));
        return Paths.get(dest, prefix, hashString + extension);
    }
    
    public static void moveToPath(Path source, Path dest) throws IOException {
        File destFolder = new File(dest.toString()).getParentFile(); 
        if (!destFolder.exists() && !destFolder.mkdirs()) {
            throw new IOException("Could not create path");
        }
        System.out.println(source.toString() + " -> " + dest);
        Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
    }
}
