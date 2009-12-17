/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView.ScaleType;

/**
 * @author Michael A. MacDonald
 */
class ZoomScaling extends AbstractScaling {
	
	static final String TAG = "ZoomScaling";

	private Matrix matrix;
	int canvasXOffset;
	int canvasYOffset;
	float scaling;
	float minimumScale;
	
	/**
	 * @param id
	 * @param scaleType
	 */
	public ZoomScaling() {
		super(R.id.itemZoomable, ScaleType.MATRIX);
		matrix = new Matrix();
		scaling = 1;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#getDefaultHandlerId()
	 */
	@Override
	int getDefaultHandlerId() {
		return R.id.itemInputTouchPanZoomMouse;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#isAbleToPan()
	 */
	@Override
	boolean isAbleToPan() {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#isValidInputMode(int)
	 */
	@Override
	boolean isValidInputMode(int mode) {
		return mode == R.id.itemInputTouchPanZoomMouse;
	}
	
	/**
	 * Call after scaling and matrix have been changed to resolve scrolling
	 * @param activity
	 */
	private void resolveZoom(VncCanvasActivity activity)
	{
		activity.vncCanvas.scrollToAbsolute();
		activity.vncCanvas.pan(0,0);
	}
	
	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#zoomIn(android.androidVNC.VncCanvasActivity)
	 */
	@Override
	void zoomIn(VncCanvasActivity activity) {
		resetMatrix();
		scaling += 0.25;
		if (scaling > 4.0)
		{
			scaling = (float)4.0;
			activity.zoomer.setIsZoomInEnabled(false);
		}
		activity.zoomer.setIsZoomOutEnabled(true);
		matrix.postScale(scaling, scaling);
		//Log.v(TAG,String.format("before set matrix scrollx = %d scrolly = %d", activity.vncCanvas.getScrollX(), activity.vncCanvas.getScrollY()));
		activity.vncCanvas.setImageMatrix(matrix);
		resolveZoom(activity);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#getScale()
	 */
	@Override
	float getScale() {
		return scaling;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#zoomOut(android.androidVNC.VncCanvasActivity)
	 */
	@Override
	void zoomOut(VncCanvasActivity activity) {
		resetMatrix();
		scaling -= 0.25;
		if (scaling < minimumScale)
		{
			scaling = (float)minimumScale;
			activity.zoomer.setIsZoomOutEnabled(false);
		}
		activity.zoomer.setIsZoomInEnabled(true);
		matrix.postScale(scaling, scaling);
		//Log.v(TAG,String.format("before set matrix scrollx = %d scrolly = %d", activity.vncCanvas.getScrollX(), activity.vncCanvas.getScrollY()));
		activity.vncCanvas.setImageMatrix(matrix);
		//Log.v(TAG,String.format("after set matrix scrollx = %d scrolly = %d", activity.vncCanvas.getScrollX(), activity.vncCanvas.getScrollY()));
		resolveZoom(activity);
	}

	private void resetMatrix()
	{
		matrix.reset();
		matrix.preTranslate(canvasXOffset, canvasYOffset);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#setScaleTypeForActivity(android.androidVNC.VncCanvasActivity)
	 */
	@Override
	void setScaleTypeForActivity(VncCanvasActivity activity) {
		super.setScaleTypeForActivity(activity);
		scaling = (float)1.0;
		minimumScale = activity.vncCanvas.bitmapData.getMinimumScale();
		canvasXOffset = -activity.vncCanvas.getCenteredXOffset();
		canvasYOffset = -activity.vncCanvas.getCenteredYOffset();
		resetMatrix();
		activity.vncCanvas.setImageMatrix(matrix);
		// Reset the pan position to (0,0)
		activity.vncCanvas.scrollTo(canvasXOffset,canvasYOffset);
	}

}
