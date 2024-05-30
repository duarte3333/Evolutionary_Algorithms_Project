package src.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class to parse the input arguments and read the input file.
 */
public class Parser {
    private static Parser instance;
    private int n, m, v, vmax, tau;
    private double mu, rho, delta;
    private int[][] C;

    private Parser(String[] args) {
        //Private constructor to prevent instantiation
        if (args.length == 2 && args[0].equals("-f")) {
            readFromFile(args[1]);
        } else if (args.length == 9 && args[0].equals("-r")) {
            readFromCommandLine(args);
        } else {
            System.err.println("Invalid arguments");
            return;
        }
    }

    /**
     * Returns the singleton instance of the Parser class.
     *
     * @param args The command-line arguments.
     * @return The singleton instance of the Parser class.
     */
    public static Parser getInstance(String[] args) {
        if (instance == null) {
            instance = new Parser(args);
        }
        return instance;
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

    /**
     * Gets the number of patrols.
     *
     * @return The number of patrols.
     */
    public int getN() {
        return n;
    }

    /**
     * Gets the number of systems.
     *
     * @return The number of systems.
     */
    public int getM() {
        return m;
    }

    /**
     * Gets the final instant of evolution.
     *
     * @return The final instant of evolution.
     */
    public int getTau() {
        return tau;
    }

    /**
     * Gets the initial population size.
     *
     * @return The initial population size.
     */
    public int getNu() {
        return v;
    }

    /**
     * Gets the maximum population size.
     *
     * @return The maximum population size.
     */
    public int getNuMax() {
        return vmax;
    }

    /**
     * Gets the mutation rate.
     *
     * @return The mutation rate.
     */
    public double getMu() {
        return mu;
    }

    /**
     * Gets the reproduction rate.
     *
     * @return The reproduction rate.
     */
    public double getRho() {
        return rho;
    }

    /**
     * Gets the comfort threshold.
     *
     * @return The comfort threshold.
     */
    public double getDelta() {
        return delta;
    }

    /**
     * Gets the time required by each patrol to pacify each system.
     *
     * @return The time matrix.
     */
    public int[][] getC() {
        return C;
    }
}
