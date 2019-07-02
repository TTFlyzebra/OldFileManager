package com.flyzebra.filemanager.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AutoImageView extends ImageView {
	private int width = 0;

	public AutoImageView(Context context) {
		super(context);
	}

	public AutoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int tw = MeasureSpec.getSize(widthMeasureSpec);
		if (tw > 0 && tw != width) {
			width = tw;
			LayoutParams para = getLayoutParams();
			para.height = width;
			setLayoutParams(para);
			postInvalidate();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
