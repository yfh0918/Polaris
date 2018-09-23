package com.polaris.conf.admin.core.model;

/**
 * 配置节点
 */
public class ConfNode {

	private String nodeZK;		// zk of prop
	private String nodeGroup;		// group of prop
	private String nodeGroupCopy;	// group of prop
	public String getNodeGroupCopy() {
		return nodeGroupCopy;
	}

	public void setNodeGroupCopy(String nodeGroupCopy) {
		this.nodeGroupCopy = nodeGroupCopy;
	}

	private String nodeKey; 		// key of prop
	private String nodeValue; 		// value of prop
	private String nodeDesc;		// description of prop
	private String delFlg;          // delete flag

	private String nodeValueReal; 	// value of prop [in zk]

	public String getNodeZK() {
		return nodeZK;
	}

	public void setNodeZK(String nodeZK) {
		this.nodeZK = nodeZK;
	}

	public String getNodeGroup() {
		return nodeGroup;
	}

	public void setNodeGroup(String nodeGroup) {
		this.nodeGroup = nodeGroup;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}

	public String getNodeDesc() {
		return nodeDesc;
	}

	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}

	public String getNodeValueReal() {
		return nodeValueReal;
	}

	public void setNodeValueReal(String nodeValueReal) {
		this.nodeValueReal = nodeValueReal;
	}

	public String getDelFlg() {
		return delFlg;
	}

	public void setDelFlg(String delFlg) {
		this.delFlg = delFlg;
	}

}
