package cn.zkspy.gui;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

	private List<Map<String, String>> aclData;

	private Map<String, String> metaData;

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public List<Map<String, String>> getAclData() {
		return aclData;
	}

	public void setAclData(List<Map<String, String>> aclData) {
		this.aclData = aclData;
	}

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
