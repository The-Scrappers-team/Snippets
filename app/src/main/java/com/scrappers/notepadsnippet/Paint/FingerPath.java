package com.scrappers.notepadsnippet.Paint;

import android.graphics.Path;

public class FingerPath {

    private int color;
    private boolean emboss;
    private boolean blur;
    private  int strokeWidth;
    private Path path;


    /**
     * Create a FingerPath with specific parameters for each single Path(android.graphics)
     * @param color the current color of the path
     * @param emboss emboss the path stroke
     * @param blur blur the path stroke
     * @param strokeWidth the stroke width of the current path
     * @param path the current path
     */
    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    /**
     * sets the current color of this path
     * @param color
     */
    private void setColor(int color){
        this.color=color;
    }

    /**
     * sets the stroke width to emboss(3d stroke)
     * @param emboss true-false
     */
    private void setEmboss(boolean emboss){
        this.emboss=emboss;
    }

    /**
     * sets the stroke to blur 
     * @param blur true-false
     */
    private void setBlur(boolean blur){
        this.blur=blur;
    }

    /**
     * sets the stroke width of the current path
     * @param strokeWidth integer value specifies the stroke width of the current path
     */
    private void setStrokeWidth(int strokeWidth){
        this.strokeWidth=strokeWidth;
    }

    /**
     * sets the current path
     * @param path the current finger path(finger motion)
     */
    private void setPath(Path path){
        this.path=path;
    }

    /**
     * gets the current fingerPath color
     * @return the current color
     */
    public int getColor() {
        return color;
    }

    /**
     * gets the current stroke width of the path
     * @return
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * gets the current path
     * @return the current path
     */
    public Path getPath() {
        return path;
    }

    /**
     * checks the current emboss
     * @return true if its activated else false if not
     */
    public boolean isEmboss(){
        return emboss;
    }

    /**
     * checks the current blur state of the stroke 
     * @return true if activated otherwise false if not
     */
    public boolean isBlur() {
        return blur;
    }
}
