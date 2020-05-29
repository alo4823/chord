package com.coen317.node;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {
	
    // Node Globals
    private static final int MAX_STEPS = 32; // # requests a single lookup can generate
    private static final int NUM_SUCCESSORS = 1;
    private static final int M = 7; //size of fingertable

    // Node ID
    private int nodeID; // stored as int for comparison
    // Node Finger Table
    private ArrayList<Node> fingerTable;
    // Node Successors
    private ArrayList<Node> successors; // stores 3 next successors
    // Node Predecessor
    private Node predecessor;
    private String ip_address;
    private String port;

    private boolean successorFound; // used for lookup
	private Node successor;
    
	//dummy constructor
	public Node() {
		
	}
    public Node(String ip_address, String port, int bits) {        
    	
        this.nodeID = Key.generate(ip_address + ":" + port, bits); // Do SHA-1 of IP Address and Port
        this.ip_address = ip_address;
        this.port = port;
        this.fingerTable = new ArrayList<>(M);
        this.successors = new ArrayList<>(NUM_SUCCESSORS);
        /*
         *  TO DO: Node's fingerTable, predecessor, and successors and will be set by Joining
         *  function and then updated by Stabilization Protocol w/ every subsequent node join.
         */
        //this.fingerTable = new ArrayList<>(M); 
        this.successor = null;
        this.predecessor = null;
        this.successorFound = false;
    }
    
    public Node(Node oldnode) {
    	this.nodeID = oldnode.nodeID;
    	this.ip_address = oldnode.ip_address;
    	this.port = oldnode.port;
    	this.fingerTable = new ArrayList(oldnode.getFingerTable());
    	//this.successors = new ArrayList(oldnode.getSuccessors());
    	this.successor = oldnode.getSuccessor();
    	this.predecessor = oldnode.getPredecessor();
    	this.successorFound = oldnode.getSuccessorFound();
    }
    
    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }
    public int getNodeID() {
        return nodeID;
    }
    public void setIpAddress(String address) {
    	this.ip_address = address;
    }
    public String getIpAddress() {
    	return this.ip_address;
    }
    public void setPort(String port) {
    	this.port = port;
    }
    public String getPort() {
    	return this.port;
    }
    public void setFingerTable(ArrayList<Node> fingerTable) {
        this.fingerTable = fingerTable;
    }
    public ArrayList<Node> getFingerTable(){
    	return this.fingerTable;
    	//return this.successors;
    }
    public void setSuccessor(Node successor){
    	this.successor = successor;
    }
    /*
    public ArrayList<Node> getSuccessors(){
    	//return this.fingerTable;
    	return this.successors;
    }*/
    public Node getSuccessor(){
    	return this.successor;
    }
    public void setPredecessor(Node predecessor){
    	this.predecessor = predecessor;
    }
    public Node getPredecessor(){
    	return this.predecessor;
    }
    public void setSuccessorFound(Boolean status){
    	this.successorFound = status;
    }
    public boolean getSuccessorFound(){
    	return this.successorFound;
    }
    
    // Add new Node to Finger Table
    public void addToFingerTable(String ip_address, String port, int bits) {
    	Node newnode = new Node(ip_address, port, bits);
    	
    	if(this.fingerTable.size() < M) {
    		this.getFingerTable().add(newnode);
    	}
    }
    
    public void addToFingerTable(Node newnode) {
    	if(this.fingerTable.size() < M) {
    		this.getFingerTable().add(newnode);
    	}
    }
    
    public void printFingerTable() {
    	
    	System.out.println(String.format("\n\nFinger Table for N%d", this.getNodeID()));
    	for (int n =0; n< this.fingerTable.size();n++) {
    		System.out.println(this.fingerTable.get(n).getNodeID());
    	}
    }
    
    public void populateFingerTable() {
	   //If node is the only node in the ring
	   
	   //If the node has 1 other node in the ring
	   
	   //If the node has n other nodes in the ring
   }
    /*
     Adding node lookup function:
     Following 4 functions are involved in finding the the node that holds the desired key.
    */
	
    // returns node that holds the requested key
    public Node find(int key){
    	Node nextNode = this;
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
    
    // returns node's successor or calls for search to continue on node's fingerTable
    public Node findSuccessor(int key) {
        // if key is between the current node and the node's immediate successor, return immediate successor
       if (isBetween(key, this.getNodeID(), this.getSuccessor().getNodeID())) {
            this.getSuccessor().setSuccessorFound(true);
            return this.getSuccessor(); //RPC
        }
        // else, continue search in node's fingerTable
        else {
            return findClosestPrecedingNode(key);
        }
    }

    // returns highest predecessor from node's fingerTable (wrt requested key)
    public Node findClosestPrecedingNode(int key) {
        // if fingerTable's not yet initialized (ie with joining), skip below for loop
        if (this.getFingerTable().isEmpty() || this.getFingerTable() == null){
            return this.getSuccessor(); //RPC
        }
        // iterate through node's fingerTable
        for (int i = M-1; i >= 0; i--) {
            // if finger node is between current node and requested key, return finger node
            if (this.getFingerTable().get(i).getNodeID() == key){
                this.getFingerTable().get(i).setSuccessorFound(true);
                return this.getFingerTable().get(i); //RPC
            }
            else if (isBetween(this.getFingerTable().get(i).getNodeID(), this.getNodeID(), key)) {
                return this.getFingerTable().get(i); //RPC
            }   
        }
        // else, return node's successor
        return this.getSuccessor(); //RPC
    }

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



