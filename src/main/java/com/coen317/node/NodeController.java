package com.coen317.node;

import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {
	private static final String template = "Stored Content: %d %s";
	
	InputData node01 = new InputData();
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
	@GetMapping("/inputdata")
	public String inputdata(@RequestParam List<String> myparams) {
		/*
		 *  Browser Usage Example: http://localhost:8080/inputdata?myparams=3,this
		 *  val = 3
		 *  content = this
		 *  
		 */
		
		if(myparams.size() != 2) {
			return "Error: Parameters not correct";
		}
		else {
			long val = Long.parseLong(myparams.get(0));
			String content = myparams.get(1);
			node01.setData(val, content);
			return String.format(template, val, content);
		}
	}
	
	@GetMapping("/getdata")
	public String getdata(@RequestParam(value = "key") long key) {
		String got_content = node01.getContent(key);
		
		String addr = null;
		
		try {
			addr = getipAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.format("%s Retrieved ID: %d and Content: %s", addr, key, got_content);
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
}
