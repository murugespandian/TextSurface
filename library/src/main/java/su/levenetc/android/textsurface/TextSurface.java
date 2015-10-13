package su.levenetc.android.textsurface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import su.levenetc.android.textsurface.animations.AnimationsSet;
import su.levenetc.android.textsurface.interfaces.ICameraAnimation;
import su.levenetc.android.textsurface.interfaces.ISet;
import su.levenetc.android.textsurface.interfaces.ISurfaceAnimation;
import su.levenetc.android.textsurface.interfaces.ITextSurfaceAnimation;

/**
 * Created by Eugene Levenetc.
 */
public class TextSurface extends FrameLayout {

	private TreeSet<Text> textsTree = new TreeSet<>();
	private SurfaceCamera camera = new SurfaceCamera();

	public TextSurface(Context context) {
		super(context);
		config();
	}

	public TextSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		config();
	}

	private void config() {

		setWillNotDraw(false);
		setBackgroundColor(Color.DKGRAY);
	}

	public SurfaceCamera getCamera() {
		return camera;
	}

	public void play(TYPE type, ISurfaceAnimation... animations) {

		play(new AnimationsSet(type, animations));
	}

	public void play(ISurfaceAnimation... animations) {
		play(new AnimationsSet(TYPE.PARALLEL, animations));
	}

	public void play(ISurfaceAnimation animation) {
		configAnimations(animation);
		animation.setTextSurface(this);
		layout();
		animation.start(null);
	}

	private void layout() {
		Iterator<Text> iterator = textsTree.iterator();
		while (iterator.hasNext()) iterator.next().layout(this);
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		camera.setTransX(right / 2);
//		camera.setTransY(bottom / 2);
	}

	private void configAnimations(ISurfaceAnimation animation) {

		if (animation instanceof ICameraAnimation) {
			((ICameraAnimation) animation).setCamera(camera);
		} else if (animation instanceof ISet) {
			LinkedList<ISurfaceAnimation> animations = ((ISet) animation).getAnimations();
			for (ISurfaceAnimation a : animations) configAnimations(a);
		} else if (animation instanceof ITextSurfaceAnimation) {
			ITextSurfaceAnimation textAnimation = (ITextSurfaceAnimation) animation;
			Text text = textAnimation.getText();
			if (text != null && textsTree.add(text)) textAnimation.setInitValues(text);
		}
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		camera.onDraw(canvas);
		for (Text text : textsTree) text.onDraw(canvas, this);
	}

	public void reset() {
		textsTree.clear();
		camera.reset();
		invalidate();
	}
}