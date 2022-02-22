package HomeworkGatherer.utils;

public class FilenameUtils {
    public static String getExtension(String f) {
        return f.substring(f.lastIndexOf(".") + 1);
    }
    public static String removeExtension(String str) {
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }
}
