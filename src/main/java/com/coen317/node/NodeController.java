package com.coen317.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class NodeController {
	private static final String template = "Stored Content: %d %s";
	
	Node thisnode;
	DHT ring;
	
	@GetMapping("/initialize")
	public String initialize() {
		
		int bits = 7;
		String portnum = "8080";
		String addr = null;
		
		try {
			addr = getipAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		thisnode = new Node(addr, portnum, bits);
		ring = new DHT(thisnode);
		
		return String.format("Node Initialized at %s:%s Node ID = %d", addr, portnum, thisnode.getNodeID());
	}
	
	@GetMapping("/getthisnodeinfo")
	public Node getthisnodeinfo() {
		return thisnode;
	}
	
	@GetMapping("/setnodeid")
	public String setnodeid(@RequestParam String nodeid) {
		thisnode.setNodeID(Integer.parseInt(nodeid));
		return "Node ID set to: " + nodeid;
	}
	
	// this spring mapping is used to set the Node and IP Address of the server due to VM's
	@GetMapping("/setnodeinfo")
	public Node setnodeinfo(@RequestParam List<String> nodeparam) {
		thisnode.setNodeID(Integer.parseInt(nodeparam.get(0)));
		thisnode.setIpAddress(nodeparam.get(1));
		
		return thisnode;
	}
	
	//if new ring, create DHT
	
	//if you want join a ring, query for ring object by passing the leader ip address
	
	@GetMapping("/addtofingertable")
	public Node addtofingertable(@RequestParam List<String> nodeparam) throws JsonMappingException, JsonProcessingException {
		
		String mynodestr = String.format("%s:%s", thisnode.getIpAddress(), thisnode.getPort());
		String nodestr = String.format("%s:%s", nodeparam.get(0),nodeparam.get(1));
		if(mynodestr.matches(nodestr)) {
			Node tempnode = new Node(thisnode);
			thisnode.addToFingerTable(tempnode);
			return tempnode;
		}
		else {
			String mytemplate = "Stored to Finger Table: %s:%s";
			//thisnode.addToFingerTable(nodeparams.get(0), nodeparams.get(1),Integer.parseInt(nodeparams.get(2)));
			ArrayList<Node> templist = thisnode.getFingerTable();
			
			String nodecmd = String.format("http://%s:%s/getthisnodeinfo", nodeparam.get(0),nodeparam.get(1));
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(nodecmd, String.class);
			
			Node newnode = new ObjectMapper().readValue(result, Node.class);
			thisnode.addToFingerTable(newnode);
			
			return newnode;
		}
	}
	
	@GetMapping("/addtofingertable2")
	public Node addtofingertable2(@RequestParam List<String> nodeparam) {
		thisnode.addToFingerTable(nodeparam.get(0), nodeparam.get(1),Integer.parseInt(nodeparam.get(2)));
		return thisnode.getFingerTable().get(this.getfingertable().size());
	}
	
	@GetMapping("/getfingertable")
	public ArrayList getfingertable() {
		return thisnode.getFingerTable();
	}
	
	@GetMapping("/setSuccessor")
	public void setSuccessor(@RequestParam List<String> successornodeparam) {
		Node successor = new Node(successornodeparam.get(0), successornodeparam.get(1), Integer.parseInt(successornodeparam.get(2)));
		thisnode.setSuccessor(successor);
	}
	
	@GetMapping("/getSuccessor")
	public Node getSuccessor() {
		return thisnode.getSuccessor();
	}
	
	@GetMapping("/setPredecessor")
	public void setPredecessor(@RequestParam List<String> predecessornodeparam) {
		Node predecessor = new Node(predecessornodeparam.get(0), predecessornodeparam.get(1), Integer.parseInt(predecessornodeparam.get(2)));
		thisnode.setPredecessor(predecessor);
	}
	
	@GetMapping("/getPredecessor")
	public Node getPredecessor() {
		return thisnode.getPredecessor();
	}
	/*
	@GetMapping("/find")
	public Node find(@RequestParam String findstring) {
		int bits = 7;
		int key = Key.generate(findstring, bits);
		System.out.println(String.format("find key generated %s = %d", findstring, key));
		Node foundnode = thisnode.find(key);
		
		return foundnode;
	}
	*/
	public String getipAddress() throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		String addr = localhost.getHostAddress();
		return addr;
		
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@GetMapping("/setSuccessorNodeSuccessor")
	public Node setSuccessorNodeSuccessor(@RequestParam List <String> nodeparam) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/setSuccessor?successornodeparam=%s,%s,%s", thisnode.getSuccessor().getIpAddress(), thisnode.getSuccessor().getPort(),nodeparam.get(0),nodeparam.get(1), nodeparam.get(2));
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
		
	}
	
	@GetMapping("/setPredecessorNodePredecessor")
	public Node setPredecessorNodePredecessor(@RequestParam List <String> nodeparam) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/setPredecessor?predecessornodeparam=%s,%s,%s", thisnode.getPredecessor().getIpAddress(), thisnode.getPredecessor().getPort(),nodeparam.get(0),nodeparam.get(1), nodeparam.get(2));
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("setSuccessorNodePredecessor")
	public Node setSuccessorNodePredecessor(@RequestParam List<String> nodeparam) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/setPredecessor?predecessornodeparam=%s,%s,%s", thisnode.getSuccessor().getIpAddress(), thisnode.getSuccessor().getPort(),nodeparam.get(0),nodeparam.get(1), nodeparam.get(2));
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("setPredecessorNodeSuccessor")
	public Node setPredecessorNodeSuccessor(@RequestParam List<String> nodeparam) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/setSuccessor?successornodeparam=%s,%s,%s", thisnode.getPredecessor().getIpAddress(), thisnode.getPredecessor().getPort(),nodeparam.get(0),nodeparam.get(1), nodeparam.get(2));
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("/getSuccessorNode")
	public Node getSuccessorNode() throws JsonMappingException, JsonProcessingException {
		//"18.237.116.195", "8080"
		String nodeaddr = String.format("http://%s:%s", thisnode.getSuccessor().getIpAddress(), thisnode.getSuccessor().getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodeaddr+"/getSuccessor", String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("/getPredecessorNode")
	public Node getPredecessorNode() throws JsonMappingException, JsonProcessingException {
		//"18.237.116.195", "8080"
		String nodeaddr = String.format("http://%s:%s", thisnode.getPredecessor().getIpAddress(), thisnode.getPredecessor().getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodeaddr+"/getPredecessor", String.class);
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("/getSuccessorNodePredecessor")
	public Node getSuccessorNodePredecessor(@RequestParam List<String> nodeparam) throws JsonMappingException, JsonProcessingException {
		
		String nodecmd = String.format("http://%s:%s/getPredecessor", thisnode.getSuccessor().getIpAddress(), thisnode.getSuccessor().getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	@GetMapping("/getfingertablei")
	public Node getfingertablei(@RequestParam String ival) {
		int i = Integer.parseInt(ival);
		return thisnode.getFingerTable().get(i);
	}
	
	@GetMapping("/searchring")
	public Node searchRing(@RequestParam String keyval) throws JsonMappingException, JsonProcessingException {
		
		int key = Integer.parseInt(keyval);
		//int bits = 7;
		//int key = Key.generate(str, bits);
		Node foundnode = find(thisnode, key);
		
		return foundnode;
	}
	
	@GetMapping("/addtoring")
	public String addToRing(@RequestParam List <String> nodeparam) throws JsonMappingException, JsonProcessingException {
		Node joiningNode = new Node(nodeparam.get(0), nodeparam.get(1), Integer.parseInt(nodeparam.get(2)));
		join(joiningNode);

		// set successor and thisnode
		//thisnode.setSuccessor(ring.getthisnode().getSuccessor());
		
		return String.format("%s with %s:%s has joined the ring", joiningNode.getNodeID(), joiningNode.getIpAddress(), joiningNode.getPort());
	}
	
	public void join(Node joiningNode) throws JsonMappingException, JsonProcessingException{
		
        // handle case where ring only has thisnode node: will become ring of 2 nodes
        if (thisnode.getPredecessor() == null && thisnode.getSuccessor() == null) {
            thisnode.setPredecessor(joiningNode);
            thisnode.setSuccessor(joiningNode);
            //joiningNode.setPredecessor(thisnode);
            setNodeOnePredecessorToNodeTwo(joiningNode, thisnode);
            //joiningNode.setSuccessor(thisnode);
            setNodeOneSuccessorToNodeTwo(joiningNode, thisnode);
            
        }
        else {
            // set joiningNode's successor | to thisnodes find of joiningNode's ID
            //joiningNode.setSuccessor(thisnode.find(joiningNode.getNodeID()));
        	setNodeOneSuccessorToNodeTwo(joiningNode, find(thisnode, joiningNode.getNodeID()));
            
            // set joiningNode's predecessor | to joiningNode's Successor's Predecessor
            //joiningNode.setPredecessor(joiningNode.getSuccessor().getPredecessor());
            setNodeOnePredecessorToNodeTwo(joiningNode, getSuccessorPredecessorNode(joiningNode));
            
            
            // update joiningNode's predecessor's successor | to joiningNode
            //joiningNode.getPredecessor().setSuccessor(joiningNode);
            setPredecessorSuccessor(joiningNode);
            
            // update joiningNode's successor's predecessor | to joiningNode
            //joiningNode.getSuccessor().setPredecessor(joiningNode);
            setSuccessorPredecessor(joiningNode);
            
            
        }
    }
	
	public void setNodeOnePredecessorToNodeTwo(Node one, Node two) {
		String nodecmd = String.format("http://%s:%s/setPredecessor?predecessornodeparam=%s,%s,%s", one.getIpAddress(), one.getPort(), two.getIpAddress(), two.getPort(), "7");
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
	}
	
	public void setNodeOneSuccessorToNodeTwo(Node one, Node two) {
		String nodecmd = String.format("http://%s:%s/setSuccessor?successornodeparam=%s,%s,%s", one.getIpAddress(), one.getPort(), two.getIpAddress(), two.getPort(), "7");
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
	}
	
	public void setSuccessorPredecessor(Node one) {
		String nodecmd = String.format("http://%s:%s/setSuccessorNodePredecessor?nodeparam=%s,%s,%s", one.getIpAddress(), one.getPort(), one.getIpAddress(), one.getPort(), "7");
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
	}
	
	public void setPredecessorSuccessor(Node one) {
		String nodecmd = String.format("http://%s:%s/setPredecessorNodeSuccessor?nodeparam=%s,%s,%s", one.getIpAddress(), one.getPort(), one.getIpAddress(), one.getPort(), "7");
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		
	}
	
	public Node getSuccessorPredecessorNode(Node one) throws JsonMappingException, JsonProcessingException {
		
		String nodecmd = String.format("http://%s:%s/getSuccessorNodePredecessor", one.getIpAddress(), one.getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	public Node getSuccessor(Node one) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/getSuccessor", one.getIpAddress(), one.getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		
		return newnode;
	}
	
	public ArrayList<Node> getFingerTable(Node one) {
		
		String nodecmd = String.format("http://%s:%s/getfingertable", one.getIpAddress(), one.getPort());
		
		RestTemplate restTemplate = new RestTemplate();
		ArrayList<Node> result = restTemplate.getForObject(nodecmd, ArrayList.class);
		System.out.println("getFingerTable");
		return result;
	}
	
	public Node getFingerTable(Node one, int i) throws JsonMappingException, JsonProcessingException {
		String nodecmd = String.format("http://%s:%s/getfingertablei?ival=%s", one.getIpAddress(), one.getPort(), Integer.toString(i));
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(nodecmd, String.class);
		Node newnode = new ObjectMapper().readValue(result, Node.class);
		System.out.println("getFingerTable");
		return newnode;
	}
	/*
	// find, findSuccessor, findClosestPrecedingNode, and isBetween have all been converted to RPC versions shown in Node.java
	public Node find(Node nextNode, int key) throws JsonMappingException, JsonProcessingException {
		int MAX_STEPS = 32;
        // check if key is stored locally
        if (key == nextNode.getNodeID()){
            nextNode.setSuccessorFound(true);
            System.out.println("Set Successor Found = True");
        }
    	int i = 0;
    	while ((!nextNode.getSuccessorFound()) && (i < MAX_STEPS)) {
            //nextNode = findSuccessor(nextNode, key);
    		nextNode = findSuccessor(key);
            i++;
    	}
        
    	if (nextNode.getSuccessorFound()) {
            nextNode.setSuccessorFound(false); // return value to false for future lookups
            return nextNode;
    	}
    	else {
            System.out.println("Error with successor lookup.");
            return null;
    	}
	}*/
	
	public Node find(Node tempnode, int key){
    	Node nextNode = tempnode;
    	int MAX_STEPS = 32;
        // check if key is stored locally
        if (key == nextNode.getNodeID()){
            nextNode.setSuccessorFound(true);
            System.out.println("Set Successor Found = True");
        }
    	int i = 0;
    	while ((!nextNode.getSuccessorFound()) && (i < MAX_STEPS)) {
            nextNode = nextNode.findSuccessor(key);
            i++;
    	}
        
    	if (nextNode.getSuccessorFound()) {
            nextNode.setSuccessorFound(false); // return value to false for future lookups
            return nextNode;
    	}
    	else {
            System.out.println("Error with successor lookup.");
            return null;
    	}
    }
	
    public Node findSuccessor(int key) throws JsonMappingException, JsonProcessingException {
        // if key is between the current node and the node's immediate successor, return immediate successor
       if (isBetween(key, thisnode.getNodeID(), thisnode.getSuccessor().getNodeID())) {
    	   thisnode.getSuccessor().setSuccessorFound(true);
            return thisnode.getSuccessor(); //RPC
        }
        // else, continue search in node's fingerTable
        else {
            return findClosestPrecedingNode(key);
        }
    }

    // returns highest predecessor from node's fingerTable (wrt requested key)
    public Node findClosestPrecedingNode(int key) {
    	int M = 7;
        // if fingerTable's not yet initialized (ie with joining), skip below for loop
        if (thisnode.getFingerTable().isEmpty() || thisnode.getFingerTable() == null){
            return thisnode.getSuccessor(); //RPC      
        }
        // iterate through node's fingerTable
        for (int i = M-1; i >= 0; i--) {
            // if finger node is between current node and requested key, return finger node
            if (thisnode.getFingerTable().get(i).getNodeID() == key){
            	thisnode.getFingerTable().get(i).setSuccessorFound(true);
                return thisnode.getFingerTable().get(i); //RPC
            }
            else if (isBetween(thisnode.getFingerTable().get(i).getNodeID(), thisnode.getNodeID(), key)) {
                return thisnode.getFingerTable().get(i); //RPC
            }   
        }
        // else, return node's successor
        return thisnode.getSuccessor(); //RPC
        //RPC to 
    }
	
	/*
	// returns node's successor or calls for search to continue on node's fingerTable
    public Node findSuccessor(Node nextNode, int key) throws JsonMappingException, JsonProcessingException {
        // if key is between the current node and the node's immediate successor, return immediate successor
       if (isBetween(key, nextNode.getNodeID(), getSuccessor(nextNode).getNodeID())) {
    	   getSuccessor(nextNode).setSuccessorFound(true);
            return getSuccessor(nextNode); //RPC
        }
        // else, continue search in node's fingerTable
        else {
            return findClosestPrecedingNode(nextNode, key);
        }
    }
    
    public Node findClosestPrecedingNode(Node nextNode, int key) throws JsonMappingException, JsonProcessingException {
    	
    	int M = 7;
    	
        // if fingerTable's not yet initialized (ie with joining), skip below for loop
        if (getFingerTable(nextNode).isEmpty() || getFingerTable(nextNode) == null){
            return getSuccessor(nextNode); //RPC
        }
        // iterate through node's fingerTable
        for (int i = M-1; i >= 0; i--) {
            // if finger node is between current node and requested key, return finger node
        	  
            if (getFingerTable(nextNode, i).getNodeID() == key){
            	getFingerTable(nextNode, i).setSuccessorFound(true);
                return getFingerTable(nextNode, i); //RPC
            }
            else if (isBetween(getFingerTable(nextNode, i).getNodeID(), nextNode.getNodeID(), key)) {
                return getFingerTable(nextNode, i); //RPC
            }   
        }
        // else, return node's successor
        return getSuccessor(nextNode); //RPC
    }
    */
    // returns true if the key is between (start, end] range in the ring
    public boolean isBetween(int target, int start, int end) {
        if (start > end) {
            return target > start || target <= end;
        } else if (start < end) {
            return target > start && target <= end;
        } else {
            return true;
        }
    }
	
}
