package utils;

public class FileProcessingUtils {
    // params: file content, string to be searched, one line comment mark,
    // multi line start comment mark, multi line end comment mark
    // MIGHT GET CONFUSED BY "//*" strings
    public static boolean stringExistsAsComment(String content, String str, String olComm, String sComm, String eComm) {
        int strPos = content.indexOf(str); // check if string exists
        boolean existsAndIsInComm = false;

        if(strPos != -1) { // if string is found
            if(content.substring(0, strPos).contains(sComm) && content.substring(strPos).contains(eComm))
            {
                existsAndIsInComm = true;
            } else {
                // search string's line
                int newLineInd = 0;
                for(int i = strPos; i >= 0 && content.charAt(i) != '\n'; i--) {
                    newLineInd = i;
                }

                if(content.substring(newLineInd, strPos).contains(olComm)) {
                    existsAndIsInComm = true;
                }
            }
        }

        return existsAndIsInComm;
    }

    public static boolean stringExistsAndIsNotComment(String content, String str, String olComm, String sComm, String eComm) {
        int strPos = content.indexOf(str); // check if string exists
        boolean existsAndIsNotComm = false;

        if(strPos != -1) { // if string is found
            if(!(content.substring(0, strPos).contains(sComm) && content.substring(strPos).contains(eComm)))
            {
                // search string's line
                int newLineInd = 0;
                for(int i = strPos; i >= 0 && content.charAt(i) != '\n'; i--) {
                    newLineInd = i;
                }

                if(!content.substring(newLineInd, strPos).contains(olComm)) {
                    existsAndIsNotComm = true;
                }
            }
        }

        return existsAndIsNotComm;
    }
}
