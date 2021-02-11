package util;

public class CountUtil {
    public static int count(String str) {
        if (str == null || str.length() == 0)
            return 0;
        int count = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++)
            count += chars[i] > 0xff ? 2 : 1;
        return count;
    }
}
