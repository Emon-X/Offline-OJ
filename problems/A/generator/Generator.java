import java.util.Random;

public class Generator {
    public static void main(String[] args) {
        Random rand = new Random();
        int a = rand.nextInt(1000);
        int b = rand.nextInt(1000);
        System.out.println(a + " " + b);
    }
}
