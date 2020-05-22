package com.coen317.node;

import java.util.ArrayList;

public class Node {
	
    // Node Globals
    private static final int MAX_STEPS = 32; // # requests a single lookup can generate
    private static final int M = 7;
    
    // Node ID
    private int nodeID; // stored as int for comparison
    // Node Finger Table
    private ArrayList<Node> fingerTable; 
    // Node Successor
    private Node successor; 
    // Node Predecessor
    private Node predecessor; 
    
    private boolean successorFound; // used for lookup
    

    public Node(String ip_address, String port, int bits) {        
    	
        this.nodeID = Key.generate(ip_address + ":" + port, bits); // Do SHA-1 of IP Address and Port
        this.fingerTable = new ArrayList<>(M); 
        this.successor = null;
        this.predecessor = null;
        this.successorFound = false;
    }
    
    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }
    public int getNodeID() {
        return nodeID;
    }
    public void setFingerTable(ArrayList<Node> fingerTable) {
        this.fingerTable = fingerTable;
    }
    public ArrayList<Node> getFingerTable(){
    	return this.fingerTable;
    }
    public void setSuccessor(Node successor){
    	this.successor = successor;
    }
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
            return this.getSuccessor();
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
            return this.getSuccessor();
        }
        // iterate through node's fingerTable
        for (int i = M-1; i >= 0; i--) {
            // if finger node is between current node and requested key, return finger node
            if (this.getFingerTable().get(i).getNodeID() == key){
                this.getFingerTable().get(i).setSuccessorFound(true);
                return this.getFingerTable().get(i);
            }
            else if (isBetween(this.getFingerTable().get(i).getNodeID(), this.getNodeID(), key)) {
                return this.getFingerTable().get(i);
            }   
        }
        // else, return node's successor
        return this.getSuccessor();
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



