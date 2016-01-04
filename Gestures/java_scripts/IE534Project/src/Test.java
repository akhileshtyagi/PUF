import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guo on 11/29/15.
 */
public class Test {
    public static void main(String[] args) {
        int[] p = {19, 47, 16, 43, 32, 50, 13, 28, 10};
        int[][] r = {{-50, 28, -50, 37}, {20, -50, 50, -50}, {-50, 32, -50, 39}, {11, -50, 46, -50},
                {-50, 19, -50, 36}, {39, -50, 42, -50}, {-50, 35, -50, 41}, {33, -50, 20, -50}, {-50, 26, -50, 47}};
        int numTools = p.length;
        int numJobs = r[0].length;

        Node root = new Node(0);
        root.addChild(new Node(3));
        root.addChild(new Node(5));
        root.addChild(new Node(8));
        for(Node a : root.getChild()){
            a.setParent(root);
        }
        Node current = root.getChild().get(1);
        current.addChild(new Node(4));

        List<Integer> list = TreePaths(root);
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
    }

    public static List<Integer> TreePaths(Node root){
        List<Integer> answer = new ArrayList<Integer>();
        if(root!= null) searchTree(root,0,answer);
        return answer;
    }
    public static void searchTree(Node root,int sum, List<Integer> answer){
        boolean istheend = true;
        for(Node a : root.getChild()){
            if(a !=null) istheend = false;
        }
        if(istheend) answer.add(sum+root.val);
        else{
            for(Node a: root.getChild()){
                searchTree(a,sum+root.val,answer);
            }
        }
    }

    public static class Node{
        int val;
        ArrayList<Node> Child = new ArrayList<Node>();
        Node parent;
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
    }
}


