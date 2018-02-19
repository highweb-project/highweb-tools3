package org.highweb.webclsdk.views;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.highweb.webclsdk.editors.CLEditor;
import org.highweb.webclsdk.model.Model;
import org.highweb.webclsdk.model.Node;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class KernelSourceOutlineView extends ContentOutlinePage {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.highweb.webclsdk.views.KernelSourceOutlineView";

    // private TableViewer viewer;
    private Action action1;
    private Action action2;
    private Action doubleClickAction;
    private CLEditor clEditor;
    private TreeViewer viewer;
    private ArrayList<TokenItem> items;
    private Node root;

    private Composite shell;

    /*
     * The content provider class is responsible for providing objects to the
     * view. It can wrap existing objects in adapters or simply return objects
     * as-is. These objects may be sensitive to the current input of the view,
     * or ignore it and always show the same content (like Task List, for
     * example).
     */

    class ViewContentProvider implements ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            System.out.println("call inputChanged");
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            System.out.println("call getElements");
            try {
                ArrayList<OutlineItem> outlineItems = new ArrayList<OutlineItem>();
                int itemsCount = items.size();
                for (int i = 0; i < itemsCount; i++) {
                    String[] itemElements = items.get(i).toString().split(":");
                    // if(itemElements[1].equals("/")) {
                    // i++;
                    // String[] nextItemElements =
                    // items.get(i).toString().split(":");
                    // if(nextItemElements[1].equals("/")) {
                    //
                    // }
                    // }
                    if (itemElements[1].equals("variable_declaration")
                            || itemElements[1].equals("function_declaration")) {
                        StringBuffer sb = new StringBuffer();
                        if (itemElements[0].contains("_newline")) {
                            itemElements[0] = itemElements[0].substring(0, itemElements[0].indexOf("_newline"));
                        }
                        if (itemElements[1].equals("function_declaration")) {
                            sb.append(itemElements[0] + "()").append(" : ");
                        } else {
                            sb.append(itemElements[0]).append(" : ");
                        }
                        String[] prevItemElements = null;
                        if (i >= 2) {
                            prevItemElements = items.get(i - 2).toString().split(":");
                            if (prevItemElements[1].equals("kernel_keyword")) {
                                sb.append(prevItemElements[0]).append(" ");
                            }
                        }
                        prevItemElements = items.get(i - 1).toString().split(":");
                        sb.append(prevItemElements[0]);
                        OutlineItem oItem = new OutlineItem(sb.toString(), Integer.parseInt(itemElements[2]),
                                itemElements[0].length());
                        outlineItems.add(oItem);
                    }
                }
                // return new String[] { "One", "Two", "Three" };
                return outlineItems.toArray();
            } catch (Exception e) {
                return new String[] { "" };
            }
        }

        @Override
        public Object[] getChildren(Object parent) {
            // TODO Auto-generated method stub
            System.out.println("call getChildren");
            return null;
        }

        @Override
        public Object getParent(Object parent) {
            // TODO Auto-generated method stub
            System.out.println("call getParent");
            return null;
        }

        @Override
        public boolean hasChildren(Object parent) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    class ViewLabelProvider extends LabelProvider {
        public Image getImage(Object obj) {
            // return PlatformUI.getWorkbench().
            // getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
            return null;
        }
    }

    class NameSorter extends ViewerSorter {
    }

    class OutlineItemSelectionChangedListener implements ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            // TODO Auto-generated method stub

            if (!event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                OutlineItem oItem = ((Model) selection.getFirstElement()).getOutlineItem();
                clEditor.selectAndReveal(oItem.getOffset(), oItem.getItemLength());
            }
        }

    }

    /**
     * The constructor.
     */
    // public KernelSourceOutlineView() {
    // super();
    // }

    public KernelSourceOutlineView(CLEditor editor) {
        super();
        clEditor = editor;
    }

    public void init(IPageSite pageSite) {
        super.init(pageSite);
        System.out.println("CL Editor outline view starts");
        pageSite.setSelectionProvider(this);
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        shell = parent;
        viewer = getTreeViewer();
        // viewer.setContentProvider(new ViewContentProvider());
        // viewer.setLabelProvider(new ViewLabelProvider());
        // viewer.addSelectionChangedListener(new
        // OutlineItemSelectionChangedListener());
        // viewer.setInput(clEditor.getInitalInput());

        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeLabelProvider());
        viewer.addSelectionChangedListener(new OutlineItemSelectionChangedListener());

//        Object[] expandedElements = viewer.getExpandedElements();
//        TreePath[] expandedTreePaths = viewer.getExpandedTreePaths();

        viewer.setInput(clEditor.getInitalInput());

        viewer.expandAll();
//        viewer.setExpandedElements(expandedElements);
//        viewer.setExpandedTreePaths(expandedTreePaths);

    }

