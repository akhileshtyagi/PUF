import java.util.*;
/**
 * Created by Guo on 11/30/15.
 */
public class MaxFlow {
    private int[] parent;
    private Queue<Integer> queue;
    private int numberOfVertices;
    private boolean[] visited;
    private Set<Pair> cutSet;
    private ArrayList<Integer> reachable;
    private ArrayList<Integer> unreachable;

    public MaxFlow (int numberOfVertices)
    {
        this.numberOfVertices = numberOfVertices;
        this.queue = new LinkedList<Integer>();
        parent = new int[numberOfVertices + 1];
        visited = new boolean[numberOfVertices + 1];
        cutSet = new HashSet<Pair>();
        reachable = new ArrayList<Integer>();
        unreachable = new ArrayList<Integer>();
    }

    public boolean bfs (int source, int goal, int graph[][])
    {
        boolean pathFound = false;
        int destination, element;
        for (int vertex = 1; vertex <= numberOfVertices; vertex++)
        {
            parent[vertex] = -1;
            visited[vertex] = false;
        }
        queue.add(source);
        parent[source] = -1;
        visited[source] = true;

        while (!queue.isEmpty())
        {
            element = queue.remove();
            destination = 1;
            while (destination <= numberOfVertices)
            {
                if (graph[element][destination] > 0 &&  !visited[destination])
                {
                    parent[destination] = element;
                    queue.add(destination);
                    visited[destination] = true;
                }
                destination++;
            }
        }

        if (visited[goal])
        {
            pathFound = true;
        }
        return pathFound;
    }

    public int  maxFlowMinCut (int graph[][], int source, int destination)
    {
        int u, v;
        int maxFlow = 0;
        int pathFlow;
        int[][] residualGraph = new int[numberOfVertices + 1][numberOfVertices + 1];

        for (int sourceVertex = 1; sourceVertex <= numberOfVertices; sourceVertex++)
        {
            for (int destinationVertex = 1; destinationVertex <= numberOfVertices; destinationVertex++)
            {
                residualGraph[sourceVertex][destinationVertex] = graph[sourceVertex][destinationVertex];
            }
        }

        /*max flow*/
        while (bfs(source, destination, residualGraph))
        {
            pathFlow = Integer.MAX_VALUE;
            for (v = destination; v != source; v = parent[v])
            {
                u = parent[v];
                pathFlow = Math.min(pathFlow,residualGraph[u][v]);
            }
            for (v = destination; v != source; v = parent[v])
            {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }
            maxFlow += pathFlow;
        }

        /*calculate the cut set*/
        for (int vertex = 1; vertex <= numberOfVertices; vertex++)
        {
            if (bfs(source, vertex, residualGraph))
            {
                reachable.add(vertex);
            }
            else
            {
                unreachable.add(vertex);
            }
        }
        for (int i = 0; i < reachable.size(); i++)
        {
            for (int j = 0; j < unreachable.size(); j++)
            {
                if (graph[reachable.get(i)][unreachable.get(j)] > 0)
                {
                    cutSet.add(new Pair(reachable.get(i), unreachable.get(j)));
                }
            }
        }
        return maxFlow;
    }

    public void printCutSet ()
    {
        Iterator<Pair> iterator = cutSet.iterator();
        while (iterator.hasNext())
        {
            Pair pair = iterator.next();
            System.out.println(pair.source + "-" + pair.destination);
        }
    }

    public static void main (String...arg)
    {
        int[][] graph;
        int numberOfNodes;
        int source;
        int sink;
        int maxFlow;

        int[] p = {2,6,5,8};
        int[][] r = {{4,0,0,0},{4,6,0,0},{0,0,2,3},{0,0,0,3}};

        numberOfNodes = p.length+r[0].length+2;
        sink = 2;
        source = 1;
        graph = new int[numberOfNodes + 1][numberOfNodes + 1];

        int cap = 0;
        for(int i=2+p.length+1;i<numberOfNodes+1;i++){
            int cost = 0;
            for(int j=0;j<p.length;j++){
                cost = Math.max(cost,r[j][i-3-p.length]);
            }
            graph[1][i] = cost;
            cap = cap+cost;
        }

        for(int i=3;i<3+p.length;i++){
            graph[i][2] = p[i-3];
        }

        for(int i=0;i<r.length;i++){
            for(int j=0;j<r[0].length;j++){
                if(r[i][j]!=0) graph[j+3+p.length][i+3] = Integer.MAX_VALUE;
                else graph[j+3+p.length][i+3]=0;
            }
        }
        display(graph);
        MaxFlow maxFlowMinCut = new MaxFlow(numberOfNodes);
        maxFlow = maxFlowMinCut.maxFlowMinCut(graph, source, sink);


        System.out.println(" ");
        System.out.println(maxFlow);
        System.out.println(cap-maxFlow);


    }


    public static void display(int[][] table){
        for(int i=0;i<table.length;i++){
            System.out.println(" ");
            for(int j=0;j<table[0].length;j++){
                System.out.print(" " + table[i][j]);
            }
        }
    }
}



class Pair
{
    public int source;
    public int destination;

    public Pair (int source, int destination)
    {
        this.source = source;
        this.destination = destination;
    }

    public Pair()
    {
    }

}
