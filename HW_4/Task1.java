public class Task1 {
    public static void main(String[] args) {
        System.out.println(findShift("abc", "cab"));
        System.out.println(findShift("abc", "bca"));
        System.out.println(findShift("abc", "abc"));
        System.out.println(findShift("abc", "acb"));
        System.out.println(findShift("hello", "llohe"));
        System.out.println(findShift("hello", "elloh"));
    }

    public static boolean findShift(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        String s3 = s1 + s1;
        for (int i = 0; i <= s3.length() - s2.length(); i++) {
            if (s3.startsWith(s2, i)) {
                return true;
            }
        }
        return false;
    }
}