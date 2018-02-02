package org.highweb.webclsdk.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class CLWordDetector implements IWordDetector {

	@Override
	public boolean isWordPart(char c) {
		// TODO Auto-generated method stub
		return Character.isLetter(c) || Character.isDigit(c) || c == '_';
	}

	@Override
	public boolean isWordStart(char c) {
		// TODO Auto-generated method stub
		return Character.isLetter(c) || c == '_';
	}

}
