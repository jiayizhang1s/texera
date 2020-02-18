package edu.uci.ics.texera.dataflow.resource.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.uci.ics.texera.api.exception.StorageException;
import edu.uci.ics.texera.api.exception.TexeraException;
import edu.uci.ics.texera.api.utils.Utils;

public class FileManager {
    private static FileManager instance = null;
    private final static Path FILE_CONTAINER_PATH = Paths.get("").resolve("user-resources").resolve("files");
    
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
    
    public void storeFile(InputStream fileStream, String fileName, String userID) {
        createFileDirectoryIfNotExist(getFileDirectory(userID));
        checkFileDuplicate(getFilePath(userID, fileName));
        
        writeToFile(getFilePath(userID, fileName), fileStream);
    }
    
    private void createFileDirectoryIfNotExist(Path directoryPath) {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new TexeraException(e);
            }
        }
    }
    
    private void checkFileDuplicate(Path filePath) throws StorageException {
        if (Files.exists(filePath)) {
            throw new TexeraException("File alread exists");
        }
    }
    
    private void writeToFile(Path filePath, InputStream fileStream) {
        String line;
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))
                ){
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new TexeraException("Error occurred whlie writing file on disk");
        }
    }
    
}
