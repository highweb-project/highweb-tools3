package org.highweb.webclsdk.views;

public class OutlineItem {
	private String itemLabel;
	private int offset;
	private int itemLength;

	public OutlineItem(String label, int offset, int length) {
		// TODO Auto-generated constructor stub
		itemLabel = label;
		this.offset = offset;
		itemLength = length;
	}

	public String toString() {
		return itemLabel;
	}

	public int getOffset() {
		return offset;
	}

	public int getItemLength() {
		return itemLength;
	}
}
