package org.highweb.webclsdk.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class CLContentAssistProcessor implements IContentAssistProcessor {

	public CLContentAssistProcessor() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 * 
	 * Code referred to : http://alvinalexander.com/java/jwarehouse/eclipse/org.eclipse.debug.examples.ui/src/org/eclipse/debug/examples/ui/pda/editor/PDAContentAssistProcessor.java.shtml
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		// TODO Auto-generated method stub
		int index = offset - 1;
        StringBuffer prefix = new StringBuffer();
        IDocument document = viewer.getDocument();
        while (index > 0) {
            try {
                char prev = document.getChar(index);
                if (Character.isWhitespace(prev)) {
                    break;
                }
                prefix.insert(0, prev);
                index--;
            } catch (BadLocationException e) {
            }
        }
        
        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
        String[] keywords = CLScanner.builtInFunctions;
        if (prefix.length() > 0) {
            String word = prefix.toString();
            for (int i = 0; i < keywords.length; i++) {
                String keyword = keywords[i];
                if (keyword.startsWith(word) && word.length() < keyword.length()) {
                    proposals.add(new CompletionProposal(keyword + "()", index + 1, offset - (index + 1), keyword.length() + 1));
                }
            }
        } else {
            // propose all keywords
            for (int i = 0; i < keywords.length; i++) {
                String keyword = keywords[i];
                proposals.add(new CompletionProposal(keyword + "()", offset, 0, keyword.length() + 1));
            }
        }
        if (!proposals.isEmpty()) {
            return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
        }
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
