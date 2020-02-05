import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TSP {
    public static String[] file = {"pr76.txt", "berlin52.txt", "st70.txt"};
    public static int n = 0;
    public static Node[] node;
    public static Scanner sc;
    public static String output = "";
    public static double minCost, maxCost, avgCost;
    public static int[] minSource, sminSource, minSource1, sminSource1;
    public static double sminCost, smaxCost, savgCost;
    public static NearestNeighbour nn;
    public static SavingHeuristic sh;
    public static TwoOpt twoOpt;
    public static NearestNeighbour[] bnn1, bnn2, wnn2;
    public static SavingHeuristic[] bsh1, bsh2, wsh2;
    public static TwoOpt[] btonn, btosh, wtonn, wtosh;

    public static void main(String args[]) throws FileNotFoundException {
        //task 1
        minSource = new int[3];
        sminSource = new int[3];
        bnn1 = new NearestNeighbour[3];
        bsh1 = new SavingHeuristic[3];
        for (int i = 0; i < 3; i++) {
            minSource[i] = -1;
            sminSource[i] = -1;
        }
        output += "\n The Greedy Sample Results\n";
        output += "        Avg Case               Best Case              Worst Case\n";
        for (int fs = 0; fs < 3; fs++) {
            init();
            for (int cs = 0; cs < 5; cs++) {
                getInput(file[fs]);
                nn = new NearestNeighbour(node, n, false, -1);
                //nn.printAns();
                if (nn.getCost() < minCost) {
                    minCost = nn.getCost();
                    minSource[fs] = nn.getSource();
                    bnn1[fs] = nn;
                }
                maxCost = Math.max(maxCost, nn.getCost());
                avgCost += nn.getCost();

                sh = new SavingHeuristic(node, n, false, -1);
                //sh.printAns();
                if (sh.getCost() < sminCost) {
                    sminCost = sh.getCost();
                    sminSource[fs] = sh.getSource();
                    bsh1[fs] = sh;
                }
                smaxCost = Math.max(smaxCost, sh.getCost());
                savgCost += sh.getCost();
            }
            avgCost /= 5;
            savgCost /= 5;
            output += "     " + String.format("%.2f", avgCost) + " , " + String.format("%.2f", savgCost) + "         " + String.format("%.2f(%d)", minCost, minSource[fs]) + "," + String.format("%.2f(%d)", sminCost, sminSource[fs]) + "       " + String.format("%.2f", maxCost) + " , " + String.format("%.2f", smaxCost) + "\n";
        }
        /*
        for(int i=0;i<3;i++)
            bnn1[i].printAns();
        for (int i=0;i<3;i++)
            bsh1[i].printAns();
        */
        System.out.println(output);


        //task 2
        bnn2 = new NearestNeighbour[3];
        bsh2 = new SavingHeuristic[3];
        wnn2 = new NearestNeighbour[3];
        wsh2 = new SavingHeuristic[3];
        minSource1 = new int[3];
        sminSource1 = new int[3];
        System.out.println("\n");
        output = "";
        output += "\n The Greedy Randomized Sample Results\n";
        output += "        Avg Case               Best Case              Worst Case\n";
        for (int fs = 0; fs < 3; fs++) {
            init();
            for (int cs = 0; cs < 10; cs++) {
                getInput(file[fs]);
                nn = new NearestNeighbour(node, n, true, minSource[fs]);
                if (nn.getCost() < minCost) {
                    minCost = nn.getCost();
                    minSource1[fs] = nn.getSource();
                    bnn2[fs] = nn;
                }
                if (nn.getCost() > maxCost) {
                    maxCost = nn.getCost();
                    wnn2[fs] = nn;
                }
                avgCost += nn.getCost();

                sh = new SavingHeuristic(node, n, true, sminSource[fs]);
                if (sh.getCost() < sminCost) {
                    sminCost = sh.getCost();
                    sminSource1[fs] = sh.getSource();
                    bsh2[fs] = sh;
                }
                if (sh.getCost() > smaxCost) {
                    smaxCost = nn.getCost();
                    wsh2[fs] = sh;
                }
                savgCost += sh.getCost();
            }
            avgCost /= 10;
            savgCost /= 10;
            output += "     " + String.format("%.2f", avgCost) + " , " + String.format("%.2f", savgCost) + "         " + String.format("%.2f(%d)", minCost, minSource[fs]) + "," + String.format("%.2f(%d)", sminCost, sminSource[fs]) + "       " + String.format("%.2f", maxCost) + " , " + String.format("%.2f", smaxCost) + "\n";
        }

        /*
        for(int i=0;i<3;i++) {
            bnn2[i].printAns();
            wnn2[i].printAns();
        }
        System.out.println();
        for (int i=0;i<3;i++) {
            bsh2[i].printAns();
            wsh2[i].printAns();
        }
        System.out.println();
        */
        System.out.println(output);

        //task 3
        for (int i = 0; i < 3; i++) {
            btonn = new TwoOpt[3];
            btosh = new TwoOpt[3];
            wtonn = new TwoOpt[3];
            wtosh = new TwoOpt[3];
        }
        System.out.println("\n");
        output = "";
        output += "\n The 2Opt for best improvement\n";
        output += "     Best Case              Worst Case\n";
        for (int fs = 0; fs < 3; fs++) {
            init();
            getInput(file[fs]);
            twoOpt = new TwoOpt(bnn2[fs].list, node, n,false);
            btonn[fs] = twoOpt;
            twoOpt = new TwoOpt(bsh2[fs].list, node, n,false);
            btosh[fs] = twoOpt;
            twoOpt = new TwoOpt(wnn2[fs].list, node, n,false);
            wtonn[fs] = twoOpt;
            twoOpt = new TwoOpt(wsh2[fs].list, node, n,false);
            wtosh[fs] = twoOpt;
        }
        for (int i = 0; i < 3; i++) {
            output += "  " + String.format("%.2f", btonn[i].getCost()) + "," + String.format("%.2f", btosh[i].getCost()) + "           " + String.format("%.2f", wtonn[i].getCost()) + " , " + String.format("%.2f", wtosh[i].getCost()) + "\n";
        }
        System.out.printf(output);
        for (int i = 0; i < 3; i++) {
            btonn = new TwoOpt[3];
            btosh = new TwoOpt[3];
            wtonn = new TwoOpt[3];
            wtosh = new TwoOpt[3];
        }
        System.out.println("\n");
        output = "";
        output += "\n The 2Opt for first improvement\n";
        output += "     Best Case              Worst Case\n";
        for (int fs = 0; fs < 3; fs++) {
            init();
            getInput(file[fs]);
            twoOpt = new TwoOpt(bnn2[fs].list, node, n,true);
            btonn[fs] = twoOpt;
            twoOpt = new TwoOpt(bsh2[fs].list, node, n,true);
            btosh[fs] = twoOpt;
            twoOpt = new TwoOpt(wnn2[fs].list, node, n,true);
            wtonn[fs] = twoOpt;
            twoOpt = new TwoOpt(wsh2[fs].list, node, n,true);
            wtosh[fs] = twoOpt;
        }
        for (int i = 0; i < 3; i++) {
            output += "  " + String.format("%.2f", btonn[i].getCost()) + "," + String.format("%.2f", btosh[i].getCost()) + "           " + String.format("%.2f", wtonn[i].getCost()) + " , " + String.format("%.2f", wtosh[i].getCost()) + "\n";
        }
        System.out.printf(output);

    }

    public static void getInput(String fileName) throws FileNotFoundException {
        sc = new Scanner(new File(fileName));
        n = sc.nextInt();
        node = new Node[n];
        int i = 0;
        while (sc.hasNextLine()) {
            node[i] = new Node();
            int temp = sc.nextInt();
            node[i].x = sc.nextDouble();
            node[i].y = sc.nextDouble();
            i++;
        }
    }

    public static void init() {
        minCost = 1000000;
        maxCost = -1000000;
        avgCost = 0;
        sminCost = 1000000;
        smaxCost = -1000000;
        savgCost = 0;
    }
}