package org.highweb.webclsdk.editors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.model.Model;
import org.highweb.webclsdk.model.Node;
import org.highweb.webclsdk.util.MessageUtil;
import org.highweb.webclsdk.views.KernelSourceOutlineView;
import org.highweb.webclsdk.views.OutlineItem;
import org.highweb.webclsdk.views.TokenItem;
import org.osgi.framework.Bundle;

public class CLEditor extends TextEditor {

    private ColorManager colorManager;
    private KernelSourceOutlineView kernelOutlinePage;
    public static CLEditor prevEditor;
    public static final Object syncObj = new Object();
    private ArrayList<TokenItem> tokenItems;
    private Node root;
    private IDocumentListener documentListener;
    private IDocument document;
    protected Timer timer;
    private TimerTask timerTask;

    @Override
    protected void handleEditorInputChanged() {
        // TODO Auto-generated method stub
        super.handleEditorInputChanged();
    }

    public CLEditor() {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new CLConfiguration(colorManager));
        // setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        // setDocumentProvider(new XMLDocumentProvider());
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        // TODO Auto-generated method stub
        super.doSetInput(input);

        final IEditorInput input1 = getEditorInput();
        IDocumentProvider documentProvider = getDocumentProvider();
        document = documentProvider.getDocument(input1);
        documentListener = new IDocumentListener() {
            @Override
            public void documentAboutToBeChanged(DocumentEvent arg0) {
                // Empty
            }

            @Override
            public void documentChanged(DocumentEvent arg0) {
                // Do something HERE
                System.out.println("documentChanged()");
                initTimer();
                timer.schedule(timerTask, 1000);
            }
        };
        document.addDocumentListener(documentListener);
    }

    public void initTimer() {
        System.out.println("initTimer()");
        if (timerTask != null) {
            timerTask.cancel(); // 占쏙옙占쎌뵠�솒��ask�몴占� timer 占쎄괠占쎈퓠占쎄퐣 筌욑옙占쎌뜖甕곌쑬�뵛占쎈뼄
            timerTask = null;
        }
        timerTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                refreshOutlineItems(document.get());
            }
        };
        if (timer != null) {
            timer.cancel(); // 占쎈뮞�놂옙鴉딇겎ask�⑨옙 占쏙옙占쎌뵠�솒紐껓옙占� �뿆�뫁�꺖占쎈립占쎈뼄.
            timer.purge(); // task占쎄괠占쎌벥 筌뤴뫀諭� task�몴占� 占쎌젫椰꾧퀬釉놂옙�뼄.
            timer = null;
        }
        timer = new Timer();
    }

    public void dispose() {
        colorManager.dispose();
        document.removeDocumentListener(documentListener);
        super.dispose();
    }

    public Object getAdapter(Class adapter) {
        if (adapter.equals(IContentOutlinePage.class)) {
            refreshOutlineItems();
            if (kernelOutlinePage == null) {
                kernelOutlinePage = new KernelSourceOutlineView(CLEditor.this);
                return kernelOutlinePage;
            }
        }
        return super.getAdapter(adapter);
    }

    @Override
    protected void editorSaved() {
        super.editorSaved();
        System.out.println("call editorSaved()");
        refreshOutlineItems();
    }

    @Override
    public void setFocus() {
        super.setFocus();
        System.out.println("call setFocus()");
        if (prevEditor == null || !prevEditor.equals(this)) {
            prevEditor = this;
            refreshOutlineItems();
        }
    }

    private Job refreshOutlineJob = new Job("Refresh Outline Job") {

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            // TODO Auto-generated method stub
            refreshOutlineItems();
            return null;
        }

    };

    private static Job currentRunningJob;

    private void refreshOutlineItems() {
        refreshOutlineItems(null);
    }

    private void refreshOutlineItems(String document) {
            System.out.println(Thread.currentThread().getName());
            if (currentRunningJob != null) {
                currentRunningJob.cancel();
            }
            currentRunningJob = new Job("refreshOutlineItems") {

                @Override
                protected IStatus run(IProgressMonitor arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("refreshOutlineItems()");
                    Bundle bundle = Activator.getDefault().getBundle();
                    IPath statePath = Platform.getStateLocation(bundle);
                    String cparserPath = "utils" + File.separator + "cparser" + File.separator + "cparser.exe";
                    String sourcePath = "";
                    // -> job 占쎌몵嚥∽옙 獄쏅떽��疫뀐옙
                    // -> 占쏙옙占쎌뵠占쎈릅占쎌뵠 筌롫뜆�뀭 占쎈뻻占쎌젎 占쎌몵嚥∽옙 占쏙옙占쎌뵠�솒占�
                    // -> 占쏙옙占쎌뵠占쎈릅 �굜�뮆媛� 1�룯占� 2�룯占�
                    // synchronized (CLEditor.syncObj) {
                    IEditorInput editorInput = getEditorInput();
                    if (editorInput instanceof FileEditorInput) {
                        IFile sourceFile = ((FileEditorInput) editorInput).getFile();
                        sourcePath = sourceFile.getLocation().toOSString();
                        if (document != null) {
                            try {
                                File file = new File(statePath + File.separator + "utils" + File.separator + "cparser"
                                        + File.separator + "temp.txt");
                                FileWriter fw = new FileWriter(file);
                                BufferedWriter bw = new BufferedWriter(fw);

                                bw.write(document);
                                bw.close();
                                sourcePath = file.getPath();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                        ProcessBuilder pBuilder = null;
                        String[] args = new String[] { statePath + File.separator + cparserPath, sourcePath, "-f",
                                statePath + File.separator + "utils" + File.separator + "cparser" + File.separator
                                        + "output.txt" };
                        pBuilder = new ProcessBuilder(args);
                        try {
                            Process p = pBuilder.start();
                            p.waitFor();

                            File outlineFile = new File(statePath + File.separator + "utils" + File.separator
                                    + "cparser" + File.separator + "output.txt");
                            BufferedReader reader = new BufferedReader(new FileReader(outlineFile));
                            String line = "";
                            tokenItems = new ArrayList<TokenItem>();
                            while ((line = reader.readLine()) != null) {
                                try {
                                    String[] lineElements = line.split(":");
                                    tokenItems.add(new TokenItem(lineElements[0], lineElements[1],
                                            Integer.parseInt(lineElements[2])));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            reader.close();
                            parseTokenItem(tokenItems);
                            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    kernelOutlinePage.updateItems(tokenItems);
                                }

                            });
                            // kernelOutlinePage.updateItems(tokenItems);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            MessageUtil.showMessage(MessageDialog.ERROR, "refreshOutlineItems", e.toString());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            MessageUtil.showMessage(MessageDialog.ERROR, "refreshOutlineItems", e.toString());
                        }
                    }
                    // }
                    return Status.OK_STATUS;
                }
            };
            currentRunningJob.schedule();
    }

    private void parseTokenItem(ArrayList<TokenItem> tokenItems) {
        System.out.println("parseTokenItem()");
//        root.clear();
        root = new Node();
        Node function = null;
        boolean isFunction = false;
        int functionBegin = 0;
        int functionEnd = 0;
        ArrayList<OutlineItem> outlineItems = new ArrayList<OutlineItem>();
        int itemsCount = tokenItems.size();
        for (int i = 0; i < itemsCount; i++) {
            String[] itemElements = tokenItems.get(i).toString().split(":");
            // if(itemElements[1].equals("/")) {
            // i++;
            // String[] nextItemElements =
            // items.get(i).toString().split(":");
            // if(nextItemElements[1].equals("/")) {
            //
            // }
            // }
            if (itemElements[1].equals("variable_declaration") || itemElements[1].startsWith("function_")) {
                // System.out.println(tokenItems.get(i).toString());
                StringBuffer sb = new StringBuffer();
                if (itemElements[0].contains("_newline")) {
                    itemElements[0] = itemElements[0].substring(0, itemElements[0].indexOf("_newline"));
                }
                if (itemElements[1].startsWith("function")) {
                    sb.append(itemElements[0] + "()").append(" : ");
                    isFunction = true;
                    String[] temp = itemElements[1].toString().split("/");
                    String[] scope = temp[1].split(",");
                    functionBegin = Integer.parseInt(scope[0]);
                    functionEnd = Integer.parseInt(scope[1]);
                    function = new Node("", Node.TYPE_FUNCTION);
                    addOnMainThread(root, function);
                } else {
                    sb.append(itemElements[0]).append(" : ");
                }
                String[] prevItemElements = null;
                if (i >= 2) {
                    prevItemElements = tokenItems.get(i - 2).toString().split(":");
                    if (prevItemElements[1].equals("kernel_keyword")) {
                        sb.append(prevItemElements[0]).append(" ");
                    }
                }
                prevItemElements = tokenItems.get(i - 1).toString().split(":");
                sb.append(prevItemElements[0]);
                OutlineItem oItem = new OutlineItem(sb.toString(), Integer.parseInt(itemElements[2]),
                        itemElements[0].length());
                outlineItems.add(oItem);

                if (isFunction) {
                    if (function.getName() == null || function.getName() == "") {
                        function.setName(sb.toString());
                        function.setOutlineItem(oItem);
                    }
                    int offset = Integer.parseInt(itemElements[2]);
                    if (offset > functionBegin && offset < functionEnd) {
                        Node var = new Node(sb.toString(), Node.TYPE_PRIVATE_VARIABLE);
                        var.setOutlineItem(oItem);
                        addOnMainThread(function, var);
                    }
                    if (offset > functionEnd) {
                        isFunction = false;
                        Node var = new Node(sb.toString(), Node.TYPE_PUBLIC_VARIABLE);
                        var.setOutlineItem(oItem);
                        addOnMainThread(root, var);
                    }
                } else {
                    Node var = new Node(sb.toString(), Node.TYPE_PUBLIC_VARIABLE);
                    var.setOutlineItem(oItem);
                    addOnMainThread(root, var);
                }

            }
        }
    }

    private void addOnMainThread(Node root, Node node) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                root.addNode((Node) node);
            }
        });
    }

    public Node getInitalInput() {
        if (root == null) {
            root = new Node();
            root.addNode(new Node("Pending...", Node.TYPE_NONE));
        }
        return root;
    }

}
