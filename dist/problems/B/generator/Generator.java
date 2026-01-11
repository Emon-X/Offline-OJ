import java.util.Random;

public class Generator {
    public static void main(String[] args) {
        Random rand = new Random();
        int n = rand.nextInt(100) + 1; // 1 to 100
        int x = rand.nextInt(50); // Search target

        System.out.println(n + " " + x);
        for (int i = 0; i < n; i++) {
            System.out.print(rand.nextInt(50) + " ");
        }
        System.out.println();
    }
}
