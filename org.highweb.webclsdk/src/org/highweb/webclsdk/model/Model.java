package org.highweb.webclsdk.model;

import org.highweb.webclsdk.views.OutlineItem;

public abstract class Model {
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_PUBLIC_VARIABLE = "public_variable";
	public static final String TYPE_PRIVATE_VARIABLE = "private_variable";
	public static final String TYPE_NONE = "none";
	
	protected Node parent;
	protected String name;
	protected String type;
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
    private OutlineItem outlineItem;
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Node getParent() {
		return parent;
	}
	
	/* The receiver should visit the toVisit object and
	 * pass along the argument. */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);
	
	public String getName() {
		return name;
	}
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public Model(String name) {
	    this.name = name;
	}

	public Model() {
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}

    public void setOutlineItem(OutlineItem oItem) {
        this.outlineItem = oItem;
    }

    public OutlineItem getOutlineItem() {
        return this.outlineItem;
    }

    public String getType() {
        return type;
    }
}
