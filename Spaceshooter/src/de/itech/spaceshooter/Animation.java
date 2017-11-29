package de.itech.spaceshooter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Animation {

    private BufferedImage animImage;
    private int frameWidth;
    private int frameHeight;
    private int numberOfFrames;
    private long frameTime;
    private long startingFrameTime;
    private long timeForNextFrame;
    private int currentFrameNumber;
    private boolean loop;

    /** x and y coordinates of the animation drawn on the screen */
    public int x;
    public int y;

    private int startingXOfFrameInImage;
    private int endingXOfFrameInImage;

    /** State of animation (active/finished, animation deleted when finished)*/
    public boolean active;
    
    // In milliseconds. Waiting time before starting animation/displaying it
    private long showDelay;
    
    // At what time was animation created
    private long timeOfAnimationCration;


    /**
     * Creates animation.
     * 
     * @param animImage Image of animation
     * @param frameWidth Width of animation image "animImage"
     * @param frameHeight Height of animation image "animImage" 
     * @param numberOfFrames Number of frames in the animation image
     * @param frameTime Redraw rate in milliseconds
     * @param loop Repeat in loop?
     * @param x x coordinate of drawing 
     * @param y y coordinate of drawing
     * @param showDelay Delay before starting the animation in milliseconds 
     */
    public Animation(BufferedImage animImage, int frameWidth, int frameHeight, int numberOfFrames, long frameTime, boolean loop, int x, int y, long showDelay)
    {
        this.animImage = animImage;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.numberOfFrames = numberOfFrames;
        this.frameTime = frameTime;
        this.loop = loop;
        this.x = x;
        this.y = y;     
        this.showDelay = showDelay;
        
        timeOfAnimationCration = System.currentTimeMillis();
        startingXOfFrameInImage = 0;
        endingXOfFrameInImage = frameWidth;
        startingFrameTime = System.currentTimeMillis() + showDelay;
        timeForNextFrame = startingFrameTime + this.frameTime;
        currentFrameNumber = 0;
        active = true;
    }

    /**
     * Changes the coordinates of the animation.
     * 
     * @param x x coordinate of the drawing
     * @param y y coordinate of the drawing
     */
    public void changeCoordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks time to show next frame of the animation.
     * It also checks if the animation is finished.
     */
    private void Update()
    {
        if(timeForNextFrame <= System.currentTimeMillis())
        {
            currentFrameNumber++;
            
            if(currentFrameNumber >= numberOfFrames)
            {
                currentFrameNumber = 0;
                if(!loop)
                    active = false;
            }
            startingXOfFrameInImage = currentFrameNumber * frameWidth;
            endingXOfFrameInImage = startingXOfFrameInImage + frameWidth;
            startingFrameTime = System.currentTimeMillis();
            timeForNextFrame = startingFrameTime + frameTime;
        }
    }

    /**
     * Draws current frame of the animation.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        this.Update();
        
        if(this.timeOfAnimationCration + this.showDelay <= System.currentTimeMillis())
            g2d.drawImage(animImage, x, y, x + frameWidth, y + frameHeight, startingXOfFrameInImage, 0, endingXOfFrameInImage, frameHeight, null);
    }
}