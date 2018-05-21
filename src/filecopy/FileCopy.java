package filecopy;

import gui.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * @author Andras Czigany - SIEMENS PSE, ECT AES4
 * 
 * Description:
 *
 **/
public class FileCopy {
    
    private static FileCopy instance = null;
    private Logger logger = null;
    
    private File inputFile = null;
    private File outputFile = null;
    private File dir = null;
    
    private FileInputStream is = null;
    private FileOutputStream os = null;
    
    private byte[] buf = new byte[8];
    private int i = 0;

            
    private FileCopy() {
        this.logger = Logger.getInstance();
    }
    
    public static FileCopy getFileCopy() {
        if (instance == null) instance = new FileCopy();
        return instance;
    }
    
    private FileInputStream openInputStream(File f) {
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException fnf) {
            logger.log("Error while creating inputstream from file: "+f.getAbsolutePath()+"\n");
            return null;
        }
    }
    
    private FileOutputStream openOutputStream(File f) {
        try {
            return new FileOutputStream(f);
        } catch (FileNotFoundException fnf) {
            logger.log("Error while creating outputstream from file: "+f.getAbsolutePath()+"\n");
            return null;
        }
    }
    
    private boolean createFile(File f) {
        try {
            return f.createNewFile();
        } catch (IOException ie) {
            logger.log("Error while creating destination file: "+f.getAbsolutePath()+"!"+"\n");
            return false;
        }
    }

    // copying url to given directory (which should end by \)
    public boolean copyTo(String url, String to) {
        
        if (to.equals("")) return false;
        if (!to.endsWith("\\")) to = to + "\\";
        
        // checking targetpath
        dir = new File(to);
        if (!dir.exists()) {
            logger.log("Destination directory not found, creating "+to+"\n");
            if (!dir.mkdir()) {	// creating target directory
                logger.log("Error creating destination directory: "+dir.getAbsolutePath()+"\n");
                return false;
            }
        }

        // opening inputstream
        inputFile = new File(url);
        if (!inputFile.exists()) {
            logger.log("Source file not found: "+to+"\n");
            return false;
        }
        is = openInputStream(inputFile);
        
        // opening outputstream
        outputFile = new File(to+inputFile.getName());
        if (outputFile.exists()) {
            if (outputFile.length() == inputFile.length()) {	// if file exists
                logger.log(outputFile.getAbsolutePath()+" already exists."+"\n");
                return true;
            }
        } else createFile(outputFile);
        os = openOutputStream(outputFile);

        // if streams aren´t open
        if (is == null || os == null) return false;

        // copying
        try {
            int p = 0;
            int allp = (new Long(inputFile.length() / buf.length)).intValue();
            
            logger.log("Copying "+inputFile.getName()+"     ");
            while( (i=is.read(buf)) !=-1 && true) { //!gui.getCancelled()) {
                os.write(buf, 0, i);
                p++;
                if ( Math.floor(100*((double)p/(double)allp)) % 10 == 0 && Math.floor(100*((double)(p-1)/(double)allp)) % 10 == 9 )
                    logger.log(".");
            }
            is.close();
            os.close();
//            if (gui.getCancelled()) {
//                gui.log("     CANCELLED\n");
//                gui.setCancelled(false);
//                return false;
//            }
//            else
//                gui.log("     READY   ("+ NumberFormat.getInstance().format(inputFile.length()/1024)+" KBytes copied to "+to+" )\n");
        } catch (IOException ioe) {
            logger.log("ERROR! \n");
        }
        
        return true;
    }

}
