import java.io.*;
import java.util.Arrays;

public class ConcurrentRequestSimulationMax {

    public static void main(String[] args) throws IOException {
        quickSim();
    }

    public static void quickSim() throws IOException {
        // Inputs
        double[] hbRequests = new double[] { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 };
        int[] searchLengths = new int[] { 10, 15, 20, 25 };
        int bcAge = 50;

        // Re-used inputs
        int visitsPerYearLB = 1;
        int visitsPerYearUB = 7;
        double imageSizeLB = 1.0;
        double imageSizeUB = 3.0;
        int imageCountLB = 1;
        int imageCountUB = 5;
        double imageProb = 0.05;
        double textSizeLB = 0.003;
        double textSizeUB = 0.004;
        double growthRate = 1.03;
        double extractTime = 1.0 / 50;
        double exportTime = 1.0 / 60;
        double compileTime = 1.0 / 50;
        double networkTime = 0.5;
        double indexFileSize = 100;

        FileWriter fw = new FileWriter("output.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int requests = 0; requests < hbRequests.length; requests++) {

            bw.write(String.format("%.0f", hbRequests[requests]) + "\t");
            for (int q = 0; q < 30; q++) {

                double overallAvgTime = 0;
                double[] sp = new double[10];

                for (int request = 0; request < hbRequests[requests]; request++) {

                    // Re-used from individual request time calculation
                    // Get search length at random
                    int searchLength = getSearchLength(searchLengths);
                    // Make sure year not negative, if search time greater than bcAge
                    int check = Math.max(bcAge - searchLength, 0);
                    for (int year = check; year < bcAge - 5; year++) {
                        double size = 0.0;
                        // For each visit
                        int visits = getRandom(visitsPerYearLB, visitsPerYearUB);
                        for (int visit = 0; visit < visits; visit++) {
                            if (Math.random() <= imageProb) {
                                int images = getRandom(imageCountLB, imageCountUB);
                                for (int image = 0; image < images; image++) {
                                    size += getRandom(imageSizeLB, imageSizeUB) * Math.pow(growthRate, year / 5.0);
                                }
                            }
                            size += getRandom(textSizeLB, textSizeUB) * Math.pow(growthRate, year / 5.0);
                        }
                        // Check for searching historical blockchain
                        overallAvgTime += extractTime * size + exportTime * size;
                    }
                    // Add time for network and index file reading
                    overallAvgTime += searchLength <= bcAge
                            ? (Math.ceil(searchLength / 5.0) - 1) * (indexFileSize * extractTime)
                            : (Math.ceil(bcAge / 5.0) - 1) * (indexFileSize * extractTime);
                    overallAvgTime += networkTime;
                    assign(sp, overallAvgTime);
                    overallAvgTime = 0;
                }

                for (double t : sp) {
                    if (t > overallAvgTime)
                        overallAvgTime = t;
                }
                bw.write(overallAvgTime + "\t");
            }
            bw.write("\n");
        }

        bw.close();
        fw.close();
    }

    public static void assign(double[] sp, double overallAvgTime) {
        Arrays.sort(sp);
        sp[0] += overallAvgTime;
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static double getRandom(double min, double max) {
        return (double) (Math.random() * (max - min) + min);
    }

    public static int getSearchLength(int[] searchLengths) {
        double prob = Math.random();
        if (prob < 10.0 / 90.0)
            return 45;
        else if (prob < 20.0 / 90.0)
            return 40;
        else if (prob < 30.0 / 90.0)
            return 35;
        else if (prob < 40.0 / 90.0)
            return 30;
        else if (prob < 50.0 / 90.0)
            return 25;
        else if (prob < 60.0 / 90.0)
            return 20;
        else if (prob < 70.0 / 90.0)
            return 15;
        else if (prob < 80.0 / 90.0)
            return 10;
        else
            return 5;
    }
}