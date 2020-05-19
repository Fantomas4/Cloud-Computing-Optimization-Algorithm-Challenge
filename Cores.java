import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cores {

    int totalCores;
    List<float[]> clientEntries = new ArrayList<>(); // Contains the client request entries read from the input file.
    int[] coreDenominations;

    Cores(String inputFileDir) {
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
        int remainingCores = this.totalCores;
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
