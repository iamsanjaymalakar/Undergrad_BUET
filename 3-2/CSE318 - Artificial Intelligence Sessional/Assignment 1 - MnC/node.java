public class node {
    public int numMissionaries,numCannibals;
    public boolean side; // left 0
    public node parent;
    public int level;

    public node(int numMissionaries, int numCannibals, boolean side, int level) {
        this.numMissionaries = numMissionaries;
        this.numCannibals = numCannibals;
        this.side = side;
        this.level = level;
    }

    public node(int numMissionaries, int numCannibals, boolean side, node parent, int level) {
        this.numMissionaries = numMissionaries;
        this.numCannibals = numCannibals;
        this.side = side;
        this.parent = parent;
        this.level = level;
    }

    public void printNode(){
        if(parent!=null)
            parent.printNode();
        System.out.print(numMissionaries+"M,"+numCannibals+"C ");
        if(!side)
            System.out.print("<-Boat left   ");
        else
            System.out.print("  Boat right-> ");
        System.out.println((Main.nMissionaries-numMissionaries)+"M,"+(Main.nCanniabals-numCannibals)+"C");
    }

    public boolean equals(node temp) {
        return (numMissionaries==temp.numMissionaries &&
                numCannibals==temp.numCannibals &&
                side==temp.side);
    }

    public boolean valid(){
        // 0 check
        if(numMissionaries<0 || numCannibals<0 ||
        numMissionaries>Main.nMissionaries || numCannibals>Main.nCanniabals)
            return false;
        // not outnumbered side 1
        if(numMissionaries<numCannibals && numMissionaries>0)
            return false;
        // at side 2
        if(((Main.nMissionaries-numMissionaries)<(Main.nCanniabals-numCannibals)) && (Main.nMissionaries-numMissionaries)>0)
            return false;
        //valid
        return true;

    }
}
