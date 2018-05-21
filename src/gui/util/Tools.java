package gui.util;

public class Tools {

    public static boolean isFileMatching(String fileName, String[] filter) {
        if (fileName == null || filter == null)
            return false;
        if (filter.length == 0)
            return true;
        for (int f = 0; f < filter.length; f++)
            if (fileName.toLowerCase().indexOf(filter[f].toLowerCase()) > -1)
                return true;
        return false;
    }
}
