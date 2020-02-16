package edu.uci.ics.texera.dataflow.resource.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.uci.ics.texera.api.exception.StorageException;
import edu.uci.ics.texera.api.utils.Utils;

public class FileManager {
    private static FileManager instance = null;
    private final static Path FILE_CONTAINER_PATH = Utils.getTexeraHomePath().resolve("user-resources").resolve("files");
    
    private FileManager() {}
    
    public static FileManager getInstance() {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }
    
    public static Path getFileDirectory(String userID) {
        return FILE_CONTAINER_PATH.resolve(userID);
    }
    
    public static Path getFilePath(String userID, String fileName) {
        return getFileDirectory(userID).resolve(fileName);
    }
    
    public void storeFile(InputStream fileStream, String fileName, String userID, double fileID) {
        Path fileDirectory = getFileDirectory(userID);
        createFileDirectoryIfNotExist(fileDirectory);
        checkFileDuplicate(getFilePath(userID, fileName));
        
        //TODO store file
    }
    
    private void createFileDirectoryIfNotExist(Path directoryPath) {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new StorageException(e);
            }
        }
    }
    
    private void checkFileDuplicate(Path filePath) throws StorageException {
        if (Files.exists(filePath)) {
            throw new StorageException("File alread exists");
        }
    }
}