/*    public Tree getInitalInput() {
        Tree root = new Tree();

        Tree books = new Tree("Books");
        Tree games = new Tree("Games");

        Tree books2 = new Tree("More books");
        Tree games2 = new Tree("More games");

        root.addTree(books);
        root.addTree(games);
        root.addTree(new Tree());

        books.addTree(books2);
        games.addTree(games2);

        books.addBook(new Book("The Lord of the Rings", "J.R.R.", "Tolkien"));
        books.addTerminal(new Terminal("Taj Mahal", "Reiner", "Knizia"));
        books.addBook(new Book("Cryptonomicon", "Neal", "Stephenson"));
        books.addBook(new Book("Smalltalk, Objects, and Design", "Chamond", "Liu"));
        books.addBook(new Book("A Game of Thrones", "George R. R.", " Martin"));
        books.addBook(new Book("The Hacker Ethic", "Pekka", "Himanen"));
        // books.addBox(new MovingBox());

        books2.addBook(new Book("The Code Book", "Simon", "Singh"));
        books2.addBook(new Book("The Chronicles of Narnia", "C. S.", "Lewis"));
        books2.addBook(new Book("The Screwtape Letters", "C. S.", "Lewis"));
        books2.addBook(new Book("Mere Christianity ", "C. S.", "Lewis"));

        games.addTerminal(new Terminal("Tigris & Euphrates", "Reiner", "Knizia"));
        games.addTerminal(new Terminal("La Citta", "Gerd", "Fenchel"));
        games.addTerminal(new Terminal("El Grande", "Wolfgang", "Kramer"));
        games.addTerminal(new Terminal("The Princes of Florence", "Richard", "Ulrich"));
        games.addTerminal(new Terminal("The Traders of Genoa", "Rudiger", "Dorn"));

        games2.addTerminal(new Terminal("Tikal", "M.", "Kiesling"));
        games2.addTerminal(new Terminal("Modern Art", "Reiner", "Knizia"));
        return root;
    }*/

    public TreeViewer getViewer() {
        return viewer;
    }

    public void updateItems(ArrayList<TokenItem> tokenItems) {
        System.out.println("updateItems()");
        items = tokenItems;
        // viewer.setContentProvider(new MovingBoxContentProvider());
        // viewer.setLabelProvider(new MovingBoxLabelProvider());
        // viewer.setInput(clEditor.getInitalInput());
        // viewer.expandAll();
        // update();
        // viewer.setExpandedElements(viewer.getExpandedElements());
        // viewer.setExpandedTreePaths(viewer.getExpandedTreePaths());
        // viewer.refresh();

//        Object[] expandedElements = viewer.getExpandedElements();
//        TreePath[] expandedTreePaths = viewer.getExpandedTreePaths();

        viewer.setInput(clEditor.getInitalInput());
        viewer.expandAll();
//        viewer.setExpandedElements(expandedElements);
//        viewer.setExpandedTreePaths(expandedTreePaths);
    }

    // /**
    // * This is a callback that will allow us
    // * to create the viewer and initialize it.
    // */
    // public void createPartControl(Composite parent) {
    // viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL |
    // SWT.V_SCROLL);
    // viewer.setContentProvider(new ViewContentProvider());
    // viewer.setLabelProvider(new ViewLabelProvider());
    // viewer.setSorter(new NameSorter());
    // viewer.setInput(getViewSite());
    //
    // // Create the help context id for the viewer's control
    // PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
    // "org.highweb.webclsdk.viewer");
    // getSite().setSelectionProvider(viewer);
    // makeActions();
    // hookContextMenu();
    // hookDoubleClickAction();
    // contributeToActionBars();
    // }
    //
    // private void hookContextMenu() {
    // MenuManager menuMgr = new MenuManager("#PopupMenu");
    // menuMgr.setRemoveAllWhenShown(true);
    // menuMgr.addMenuListener(new IMenuListener() {
    // public void menuAboutToShow(IMenuManager manager) {
    // KernelSourceOutlineView.this.fillContextMenu(manager);
    // }
    // });
    // Menu menu = menuMgr.createContextMenu(viewer.getControl());
    // viewer.getControl().setMenu(menu);
    // getSite().registerContextMenu(menuMgr, viewer);
    // }
    //
    // private void contributeToActionBars() {
    // IActionBars bars = getViewSite().getActionBars();
    // fillLocalPullDown(bars.getMenuManager());
    // fillLocalToolBar(bars.getToolBarManager());
    // }
    //
    // private void fillLocalPullDown(IMenuManager manager) {
    // manager.add(action1);
    // manager.add(new Separator());
    // manager.add(action2);
    // }
    //
    // private void fillContextMenu(IMenuManager manager) {
    // manager.add(action1);
    // manager.add(action2);
    // // Other plug-ins can contribute there actions here
    // manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    // }
    //
    // private void fillLocalToolBar(IToolBarManager manager) {
    // manager.add(action1);
    // manager.add(action2);
    // }
    //
    // private void makeActions() {
    // action1 = new Action() {
    // public void run() {
    // showMessage("Action 1 executed");
    // }
    // };
    // action1.setText("Action 1");
    // action1.setToolTipText("Action 1 tooltip");
    // action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
    // getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    //
    // action2 = new Action() {
    // public void run() {
    // showMessage("Action 2 executed");
    // }
    // };
    // action2.setText("Action 2");
    // action2.setToolTipText("Action 2 tooltip");
    // action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
    // getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    // doubleClickAction = new Action() {
    // public void run() {
    // ISelection selection = viewer.getSelection();
    // Object obj = ((IStructuredSelection)selection).getFirstElement();
    // showMessage("Double-click detected on "+obj.toString());
    // }
    // };
    // }
    //
    // private void hookDoubleClickAction() {
    // viewer.addDoubleClickListener(new IDoubleClickListener() {
    // public void doubleClick(DoubleClickEvent event) {
    // doubleClickAction.run();
    // }
    // });
    // }
    // private void showMessage(String message) {
    // MessageDialog.openInformation(
    // viewer.getControl().getShell(),
    // "Sample View",
    // message);
    // }
    //
    // /**
    // * Passing the focus request to the viewer's control.
    // */
    // public void setFocus() {
    // viewer.getControl().setFocus();
    // }
}
