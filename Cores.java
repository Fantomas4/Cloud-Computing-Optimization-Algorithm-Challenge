import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cores {

    int totalCores;
    List<float[]> clientEntries = new ArrayList<>(); // Contains the client request entries read from the input file.
    int[] coreDenominations; // Contains the available core denominations (possible numbers of cores per VM).
    float[][] profitMarginTable; // Table used to implement the memory function method for the knapsack problem.
    int[] vmDistributionResults; // Array that contains the minimum amount of VMs required for every core request
                                 // made by the clients.
    double maxProfitMargin; // Holds the value of the maximum profit that could be made if the available cores where
                            // distributed to the clients in the most profitable way possible.

    Cores(String inputFileDir) {
        // Check if loadFile() successfully read the specified input file.
        if (loadFile(inputFileDir)) {
            this.coreDenominations = new int[]{1,2,7,11};
            this.profitMarginTable = new float[clientEntries.size() + 1][totalCores + 1];

            // Initialize profitMarginTable with -1.
            for (int i = 0; i < this.profitMarginTable.length; i++) {
                for (int j = 0; j < this.profitMarginTable[0].length; j++) {
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
            
            executeOperations();
        }
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

    /**
     * Calculates the minimum amount of VMs needed to satisfy each client's request (core number).
     * Based on the "Coin Change" algorithm from the book "Introduction to The Design and
     * Analysis of Algorithms - Anany Levitin"
     * @return An array of integers that represents the minimum amount of VMs needed per core amount request.
     * Each index of the array corresponds to an amount of cores requested, e.g. index 112 contains the minimum
     * amount of VMs for a client request of 112 cores.
     */
    private int[] calculateVMsPerClient() {
        // Find the maximum number of cores requested
        // in any of the client requests
        int maxCoreRequirement = 0;
        for (float[] entry : clientEntries) {
            int value = (Math.round(entry[0]));
            if (value > maxCoreRequirement) {
                maxCoreRequirement = value;
            }
        }

        // Create an integer array that will hold the results (minimum amount of VMs required)
        // for every amount of cores requested. The array is of size maxCoreRequirement + 1, since
        // index 0 is initialized with 0.
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

    /**
     * Calculates the maximum profit that can by achieved given a number of clients and the amount of available cores
     * to be distributed. Based on the memory function method for the knapsack problem from the book "Introduction to The Design and
     * Analysis of Algorithms - Anany Levitin".
     * @param clientsAmount The amount of clients for which the calculation will be performed.
     * @param availableCores The amount of cores available for distribution amongst the clients.
     * @return A float number representing the maximum profit achieved.
     */
    private float calculateMaxProfit(int clientsAmount, int availableCores) {
        if (this.profitMarginTable[clientsAmount][availableCores] < 0) {
            float value;
            if (availableCores < clientEntries.get(clientsAmount - 1)[0]) {
                value = calculateMaxProfit(clientsAmount - 1, availableCores);
            } else {
                value = Math.max(calculateMaxProfit(clientsAmount - 1, availableCores),
                        (clientEntries.get(clientsAmount - 1)[1] * clientEntries.get(clientsAmount -1)[0]) +
                                calculateMaxProfit(clientsAmount - 1, Math.round(availableCores -
                                        clientEntries.get(clientsAmount - 1)[0])));
            }
            this.profitMarginTable[clientsAmount][availableCores] = value;
        }

        return this.profitMarginTable[clientsAmount][availableCores];
    }

    /**
     * Prints the results of operation A and operation B (calculateVMsPerClient() and calculateMaxProfit())
     * in the format specified by the assignment's description.
     */
    private void printResults() {

        // Print the results of operation A
        for (int i = 0; i < this.clientEntries.size(); i++) {
            System.out.printf("Client %d: %d VMs\n", i + 1, this.vmDistributionResults[Math.round(this.clientEntries.get(i)[0])]);
        }

        // Print the result of operation B
        System.out.printf("Total amount: %.3f", this.maxProfitMargin);
    }

    /**
     * Executes the necessary calculations by calling calculateVMsPerClient() and calculateMaxProfit()
     */
    private void executeOperations() {
        this.vmDistributionResults = calculateVMsPerClient();
        this.maxProfitMargin = calculateMaxProfit(this.clientEntries.size(), this.totalCores);
        printResults();
    }

    public static void main(String[] args) {
        Cores cores = new Cores(args[0]);
    }
}
