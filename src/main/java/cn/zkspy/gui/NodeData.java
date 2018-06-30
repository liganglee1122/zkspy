package cn.zkspy.gui;

import java.io.Serializable;

public class NodeData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8814076101091284128L;

	/**
	 * 
	 */
	private String curPath;

	private String data;
	
	
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCurPath() {
		return curPath;
	}

	public void setCurPath(String curPath) {
		this.curPath = curPath;
	}

}
