package org.highweb.webclsdk.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.highweb.webclsdk.model.Node;

public class TreeLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof Node) {
		    String type = ((Node)element).getType();
		    switch (type) {
            case Node.TYPE_FUNCTION:
                descriptor = TreeViewerPlugin.getImageDescriptor("method_public_obj.gif");
                break;
            case Node.TYPE_PUBLIC_VARIABLE:
                descriptor = TreeViewerPlugin.getImageDescriptor("field_public_obj.gif");
                break;
            case Node.TYPE_PRIVATE_VARIABLE:
                descriptor = TreeViewerPlugin.getImageDescriptor("field_private_obj.gif");
                break;
            default:
		        return null;
		    }
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof Node) {
			if(((Node)element).getName() == null) {
				return "Node";
			} else {
				return ((Node)element).getName();
			}
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
