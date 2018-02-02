package org.highweb.webclsdk.views.commons;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public class SWTApi {
	public static GridData getGridData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace,
			int horizontalSpan, int verticalSpan, int widthHint, int heightHint) {

		GridData gridData= new GridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace);
	
		if (horizontalSpan > 0) {
			gridData.horizontalSpan= horizontalSpan;
		}
		if (verticalSpan > 0) {
			gridData.verticalSpan= verticalSpan;
		}
		if (widthHint > 0) {
			gridData.widthHint= widthHint;
		}
		if (heightHint > 0) {
			gridData.heightHint= heightHint;
		}
	
		return gridData;
	}
    
    public static GridData setLayoutData(Control control, int horizontalAlignment , int verticalAlignment, boolean grabExcessHorizontalSpace,
			boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan, int widthHint, int heightHint) {
		GridData gd = getGridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan, verticalSpan, widthHint, heightHint);
		control.setLayoutData(gd);
		return gd;
	}
}
