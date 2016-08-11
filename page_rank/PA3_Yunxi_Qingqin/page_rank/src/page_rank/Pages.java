package page_rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class is used to save all nodes and links in each node
 * @author Qingqin Hou
 *
 */
public class Pages {
	// the key is node, value is all the links in the node
	private Map<String, List<String>> edges=new HashMap<String,List<String>>();
	// node list
	private List<String> pages=new ArrayList<String>();
	
	public void put(String page, String link){
		if(edges.containsKey(page)){
			edges.get(page).add(link);
		}else{
			List<String> links=new ArrayList<String>();
			links.add(link);
			edges.put(page, links);
		}
		if(!edges.containsKey(link)){
			List<String> links=new ArrayList<String>();
			edges.put(link, links);
		}
	}
	
	public List<String> getLinks(String name){
		return this.edges.get(name);
	}
	

	public Map<String, List<String>> getEdges(){
		return this.edges;
	}
	
	public List<String> getPages(){
		if(edges.size()==pages.size()){
			return pages;
		}else{
			pages.addAll(edges.keySet());
			return pages;
		}
	}
}
