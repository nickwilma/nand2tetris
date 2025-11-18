import java.util.ArrayList;
import java.util.List;

public abstract class SourceParser {

    public static List<String> fromString(String source) {
        List<String> result = new ArrayList<String>();

        String[] parsed = source.split("\n");
        for (String item : parsed) {
            item = removeCommentary(item);
            item = removeWhitespace(item);

            if(!isEmptyString(item)) {
                result.add(item);
            }
        }

        return result;
    }

    private static String removeCommentary(String str) {
        String[] withoutCommentary = str.split("//");
        if(withoutCommentary.length > 0) {
            return withoutCommentary[0];
        }
        return str;
    }

    private static String removeWhitespace(String str) {
        return str.replaceAll(" ", "");
    }

    private static boolean isEmptyString(String str) {
        return str.length() == 0;
    }
}
