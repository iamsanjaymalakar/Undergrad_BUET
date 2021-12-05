# CSE 462 - Algorithm Engineering Sessional

## Introduction
### Complete Graph
A complete graph is a simple undirected graph in which every pair of distinct vertices is connected by a unique edge.

### Clique
A clique of a graph _G(V,E)_ is a complete subgraph of _G_.

### Clique Cover Problem
**Input**: An undirected graph _G(V,E)_ and an integer _K_.

**Output**: True if the vertices of G can be partitioned into _K_ sets S<sub>i</sub> ,whenever
two vertices in the same sets S<sub>i</sub> are adjacent. S<sub>i</sub> do not need to be disjoint,
they can be non disjoint. But we can make them disjoint by putting common
vertices in only one set without any problem. Thus we can think S<sub>i</sub> are disjoint.

**Note**: There is also edge clique cover problem but we are only interested in
vertex clique cover. So if we say clique cover, we are indicating vertex clique
cover.

### Applications
- DNA molecular solution problem, data partitioning problem in embedded
processor-based systems (memory chips), image processing problems etc.
- Applications of the vertex clique cover problem arise in network security,
scheduling and VLSI design.
- Algorithms for clique cover can also be used to solve the closely related
problem of finding a maximum clique, which has a range of applications
in biology, such as identifying related protein sequences.

### Hardness Status of Clique Cover Problem
The clique cover problem in computational complexity theory is the algorithmic
problem of finding a minimum clique cover, or (rephrased as a decision prob-
lem) finding a clique cover whose number of cliques is below a given thresh-
old. Finding a minimum clique cover is **NP-hard**, and its decision version
is **NP-complete**. It was one of Richard Karp’s original 21 problems shown
NP-complete in his **1972** paper **“Reducibility Among Combinatorial
Problems”**.
The equivalence between clique covers and coloring is a reduction that can be
used to prove the **NP-completenes**s of the clique cover problem from the
known **NP-completeness** of graph coloring.
