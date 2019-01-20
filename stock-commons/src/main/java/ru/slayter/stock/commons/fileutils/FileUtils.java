package ru.slayter.stock.commons.fileutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FileUtils {

	/**
	 * Creates a File if the file does not exist, or returns a
	 * reference to the File if it already exists.
	 */
	public File createOrRetrieve(final String target) throws IOException{

	    final Path path = Paths.get(target);

	    if(Files.notExists(path)){
	        return Files.createFile(Files.createDirectories(path)).toFile();
	    }
	    return path.toFile();
	}

	/**
	 * Deletes the target if it exists then creates a new empty file.
	 */
	public File createOrReplaceFileAndDirectories(final String target) throws IOException{

	    final Path path = Paths.get(target);
	    // Create only if it does not exist already
	    Files.walk(path)
	        .filter(p -> Files.exists(p))
	        .sorted(Comparator.reverseOrder())
	        .forEach(p -> {
	            try{
	                Files.createFile(Files.createDirectories(p));
	            }
	            catch(IOException e){
	                throw new IllegalStateException(e);
	            }
	        });

	    return Files.createFile(
	        Files.createDirectories(path)
	    ).toFile();
	}	
	
}
