package de.itech.spaceshooter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;

public class Asteroid extends GameObject{	
	
	private double range = 1200;    
    private double turningVelocity = 0.03;
    private double asteroidRotationAngle = 0;        
    private Image astImage;
    
    public Asteroid (Coordinate position, double height, double width, double movingAngle, double movingDistance) {
        super(position, width, height);        
        setMovingAngle(movingAngle);
        setMovingDistance(movingDistance);        
        URL imageURL = getClass().getResource("images/asteroid/asteroid_transp.png");        
        try {
        	astImage = ImageIO.read(imageURL);
        } catch (Exception e)
        {
        	System.out.println(e.getMessage());
        }
    }

    public double getRange() { return range; }

    public void setRange(int range) { this.range = range; }
    
    public Coordinate getPosition() { return super.getObjectPosition(); }    	   
    
    @Override
    public void makeMove() {
    	asteroidRotationAngle = asteroidRotationAngle + turningVelocity;
        if (asteroidRotationAngle < 0) { asteroidRotationAngle = asteroidRotationAngle + 2 * Math.PI; }
        if (asteroidRotationAngle > 2 * Math.PI) { asteroidRotationAngle = asteroidRotationAngle - 2 * Math.PI; }        
    	if (range > 0) super.makeMove();
        range = range - super.getMovingDistance();        
    }
    
    @Override
    public void paintMe(java.awt.Graphics g) { 
    	Graphics2D g2d2 = (Graphics2D) g;    	
        g2d2.drawImage(astImage, (int)getObjectPosition().getX(), (int)getObjectPosition().getY(), null);        
    }
}
