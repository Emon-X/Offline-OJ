import java.util.Scanner;

public class ModelSolution {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int n = scanner.nextInt();
            int x = scanner.nextInt();
            int index = -1;
            for (int i = 0; i < n; i++) {
                int val = scanner.nextInt();
                if (val == x && index == -1) {
                    index = i;
                }
            }
            System.out.println(index);
        }
    }
}
