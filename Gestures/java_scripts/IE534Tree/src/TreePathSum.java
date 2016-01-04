import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Guo on 11/29/15.
 */
public class TreePathSum {
    public static void main(String[] args) {
        /*int[] p = {19, 47, 16, 43, 32, 50, 13, 28, 10};*/
        /*int[][] r = {{-50,28,-50,37,-50,35,-50,12,-50,26,-50,23,-50,15,-50}, {20,-50,50,-50,23,-50,16,-50,36,-50,27,-50,50,-50,50},
                {-50,32,-50,39,-50,45,-50,13,-50,42,-50,47,-50,17,-50}, {11,-50,46,-50,18,-50,39,-50,28,-50,50,-50,38,-50,45},
                {-50,19,-50,36,-50,33,-50,43,-50,26,-50,40,-50,33,-50}, {39,-50,42,-50,40,-50,29,-50,43,-50,14,-50,32,-50,25},
                {-50,35,-50,41,-50,19,-50,16,-50,27,-50,27,-50,37,-50}, {33,-50,20,-50,29,-50,37,-50,15,-50,18,-50,37,-50,27},
                {-50,26,-50,47,-50,11,-50,31,-50,35,-50,48,-50,25,-50}};*/

        /*int[][] r = {{-50,28,-50,37,-50,35,-50,12,-50}, {20,-50,50,-50,23,-50,16,-50,36},
                {-50,32,-50,39,-50,45,-50,13,-50}, {11,-50,46,-50,18,-50,39,-50,28},
                {-50,19,-50,36,-50,33,-50,43,-50}, {39,-50,42,-50,40,-50,29,-50,43},
                {-50,35,-50,41,-50,19,-50,16,-50}, {33,-50,20,-50,29,-50,37,-50,15},
                {-50,26,-50,47,-50,11,-50,31,-50}};*/

          int[] p = {20,50};
          int[][] r = {{20,60,120},{80,50,-50}};




        Node root = GraphConstruct(p,r);
        int val = TreePaths(root);
        System.out.println(val);

    }

    public static Node GraphConstruct(int[] p, int[][] r){
        int numTools = p.length;
        int numJobs = r[0].length;

        //build toolMap
        ArrayList<HashMap<Integer,Integer>> toolMap = new ArrayList<HashMap<Integer,Integer>>();
        for(int i=0;i<numJobs;i++){
            toolMap.add(new HashMap<Integer,Integer>());
        }
        for(int i=0;i<numJobs;i++){
            for(int j=0;j<numTools;j++){
                if(r[j][i]!=-50) toolMap.get(i).put(j,r[j][i]);
            }
        }
        Node root = new Node(0);
        root.tools = -1;
        root.job = -1;
        ArrayList<Node> Layer = new ArrayList<Node>();
        Layer.add(root);
        for(int i=0;i<numJobs;i++){
            ArrayList<Node> newLayer = AddNextLayer(Layer,i,toolMap, p);
            Layer = newLayer;
            System.out.println("Layer "+ i);
        }
        return root;
    }

    public static ArrayList<Node> AddNextLayer(ArrayList<Node> currentlayer,int Layer,ArrayList<HashMap<Integer,Integer>> toolMap, int[] p){
           ArrayList<Node> newLayer = new ArrayList<Node>();
           for(Node a : currentlayer){
               HashMap<Integer,Integer> map = toolMap.get(Layer);
               for(Map.Entry<Integer, Integer> entry : map.entrySet()){
                   Node newNode = new Node();
                   newNode.setParent(a);
                   newNode.setTools(entry.getKey());
                   newNode.setJob(Layer);
                   int val = calculateVal(newNode,toolMap,p);
                   newNode.setVal(val);
                   a.addChild(newNode);
                   newLayer.add(newNode);
               }
           }
        return newLayer;
    }

    public static int calculateVal(Node newNode,ArrayList<HashMap<Integer,Integer>> toolMap,int[] p){
        HashSet<Integer> toollist = new HashSet<Integer>();
        int val = toolMap.get(newNode.job).get(newNode.tools);
        Node current = newNode.parent;
        while(current!=null){
            if(!toollist.contains(current.tools)) toollist.add(current.tools);
            current = current.parent;
        }
        if(toollist.contains(newNode.tools)){
            return val;
        }else{
            return val-p[newNode.tools];
        }
    }


    public static int TreePaths(Node root){
        int answer = 0;
        if(root!= null) answer= searchTree(root,0,answer);
        return answer;
    }

    public static int searchTree(Node root,int sum, int answer){
        boolean istheend = true;
        for(Node a : root.getChild()){
            if(a !=null) istheend = false;
        }
        if(istheend){ answer = Math.max(answer,sum+root.val);
        }
        else{
            for(Node a: root.getChild()){
                answer = searchTree(a,sum+root.val,answer);
            }
        }
        return answer;
    }

    public static class Node{
        int val;
        int job;
        int tools;
        Node parent;
        ArrayList<Node> Child = new ArrayList<Node>();
        public Node(){}
        public Node(int val){
            this.val = val;
        }
        public void addChild(Node c){
            this.Child.add(c);
        }
        public void setParent(Node p){
            this.parent=p;
        }
        public int getVal(){
            return val;
        }
        public ArrayList<Node> getChild(){
            return Child;
        }
        public Node getParent(){
            return parent;
        }
        public void setJob(int job) {this.job = job;}
        public void setTools(int tools) {this.tools = tools;}
        public int getJob(){return job;}
        public int getTools(){return tools;}
        public void setVal(int val){this.val = val;}
    }
}


