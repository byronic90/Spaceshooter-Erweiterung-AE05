package de.itech.spaceshooter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

public class Missile extends GameObject {    
    
    private int range = 150;    
    private Color missileColor;
    
    
    public Missile (Coordinate position, double size, double movingAngle, double movingDistance, Color missileColor) {
        super(position, size, size/3);        
        setMovingAngle(movingAngle);
        setMovingDistance(movingDistance);
        this.missileColor = missileColor;
    }    
    public int getRange() { return range; }
    public void setRange(int range) { this.range = range; }
    public void setColor(int color) { 
    	switch(color){    
		    case 1: this.missileColor = Color.GREEN;
					break;
			case 2: this.missileColor = Color.RED;
					break;
			case 3: this.missileColor = Color.BLUE;
					break;
			case 4: this.missileColor = Color.WHITE;
					break;
    	}    	
    }
    
    @Override
    public void makeMove() {
        if (range > 0) super.makeMove();
        range--;
    }
    
    @Override
    public void paintMe(java.awt.Graphics g) {                
        Graphics2D g2d = (Graphics2D) g;   
        g2d.setColor(missileColor);                
        AffineTransform transform = new AffineTransform();        
        RoundRectangle2D missileShape = new RoundRectangle2D.Double(getObjectPosition().getX(), 
                                                                    getObjectPosition().getY(), 
                                                                    getWidth(), getHeight(), 3, 3);         
        transform.rotate(getMovingAngle(),missileShape.getCenterX(), missileShape.getCenterY());
        Shape transformedMissileShape = transform.createTransformedShape(missileShape);        
        g2d.fill(transformedMissileShape);        
    }
    
}
