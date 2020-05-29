package com.coen317.node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.coen317.node.property.FileStorageProperties;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({
	FileStorageProperties.class
})

public class NodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NodeApplication.class, args);
		
		//chordTest();
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
        
        // JSON Object to String
        System.out.println(N32.toString());
        
        // Setup Fingertable for N32
        ArrayList<Node> fingerTable = new ArrayList<Node>();
        fingerTable.add(N40);
        fingerTable.add(N40);
        fingerTable.add(N40);
        fingerTable.add(N40);
        fingerTable.add(N112);
        fingerTable.add(N112);
        fingerTable.add(N112);
        N32.setFingerTable(fingerTable);
        
        N32.printFingerTable();
        
       //Find Test Key = 60
        Node findTest01 = N32.find(60);
        System.out.println("Find Test Node(112): " + findTest01.getNodeID());
        
        //Find Test Key = 60
        Node findTest02 = N32.find(120);
        System.out.println("Find Test Node(16): " + findTest02.getNodeID());
	}
}
