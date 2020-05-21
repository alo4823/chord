package com.coen317.node;

import java.util.*;

public class InputData {

	private HashMap<Long, String> nodes = new HashMap<Long, String>();
	//private final long id;
	//private final String content;

	public InputData() {
		//nodes.put(id, content);
		//this.id = id;
		//this.content = content;
	}
	/*
	public long getId() {
		return id;
	}
	
	public String getContent() {
		return content;
	}
	*/
	
	public void setData(long id, String content) {
		nodes.put(id, content);
	}
	public String getContent(long key) {
		return nodes.get(key);
	}
}
