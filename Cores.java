import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cores {

    int totalCores;
    List<float[]> clientEntries = new ArrayList<>(); // Contains the client request entries read from the input file.
    int[] coreDenominations;

    float[][] profitMarginTable = new float[clientEntries.size() + 1][totalCores + 1];


    Cores(String inputFileDir) {
        // initialize profitMarginTable with -1.
        for (int i = 0; i < this.profitMarginTable.length; i++) {
            for (int j = 0; j < this.totalCores; j++) {
                this.profitMarginTable[i][j] = -1;
            }
        }

        // Set profitMarginTable's row 0 elements to 0.
        for (int j = 0; j < this.profitMarginTable[0].length; j++) {
            this.profitMarginTable[0][j] = 0;
        }

        // Set profitMarginTable's column 0 elements to 0.
        for (int i = 0; i < this.profitMarginTable.length; i++) {
            this.profitMarginTable[i][0] = 0;
        }

        // Check if loadFile() successfully read the specified input file.
        // If so, call findShortestPath().
        if (loadFile(inputFileDir)) {
            System.out.println("Total Cores: " + totalCores);
            for (float[] entry : clientEntries) {
                System.out.printf("%f %f\n", entry[0], entry[1]);

            }
        }

        this.coreDenominations = new int[]{1,2,7,11};
    }

    /**
     * Method used to read the contents (point coordinates) of the specified
     * input file and store them into minePos.
     * @param dir Input file directory.
     * @return True if the specified input file was successfully loaded, False if an error occurred.
     */
    private boolean loadFile(String dir) {
        File file = new File(dir);
        Scanner scanner;
        int lineCount = 0;

        try {
            String[] clientString;
            String line;
            scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                lineCount += 1;

                if (lineCount == 1) {
                    // When the first line is read, store the total amount of available cores
                    // retrieved in the totalCores property.
                    this.totalCores = Integer.parseInt(line);
                } else {
                    if (!line.equals("")) {
                        clientString = line.split(" ");

                        float[] clientEntry = new float[2];
                        clientEntry[0] = Float.parseFloat(clientString[0]);
                        clientEntry[1] = Float.parseFloat(clientString[1]);

                        this.clientEntries.add(clientEntry);
                    }
                }
            }

            return true;
        } catch (FileNotFoundException e) {
            System.out.println("*** Error: File not Found. ***");
            return false;
        }
    }

    public int[] calculateVMPerClient() {
        int maxCoreRequirement = 0;

        for (float[] entry : clientEntries) {
            int value = (Math.round(entry[0]));
            if (value > maxCoreRequirement) {
                maxCoreRequirement = value;
            }
        }

        int[] vmCoreDistribution = new int[maxCoreRequirement + 1];
        vmCoreDistribution[0] = 0;

        for (int i = 1; i <= maxCoreRequirement; i++) {
            int temp = maxCoreRequirement + 1;
            int j = 0;
            while (j < this.coreDenominations.length && i >= this.coreDenominations[j]) {
                temp = Math.min(vmCoreDistribution[i - this.coreDenominations[j]], temp);
                j += 1;
            }
            vmCoreDistribution[i] = temp + 1;
        }

        return vmCoreDistribution;
    }

    public float calculateMaxProfit(int clientsAmount, int availableCores) {
        if (this.profitMarginTable[clientsAmount][availableCores] < 0) {
            float value;
            if (availableCores < clientEntries.get(clientsAmount)[0]) {
                value = calculateMaxProfit(clientsAmount - 1, availableCores);
            } else {
                value = Math.max(calculateMaxProfit(clientsAmount - 1, availableCores),
                        clientEntries.get(clientsAmount)[1] + calculateMaxProfit(clientsAmount - 1,
                                Math.round(availableCores - clientEntries.get(clientsAmount)[0])));
            }
            this.profitMarginTable[clientsAmount][availableCores] = value;
        }
        return this.profitMarginTable[clientsAmount][availableCores];
    }

    public static void main(String[] args) {
        Cores cores = new Cores(args[0]);
        int[] result = cores.calculateVMPerClient();

        System.out.println("=======================");

//        for (int res : result) {
//            System.out.println(res);
//        }
        System.out.println("Client 1: " + result[1100]);
        System.out.println("Client 2: " + result[1000]);
        System.out.println("Client 3: " + result[21]);
        System.out.println("Client 4: " + result[50]);
        System.out.println("Client 5: " + result[49]);
        System.out.println("Client 6: " + result[15]);
        System.out.println("Client 7: " + result[11010]);
        System.out.println("Client 8: " + result[500]);
        System.out.println("Client 9: " + result[637]);
    }
    
}
