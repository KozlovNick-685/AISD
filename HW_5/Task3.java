public class Task3 {
    public static void main(String[] args) {
        int[] array1 = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        func(array1,16);

    }
    public static void func(int[] data, int need) {
        int start = 0;
        int end = data.length - 1;
        while (start < end) {
            int current = data[start] + data[end];
            if (current == need) {
                System.out.println( + data[start] + " и " + data[end]);
                return;
            }
            if (current < need) {
                start = start + 1;
            } else {
                end = end - 1;
            }
        }
        System.out.println("ничего :(");
    }
}