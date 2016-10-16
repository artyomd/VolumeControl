package artyomd.volumecontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by artyomd on 4/13/16.
 */
public class CustomView extends View {

    private Paint colorPaint = new Paint();
    private Paint pointerPaint = new Paint();
    private long radius = 300;
    private float total_angle = 0;
    private float angle=0;
    private float center_x;
    private float center_y;
    private float x0;
    private float y0;
    private float value= 0;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
    }

    public float getValue(){
        return this.value;
    }
    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                0, 0);

        try {
            if (a.getString(R.styleable.CustomView_colour) != null) {
                colorPaint.setColor(Color.parseColor(a.getString(R.styleable.CustomView_colour)));
            } else {
                colorPaint.setColor(Color.GRAY);
            }
            if (a.getString(R.styleable.CustomView_pointerColour) != null) {
                pointerPaint.setColor(Color.parseColor(a.getString(R.styleable.CustomView_pointerColour)));
            } else {
                pointerPaint.setColor(Color.BLACK);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();


        if (actionMasked == MotionEvent.ACTION_DOWN)
        {
            x0 = event.getX();
            y0 = event.getY();
            return true;
        }
        else if (actionMasked == MotionEvent.ACTION_MOVE)
        {

            float x = event.getX() - center_x;
            float y = center_y - event.getY();

            if ((x != 0) && (y != 0))
            {
                double angleB = computeAngle(x, y);

                x = x0 - center_x;
                y = center_y - y0;
                double angleA = computeAngle(x, y);

                angle = (float) (angleA - angleB);
                calculateValue();
                this.invalidate();
                return true;
            }
        }
        else if ((actionMasked == MotionEvent.ACTION_UP) || (actionMasked == MotionEvent.ACTION_CANCEL))
        {
            total_angle += angle;
            angle=0;
            while (total_angle>360&&total_angle>0){
                total_angle-=360;
            }
            while (total_angle>-360&&total_angle<0){
                total_angle+=360;
            }
            calculateValue();
            invalidate();

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.center_x = getWidth()/2;
        this.center_y = getHeight()/2;
        drawCircle(canvas);
        drawLine(canvas);
        drawNumbersAndDots(canvas);
        displayValue(canvas);

    }

    private void drawNumbersAndDots(Canvas canvas) {
        float dAlfa = 360 / 10;
        float alfa = 0;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(90);
        paint.setColor(Color.BLUE);
        for (int i = 0; i < 10; i++) {
            canvas.drawText(String.valueOf(i), (float) (center_x + (radius + 150) * Math.cos(Math.toRadians(-alfa))), (float) (center_y - (radius + 150) * Math.sin(Math.toRadians(-alfa))), paint);
            alfa += dAlfa;
        }
        dAlfa = 360 / 20;
        alfa = 0;
        for (int i = 0; i < 20; i++) {
            canvas.drawCircle((float) (center_x- (radius + 50) * Math.cos(Math.toRadians(alfa))), (float) (center_y- (radius + 50) * Math.sin(Math.toRadians(alfa))), 5, paint);
            alfa += dAlfa;
        }
    }

    private void drawCircle(Canvas canvas) {
        colorPaint.setAntiAlias(true);
        colorPaint.setDither(true);
        canvas.drawCircle(center_x,center_y, radius, colorPaint);
    }

    private void drawLine(Canvas canvas) {
        float tangle;
        if(angle==0){
            tangle = total_angle;
        }
        else{
            tangle=total_angle+angle;
        }
        pointerPaint.setAntiAlias(true);
        pointerPaint.setDither(true);
        pointerPaint.setStrokeWidth(15);
        canvas.drawLine(center_x,center_y, (float) (center_x + radius * Math.cos(Math.toRadians(tangle))), (float) (center_y+ radius * Math.sin(Math.toRadians(tangle))), pointerPaint);
    }

    private void displayValue(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(90);
        canvas.drawText("Value:" + this.value, center_x - 300, center_y + 700, paint);
    }

    private void calculateValue(){
        float tangle;
        if(angle==0){
            tangle = total_angle;
        }
        else{
            tangle=total_angle+angle;
        }
        value = tangle / 36;
    }

    private double computeAngle(float x, float y)
    {
        final double RADS_TO_DEGREES = 360 / (java.lang.Math.PI * 2);
        double result = java.lang.Math.atan2(y, x) * RADS_TO_DEGREES;

        if (result < 0)
        {
            result = 360 + result;
        }

        return result;
    }
}
