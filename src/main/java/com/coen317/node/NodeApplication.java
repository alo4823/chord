package com.coen317.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeApplication {

	public static void main(String[] args) {
		//SpringApplication.run(NodeApplication.class, args);
		
		chordTest();
	}
	
	public static void chordTest() {
		 // create introducer node
        Node N32 = new Node("54.187.21.131", "8080", 7);
        N32.setNodeID(32);   // hardcoding nodeID
        
        // create DHT ring by passing in introducer node
        DHT ring = new DHT(N32);
        System.out.println("Introducer nodeID: " + ring.getIntroducer().getNodeID());
        
        // create a 2nd new node 
        Node N45 = new Node("30.219.57.143", "8080", 7);
        N45.setNodeID(45); 	// hardcoding nodeID
        ring.join(N45); 
        System.out.println("** N45 added to the ring");
        System.out.println("Introducer successor: "   + ring.getIntroducer().getSuccessor().getNodeID());
        System.out.println("Introducer predecessor: " + ring.getIntroducer().getPredecessor().getNodeID());
        
        // create a 3rd new node 
        Node N40 = new Node("20.219.80.143", "8080", 7);
        N40.setNodeID(40);	// hardcoding nodeID
        ring.join(N40);
        System.out.println("** N40 added to the ring");
        System.out.println("Introducer successor: "   + ring.getIntroducer().getSuccessor().getNodeID());
        System.out.println("Introducer predecessor: " + ring.getIntroducer().getPredecessor().getNodeID());
        
        // create a 4th new node 
        Node N16 = new Node("20.809.80.143", "8080", 7);
        N16.setNodeID(16); // hardcoding nodeID
        ring.join(N16);
        System.out.println("** N16 added to the ring");
        System.out.println("Introducer successor: "   + ring.getIntroducer().getSuccessor().getNodeID());
        System.out.println("Introducer predecessor: " + ring.getIntroducer().getPredecessor().getNodeID());
        
     // create a 5th new node 
        Node N112 = new Node("20.809.80.143", "8080", 7);
        N112.setNodeID(112); // hardcoding nodeID
        ring.join(N112);
        System.out.println("** N112 added to the ring");
        System.out.println("Introducer successor: "   + ring.getIntroducer().getSuccessor().getNodeID());
        System.out.println("Introducer predecessor: " + ring.getIntroducer().getPredecessor().getNodeID());
	}

}
