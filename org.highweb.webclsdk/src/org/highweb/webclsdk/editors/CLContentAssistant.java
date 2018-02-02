package org.highweb.webclsdk.editors;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.widgets.Shell;

/*
 * Code referrd to : http://alvinalexander.com/java/jwarehouse/eclipse/org.eclipse.debug.examples.ui/src/org/eclipse/debug/examples/ui/pda/editor/PDAContentAssistant.java.shtml
 */
public class CLContentAssistant extends ContentAssistant {

	public CLContentAssistant() {
		// TODO Auto-generated constructor stub
		super();

		CLContentAssistProcessor processor = new CLContentAssistProcessor();
		setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);

		enableAutoActivation(true);
		setAutoActivationDelay(100);
		enableAutoInsert(false);

		setInformationControlCreator(getInformationControlCreator());
	}

	private IInformationControlCreator getInformationControlCreator() {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				// TODO Auto-generated method stub
				return new DefaultInformationControl(parent);
			}
			
		};
	}

}
