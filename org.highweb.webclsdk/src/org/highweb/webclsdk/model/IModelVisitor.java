package org.highweb.webclsdk.model;



public interface IModelVisitor {
	public void visitNode(Node box, Object passAlongArgument);
}
