package com.coen317.node;


public class DHT {
    Node introducer; 
    
    // constructor takes in introdocer node to create the Chord ring
    public DHT(Node introducer){
        this.introducer = introducer;
    }
    
    public Node getIntroducer(){
        return introducer;
    }
    
    public void setIntroducer(Node introducer){
        this.introducer = introducer;
    }
    
    public void join(Node joiningNode){ 
        
        // handle case where ring only has introducer node: will become ring of 2 nodes
        if (introducer.getPredecessor() == null && introducer.getSuccessor() == null) {
            introducer.setPredecessor(joiningNode);
            introducer.setSuccessor(joiningNode);
            joiningNode.setPredecessor(introducer);
            joiningNode.setSuccessor(introducer);
        }
        else {
            // set joiningNode's successor
            joiningNode.setSuccessor(introducer.find(joiningNode.getNodeID())); 
            // set joiningNode's predecessor
            joiningNode.setPredecessor(joiningNode.getSuccessor().getPredecessor()); 
            // update joiningNode's predecessor's successor
            joiningNode.getPredecessor().setSuccessor(joiningNode); 
            // update joiningNode's successor's predecessor
            joiningNode.getSuccessor().setPredecessor(joiningNode);
        }
    }
}
