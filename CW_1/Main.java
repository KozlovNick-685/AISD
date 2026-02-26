public class Main {
	public static void main(String[] args) {
		int a = 3;
		int n = 3;
		int m = 2;
		int promegh = a%m;
		for(int j = 1; j<= n; j++){
			promegh*=promegh;
		}
		System.out.println(promegh);
	}
}