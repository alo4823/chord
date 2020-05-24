package com.coen317.node;

import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
	
	//if new ring, create DHT
	
	//if you want join a ring, query for ring object by passing the leader ip address
	
	@GetMapping("/addtofingertable")
	public String addtofingertable(@RequestParam List<String> nodeparams) {
		String mytemplate = "Stored to Finger Table: %s:%s";
		
		thisnode.addToFingerTable(nodeparams.get(0), nodeparams.get(1),Integer.parseInt(nodeparams.get(2)));
		
		return String.format(mytemplate, nodeparams.get(0), nodeparams.get(1));
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
	
	@GetMapping("/find")
	public Node find(@RequestParam String findstring) {
		int bits = 7;
		int key = Key.generate(findstring, bits);
		System.out.println(String.format("find key generated %s = %d", findstring, key));
		Node foundnode = thisnode.find(key);
		
		return foundnode;
	}
	
	public String getipAddress() throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		String addr = localhost.getHostAddress();
		return addr;
		
	}
	
	@GetMapping("/getNode")
	public Node callNode(@RequestParam List<String> nodeinfo){
		String nodeaddr = String.format("http://%s:%s", nodeinfo.get(0), nodeinfo.get(1));
		System.out.println("getNode nodeaddr="+nodeaddr);
		RestTemplate restTemplate = new RestTemplate();
		Node node = restTemplate.getForObject(
					nodeaddr+"/getSuccessor", Node.class);
		return node;
	}
}
