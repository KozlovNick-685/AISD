public class Task1 {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 3, 7, 9};
        int target = 6;
        int index = 0;
        for(int i = 0; i < arr.length; i++) {
            if (arr[i] > target) {
                index = i - 1;
                System.out.println(index);
                break;
            }
        }
    }
}
