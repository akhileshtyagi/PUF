package page_rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRank {
	Pages pages;
	Map<String, Integer> pageIndices=new HashMap<String,Integer>();
	Map<String, Integer> indegree=new HashMap<String,Integer>();
	private static final double BETA=0.85;
	double pagerank[];
	int[][] matrix;

	/**
	 * made this constructor to avoid need to read from a file
     */
	public PageRank(Pages pages, double approx){
		// set pages
		this.pages = pages;

		// compute page rank
		compute_page_rank(approx);
	}

	public PageRank(String path, double approx) {
		// parse graph
		ParseFile pf = new ParseFile();
		pages = pf.parse(path);

		// compute the page_rank
		compute_page_rank(approx);
	}

	private void compute_page_rank(double approx){
		// set an index to each node in the graph
		int index=0;
		for(String page:pages.getPages()){
			pageIndices.put(page,index++);
		}
		int pageNum=pageIndices.size();
		
		// adjacency matrix
		matrix=new int[pageNum][pageNum];
		
		// page rank vector (P(n))
		double[] vector=new double[pageNum];
		// initialize each element in page rank vector to 1/pageNum 
		for(int i=0;i<pageNum;i++){
			vector[i]=(double)1/pageNum;
		}
		
		int round=1; // steps used to converge
		while(true){
			double sum=0;
			// page rank vector (P(n+1))
			double[] newVector=new double[pageNum];
			for(int i=0;i<pageNum;i++){
				newVector[i]=(double)(1-BETA)/pageNum;
			}
			
			
			for(String name:pages.getPages()){  // for each node in the graph
				Map<String, List<String>> edges=pages.getEdges();
				int iPage=pageIndices.get(name);	// get its index in page rank vector 
				if(edges.get(name).size()==0){	// if the node has no out-degree
					for(String link:pages.getPages()){	// add beta*(its pagerank)/pageNum to all other pages
						newVector[pageIndices.get(link)]+=BETA*vector[iPage]/pageNum;
					}
				}else{	//otherwise
					for(String link:edges.get(name)){	// for each link in this node
						this.matrix[pageIndices.get(link)][pageIndices.get(name)]=1;  // record the edge in adjacent matrix
						newVector[pageIndices.get(link)]+=BETA*vector[iPage]/edges.get(name).size(); // add beta*(its pagerank)/(link number) to its links
					}
				}
			}
			// calculate Norm(P(n)-P(n+1))
			for(int i=0;i<pageNum;i++){
				sum+=Math.abs(newVector[i]-vector[i]);
			}
			vector=newVector;
			// if Norm is less than epsilon, break
			if(sum<approx){
				break;
			}
			round++;
		}
		System.out.println(round+" steps to calculate the page rank.");
		this.pagerank=vector;
		
		// calculate indegree
		Map<String, List<String>> edges=pages.getEdges();
		for(String name:edges.keySet()){
			for(String link:edges.get(name)){
				if(indegree.containsKey(link)){
					indegree.put(link, indegree.get(link)+1);
				}else{
					indegree.put(link, 1);
				}
			}
		}
	}
	
	public double pageRankOf(String vertex){
		return this.pagerank[pageIndices.get(vertex)];
	}
	
	public int outDegreeOf(String vertex){
		return this.pages.getLinks(vertex).size();
	}
	
	public int inDegreeOf(String vertex){
		int index=this.pageIndices.get(vertex);
		int count=0;
		for(int i=0;i<this.pageIndices.size();i++){
			if(this.matrix[index][i]==1){
				count+=1;
			}
		}
		return count;
	}
	
	public int numEdges(){
		return this.pageIndices.size();
	}
	
	public String[] topKPageRank(int k){
		List<String> result=new ArrayList<String>();
		//sort
		for(String name:this.pageIndices.keySet()){
			int index1=this.pageIndices.get(name);
			for(int j=0;j<result.size()+1;j++){
				if(j==result.size()){
					result.add(name);
					break;
				}
				int index2=this.pageIndices.get(result.get(j));
				if(this.pagerank[index2]<this.pagerank[index1]){
					result.add(j, name);
					break;
				}
			}
		}
		return result.subList(0, k).toArray(new String[k]);
	}
	
	public String[] topKInDegree(int k){
		List<String> result=new ArrayList<String>();
		//sort
		for(String name:this.indegree.keySet()){
			for(int i=0;i<result.size()+1;i++){
				if(i==result.size()){
					result.add(name);
					break;
				}else{
					if(this.indegree.get(result.get(i))<this.indegree.get(name)){
						result.add(i, name);
						break;
					}
				}
			}
		}
		return result.subList(0, k).toArray(new String[k]);
	}
	
	public String[] topKOutDegree(int k){
		List<String> result=new ArrayList<String>();
		//sort
		for(String name:this.pageIndices.keySet()){
			for(int i=0;i<result.size()+1;i++){
				if(i==result.size()){
					result.add(name);
					break;
				}else{
					if(this.pages.getLinks(result.get(i)).size()<this.pages.getLinks(name).size()){
						result.add(i, name);
						break;
					}
				}
			}
		}
		return result.subList(0, k).toArray(new String[k]);
	}

	/**
	 * returns the page_rank probability of a given vertex
	 */
	//TODO test
	public double get_probability(String vertex){
		return pagerank[pageIndices.get(vertex)];
	}
}
