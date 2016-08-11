package page_rank;

import java.util.List;
import java.util.Set;

public class WikiTennisRanker {
	public static void main(String args[]){
		// Construct graoh
		String[] keywords = {"tennis","grand slam"};
        WikiCrawler wc = new WikiCrawler("/wiki/Tennis",keywords,1000,"WikiTennisGraph.txt");
        long start = System.currentTimeMillis();
        wc.crawl();
        long end = System.currentTimeMillis();
        long time = (end - start)/1000;
        System.out.println("Time used:"+time);
        
        // Compute page rank
		WikiTennisRanker.compute(0.01);
		WikiTennisRanker.compute(0.005);
	}
	
	
	public static void compute(double approcimate){
		PageRank pr=new PageRank("WikiTennisGraph.txt",0.01);
        String[] kpr=pr.topKPageRank(15);
        System.out.println("Highest page rank("+approcimate+"):"+kpr[0]);
        String[] ipr=pr.topKInDegree(15);
        System.out.println("Highest in degree("+approcimate+"):"+ipr[0]);
        String[] opr=pr.topKOutDegree(15);
        System.out.println("Highest out degree("+approcimate+"):"+opr[0]);
        
        System.out.println("Jaccard pagerank vs indegree("+approcimate+"):"+WikiTennisRanker.exactJaccard(kpr, ipr));
        System.out.println("Jaccard indegree vs outdegree("+approcimate+"):"+WikiTennisRanker.exactJaccard(ipr, opr));        
        System.out.println("Jaccard pagerank vs outdegree("+approcimate+"):"+WikiTennisRanker.exactJaccard(kpr, opr));
	}
	
	public static double exactJaccard(String[] list1,String[] list2){
		int intersection=0;
		for(String s1:list1){
			for(String s2:list2){
				if(s1.equals(s2)){
					intersection++;
					break;
				}
			}
		}
		return ((double)intersection/(list1.length+list2.length-intersection));
	}
}
