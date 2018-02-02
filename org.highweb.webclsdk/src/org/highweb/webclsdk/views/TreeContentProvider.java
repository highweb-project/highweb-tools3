package org.highweb.webclsdk.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.highweb.webclsdk.model.DeltaEvent;
import org.highweb.webclsdk.model.IDeltaListener;
import org.highweb.webclsdk.model.Model;
import org.highweb.webclsdk.model.Node;

public class TreeContentProvider implements ITreeContentProvider, IDeltaListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((Node)oldInput);
		}
		if(newInput != null) {
			addListenerTo((Node)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(Node box) {
		box.removeListener(this);
		for (Iterator iterator = box.getNodes().iterator(); iterator.hasNext();) {
			Node aBox = (Node) iterator.next();
			removeListenerFrom(aBox);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(Node box) {
		box.addListener(this);
		for (Iterator iterator = box.getNodes().iterator(); iterator.hasNext();) {
			Node aBox = (Node) iterator.next();
			addListenerTo(aBox);
		}
	}
	
	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Node) {
			Node box = (Node)parentElement;
			return concat(box.getNodes().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] object) {
		Object[] both = new Object[object.length];
		System.arraycopy(object, 0, both, 0, object.length);
		return both;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof Model) {
			return ((Model)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object movingBox = ((Model)event.receiver()).getParent();
		viewer.refresh(movingBox, false);
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}

}
