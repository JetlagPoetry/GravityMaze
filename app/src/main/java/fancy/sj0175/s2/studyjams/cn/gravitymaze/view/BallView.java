package fancy.sj0175.s2.studyjams.cn.gravitymaze.view;

import android.content.Context;
import android.util.AttributeSet;

public class BallView extends android.support.v7.widget.AppCompatImageView {

	
	public BallView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

	public BallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void moveTo(int l,int t){
		super.setFrame(l, t, l+getWidth(), t+getHeight());
	}
}
