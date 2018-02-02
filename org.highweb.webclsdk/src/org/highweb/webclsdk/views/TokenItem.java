package org.highweb.webclsdk.views;

public class TokenItem {
	private String name;
	private String identification;
	private int offset;

	public TokenItem(String name, String identification, int line) {
		this.name = name;
		this.identification = identification;
		this.offset = line;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append(":").append(identification).append(":").append(offset);
		return sb.toString();
	}
}
