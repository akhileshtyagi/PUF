import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class MaxFlowMinCut
{
    private int[] parent;
    private Queue<Integer> queue;
    private int numberOfVertices;
    private boolean[] visited;
    private Set<Pair> cutSet;
    private ArrayList<Integer> reachable;
    private ArrayList<Integer> unreachable;

    public MaxFlowMinCut (int numberOfVertices)
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

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of nodes");
        numberOfNodes = scanner.nextInt();
        graph = new int[numberOfNodes + 1][numberOfNodes + 1];

        System.out.println("Enter the graph matrix");
        for (int sourceVertex = 1; sourceVertex <= numberOfNodes; sourceVertex++)
        {
            for (int destinationVertex = 1; destinationVertex <= numberOfNodes ; destinationVertex++)
            {
                graph[sourceVertex][destinationVertex] = scanner.nextInt();
            }
        }
        System.out.println("Enter the source of the graph");
        source= scanner.nextInt();

        System.out.println("Enter the sink of the graph");
        sink = scanner.nextInt();

        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(numberOfNodes);
        maxFlow = maxFlowMinCut.maxFlowMinCut(graph, source, sink);

        System.out.println("The Max Flow is " + maxFlow);
        System.out.println("The Cut Set is ");
        maxFlowMinCut.printCutSet();
        scanner.close();
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