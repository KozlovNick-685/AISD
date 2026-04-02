import java.util.ArrayList;
import java.util.List;

public class Task2 {
    public static void main(String[] args) {
        String[] str = new String[]{"Нео", "Воробей", "Джони"};
        System.out.println(func(str));
    }

    public static List<String> func(String[] array) {
        List<String> list = new ArrayList<>();
        for (String word : array) {
            list.add(word);
        }

        list.sort(new java.util.Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                if (a.equals(b)) return 0;

                int minLen = Math.min(a.length(), b.length());
                for (int i = 0; i < minLen; i++) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return a.charAt(i) - b.charAt(i);
                    }
                }
                return a.length() - b.length();
            }
        });

        return list;
    }
}