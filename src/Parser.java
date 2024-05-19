package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private int n, m, v, vmax, tau;
    private double mu, rho, delta;
    private int[][] C;

    public Parser(String[] args) {
        if (args.length == 2 && args[0].equals("-f")) {
            readFromFile(args[1]);
        } else if (args.length == 9 && args[0].equals("-r")) {
            readFromCommandLine(args);
        } else {
            System.err.println("Invalid arguments");
            return;
        }
    }

    private void readFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            n = scanner.nextInt();
            m = scanner.nextInt();
            tau = scanner.nextInt();
            v = scanner.nextInt();
            vmax = scanner.nextInt();
            mu = scanner.nextDouble();
            rho = scanner.nextDouble();
            delta = scanner.nextDouble();
            C = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    C[i][j] = scanner.nextInt();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        }
    }

    private void readFromCommandLine(String[] args) {
        n = Integer.parseInt(args[1]);
        m = Integer.parseInt(args[2]);
        tau = Integer.parseInt(args[3]);
        v = Integer.parseInt(args[4]);
        vmax = Integer.parseInt(args[5]);
        mu = Double.parseDouble(args[6]);
        rho = Double.parseDouble(args[7]);
        delta = Double.parseDouble(args[8]);
        C = generateRandomMatrix(n, m);
    }

    private int[][] generateRandomMatrix(int n, int m) {
        int[][] matrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = (int) (Math.random() * 10 + 1); // Random values between 1 and 10
            }
        }
        return matrix;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getTau() {
        return tau;
    }

    public int getNu() {
        return v;
    }

    public int getNuMax() {
        return vmax;
    }

    public double getMu() {
        return mu;
    }

    public double getRho() {
        return rho;
    }

    public double getDelta() {
        return delta;
    }

    public int[][] getC() {
        return C;
    }
}
