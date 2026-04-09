import java.util.ArrayList;
import java.util.Arrays;

public class Task3 {
    public static void main(String[] args) {
        String str = "Love ITIS!";
        String str1 = "";
        ArrayList words = new ArrayList();
        String word = "";
        int space = 0;

        for (int i = 0; i <= str.length() - 1; i++) {
            if (str.charAt(i) != ' ') {
                word += str.charAt(i);
            }
            if ((str.charAt(i) == ' ') || (i == str.length() - 1)) {
                if (!word.isEmpty()) {
                    space++;
                    words.add(word);
                    word = "";
                }
            }
        }
        for(int i = space - 1; i >= 0; i--) {
            str1 += words.get(i);
            str1 += ' ';
        }
        System.out.println(str1);
    }
}
