package com.scrappers.notepadsnippet.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.scrappers.notepadsnippet.Paint.Paint.brushTrack;
import static com.scrappers.notepadsnippet.Paint.Paint.paintCursor;
import static com.scrappers.notepadsnippet.Paint.Paint.paintView;


public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private float currentX, currentY;
    private Path currentPath;
    private Paint brush;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap currentBitmap;
    private ArrayList<FingerPath> unDonePaths=new ArrayList<>();

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        brush = new Paint();
        brush.setAntiAlias(true);
        brush.setDither(true);
        brush.setColor(DEFAULT_COLOR);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeCap(Paint.Cap.ROUND);
        brush.setXfermode(null);
        brush.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {0.1f, 0.1f, 0.1f}, 0.2f, 1, 1.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    /**
     * Initialize a pre-existing note paint file , otherwise create a new blank paint for the current note
     * @param metrics the device display metrics to get the height & width in case a new blank paint is created
     * @param photoPath the path of the pre-existing note paint file to read
     */
    public void init(DisplayMetrics metrics,String photoPath) {
        /*
         *get Device metrics(w,h)
         */
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        File paintFile=new File(photoPath);
        if(paintFile.exists()){
            /*
             * read the png image as a bitmap
             */
            currentBitmap = BitmapFactory.decodeFile(photoPath);
            /*
             *Create Mutable bitmap image to be able to adjust its height & width if needed & erase its color
             */
            currentBitmap=currentBitmap.copy(currentBitmap.getConfig(),true);
        }else{
            /*
             * Create a new blank image if there's not a pre-existed image
             */
            currentBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            currentBitmap=currentBitmap.copy(currentBitmap.getConfig(),true);
            /*
             *start the bitmap image with a white blank background
             */
            currentBitmap.eraseColor(DEFAULT_BG_COLOR);
        }
        /*
         *Load the default brush size & brush color
         */
        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    /**
     * sets the Stroke of brush to default values
     */
    public void setDefaultStroke() {
        emboss = false;
        blur = false;
    }

    /**
     * set eht current color of the brush
     * @param newColor
     */
    public void setCurrentColor(int newColor){
        currentColor=newColor;
    }

    /**
     * get the current stroke width of the brush
     * @return the stroke width of the brush
     */
    public int getStrokeWidth(){
        return strokeWidth;
    }

    /**
     * sets the stroke width of the paint brush
     * @param newStrokeWidth the new stroke width
     */
    public void setStrokeWidth(int newStrokeWidth){
        strokeWidth=newStrokeWidth;
    }

    /**
     * emboss the current Stroke(make it 3d stroke)
     */
    public void emboss() {
        emboss = true;
        blur = false;
    }

    /**
     * blurs the current stroke
     */
    public void blur() {
        emboss = false;
        blur = true;
    }

    /**
     * destroys the current painted image
     */
    public void destroy() {
        currentBitmap.eraseColor(Color.WHITE);
        setDefaultStroke();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*
         *this draws the canvas by a white color overriding everything
         *canvas.drawColor(DEFAULT_BG_COLOR);
         */
        /*
         * draw the read bitmap image from the storage starting from the top left corner
         * 
         */
        try {
            canvas.drawBitmap(currentBitmap, 0, 0, brush);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        /*  
         * tackle over the fingerPaths array with their parameters used by the Paint brush 
         */
        for (FingerPath fp : paths) {
            /*
             *sets the current path parameters to the brush used 
             */
            brush.setColor(fp.getColor());
            brush.setStrokeWidth(fp.getStrokeWidth());
            if (fp.isEmboss()){
                brush.setMaskFilter(mEmboss);
            }
            if (fp.isBlur()){
                brush.setMaskFilter(mBlur);
            }
            /*
             *draws the canvas using the path fetched from touchStart() , touchMove() & touchUp();
             */
            canvas.drawPath(fp.getPath(), brush);

        }

    }

    /**
     * initialized when the user first touches the paint view
     * @param x the x-coordinate point in which user presses on starting from (0,0) in the upper left corner
     * @param y the y-coordinate point in which user presses on starting from (0,0) in the upper left corner
     */
    private void touchStart(float x, float y) {
        /*
         * initialize the path of the users finger & its parameters(color,emboss,blur,width)
         */
        currentPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, currentPath);
        /*
         * add an array of finger path , each path holds its values
         */
        paths.add(fp);

        /*
         * Set the beginning of the next contour to the point (x,y).
         *
         * @param x The x-coordinate of the start of a new contour
         * @param y The y-coordinate of the start of a new contour
         */
        currentPath.moveTo(x, y);
        /*
         * assign the final values to gain the new values of the current location representing the finger position
         */
        currentX = x;
        currentY = y;

        /*
         * move the brush cursor to the point of finger press
         */
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(paintView.getLayoutParams());
        /*Warning : Margins left & top are the only used ones in this case ; because any view is by default anchored to the
         * upper left corner (0,0) according to java Coordinate System so marginEnd(Right) & marginBottom are totally ignored
         */
        lp.setMargins((int)x,(int)y, 0, 0);
        paintCursor.setLayoutParams(lp);

        /*
         * move the brush cursor to the point of finger path continuously
         */
        RelativeLayout.LayoutParams lpBrushTrack=new RelativeLayout.LayoutParams(brushTrack.getLayoutParams());
        /*Warning : Margins left & top are the only used ones in this case ; because any view is by default anchored to the
         * upper left corner (0,0) according to java Coordinate System so marginEnd(Right) & marginBottom are totally ignored
         */
        lpBrushTrack.setMargins((int)x-11,(int)y-11, 0, 0);
        brushTrack.setLayoutParams(lpBrushTrack);

    }

    /**
     * Moves the current path by the user's fingerPath
     * @param x x-coordinate of the end point on a quadratic curve
     * @param y y-coordinate of the end point on a quadratic curve
     */
    private void touchMove(float x, float y) {
            /*
             * Add a quadratic bezier curve from the last point, approaching control point
             * (x1,y1), and ending at (x2,y2). If no moveTo() call has been made for
             * this contour, the first point is automatically set to (0,0).
             *
             * @param x1 The x-coordinate of the control point on a quadratic curve
             * @param y1 The y-coordinate of the control point on a quadratic curve
             * @param x2 The x-coordinate of the end point on a quadratic curve
             * @param y2 The y-coordinate of the end point on a quadratic curve
             */
            currentPath.quadTo(currentX, currentY, x , y );
            /*
             * assign current position of user's finger(control points) to the new position(end points)
             */
            currentX = x;
            currentY = y;

            /*
             * move the brush cursor to the point of finger path continuously
             */
            RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(paintView.getLayoutParams());
            /*Warning : Margins left & top are the only used ones in this case ; because any view is by default anchored to the
             * upper left corner (0,0) according to java Coordinate System so marginEnd(Right) & marginBottom are totally ignored
             */
            lp.setMargins((int)x,(int)y, 0, 0);
            paintCursor.setLayoutParams(lp);

            /*
             * move the brush cursor to the point of finger path continuously
             */
            RelativeLayout.LayoutParams lpBrushTrack=new RelativeLayout.LayoutParams(brushTrack.getLayoutParams());
            /*Warning : Margins left & top are the only used ones in this case ; because any view is by default anchored to the
             * upper left corner (0,0) according to java Coordinate System so marginEnd(Right) & marginBottom are totally ignored
             */
            lpBrushTrack.setMargins((int)x-11,(int)y-11, 0, 0);
            brushTrack.setLayoutParams(lpBrushTrack);



    }

    private void touchUp() {
        /*
         * Add a line from the last point to the specified point (x,y).
         * If no moveTo() call has been made for this contour, the first point is
         * automatically set to (0,0).
         */
        currentPath.lineTo(currentX, currentY);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Listener for user's touches & moves of user's fingers on the screen
     * @param event the current action taken by the user's finger
     * @return true if user touches the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * Call this view's OnClickListener, if it is defined.  Performs all normal
         * actions associated with clicking: reporting accessibility event, playing
         * a sound, etc.
         *
         * @return True there was an assigned OnClickListener that was called, false
         *         otherwise is returned.
         */
        super.performClick();
        /*
         * get the current x & y coordinates starting from the java coordinate plane
         */
        float x = event.getX();
        float y = event.getY();
        /*
         * checks for action
         */
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;

        }

        return true;
    }

    /**
     * undo the current path track only not its properties(color,stroke,emboss,blur,etc...)
     */
    public void undo(){
        if(paths.size() > 0){
            /*
             * Removes the element at the specified position in this list.
             * Shifts any subsequent elements to the left (subtracts one from their
             * indices).
             *
             * @param index the index of the element to be removed
             * @return the element that was removed from the list
             * @throws IndexOutOfBoundsException {@inheritDoc}
             */
            unDonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    /**
     * redo the current path track only not its properties(color,stroke,emboss,blur,etc...)
     */
    public void redo(){
        if(unDonePaths.size() > 0){
            /*
             * Removes the element at the specified position in this list.
             * Shifts any subsequent elements to the left (subtracts one from their
             * indices).
             *
             * @param index the index of the element to be removed
             * @return the element that was removed from the list
             * @throws IndexOutOfBoundsException {@inheritDoc}
             */
            paths.add(unDonePaths.remove(unDonePaths.size() - 1));
            invalidate();
        }
    }
    /**
     * saves the resulted bitmap from this view in a form of a compressed bitmap png format
     * @param filename the fileName & directory of the png file
     */
    public void save(File filename){
        try (FileOutputStream out = new FileOutputStream(filename)) {
            getBitmapFromView(this).compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the bitmap image from the current Paint View
     * @param view this view
     * @return the resulted bitmap from this view
     */
    private Bitmap getBitmapFromView(View view) {

        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null){
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


}


