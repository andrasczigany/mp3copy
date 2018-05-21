package filecopy;

import java.io.File;
import java.util.Vector;

/**
 * @author Andras Czigany - SIEMENS PSE, ECT AES4
 * 
 * Description:
 *
 **/
class FileCopyThread extends Thread {
    private FileCopyFrame frame = null;
    
    public FileCopyThread(FileCopyFrame f) {
        super();
        frame = f;
        this.start();
    }
    
    public void run() {
        Vector success = new Vector();
        boolean ok = true;
        boolean runon = true;

        for (int i = 0; i < frame.getList().getModel().getSize(); i++) {
            File f = (File)frame.getList().getModel().getElementAt(i);

            if ( f.isDirectory()) {
                for (int j = 0; j < f.listFiles().length; j++)
                    if (!copy(f.listFiles()[j].getAbsolutePath(), frame.getDirField().getText() + "\\" + f.getName() + "\\"))
                        ok = false;
                    if (ok) success.add(f);
            }
            else {
                if (copy(f.getAbsolutePath(), frame.getDirField().getText()))
                    success.add(f);
            }
        }
        
        frame.removeFiles(success.toArray());
        if (frame.getStopCopying()) {
            frame.setStopCopying(false);
        }
    }
    
    private boolean copy(String f, String t) {
        boolean success = true;
        if (frame.getStopCopying()) return false;
        if (!FileCopy.getFileCopy().copyTo(f, t))
            success = false;
        return success;
    }
}
