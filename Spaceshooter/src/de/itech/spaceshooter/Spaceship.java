package de.itech.spaceshooter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;

public class Spaceship extends GameObject{    
    
    public static final double DRIVING_VELOCIY = 15.00;
    public static final int AMMO_LOADING_TIME = 0;    
    private double drivingVelocity = DRIVING_VELOCIY;
    private double angleCannon = 0;    
    private boolean ableToShoot = true;
    private int loadingTime = AMMO_LOADING_TIME;
    private int ammoLoadingTimer = AMMO_LOADING_TIME;
    private int energy = 10;
    private int energyStart = 10;
    private boolean paintShipStatusBar = true;
    private boolean isVisible = true;
    private Image shipImage;    
        
    public Spaceship(Coordinate position, double width, double height, double movingAngle, double movingDistance) {
        super(position, width, height);        
        setMovingAngle(movingAngle);
        setMovingDistance(movingDistance);               
        URL imageURL = getClass().getResource("images/ship_blue.png");        
        try {
        	shipImage = ImageIO.read(imageURL);
        } catch (Exception e)
        {
        	System.out.println("no ship");
        }
    }          

    public double getDrivingVelocity() { return drivingVelocity; }
    public void setDrivingVelocity(double drivingSpeed) { this.drivingVelocity = drivingSpeed; }            
    public double getAngleCannon() { return angleCannon; }    
    public void setAngleCannon(double angle) { this.angleCannon = angle; }    
    public boolean isAbleToShoot() { return ableToShoot; }
    public int getLoadingTime() { return loadingTime; }
    public void setLoadingTime(int loadingTime) { this.loadingTime = loadingTime; }      
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getEnergyStart() { return energyStart; }
    public void setEnergyStart(int energyStart) { this.energyStart = energyStart; }
    public boolean isPaintShipStatusBar() { return paintShipStatusBar; }
    public void setPaintShipStatusBar(boolean paintShipStatusBar) { this.paintShipStatusBar = paintShipStatusBar; }
    public void accelerateShip() { setMovingDistance(drivingVelocity); }    
    public void decelerateShip() { setMovingDistance(-drivingVelocity); }    
    public void stopShip() { setMovingDistance(0); }  
    public void setVisible(boolean visible) { this.isVisible = visible; }
    public void setAbleToShoot(boolean ableToShoot) {
        this.ableToShoot = ableToShoot;
        ammoLoadingTimer = loadingTime;
    }

    @Override
    public void makeMove() {        
        if (ammoLoadingTimer > 0) {
            ammoLoadingTimer = ammoLoadingTimer - 1;
        }
        else {
            setAbleToShoot(true);
        }
        super.makeMove();
    }  

    public Missile shoot() {        
        double shipCenterX = getObjectPosition().getX() + getWidth()*0.5;
        double shipCenterY = getObjectPosition().getY() + getHeight()*0.5;
        double cannonLength = getWidth()*0.8;
        double missileSize = getWidth()*0.12;
        double missileAngle = 0;
        Coordinate missileDirection = GameObject.polarToCartesianCoordinates(missileAngle);
        double cannonEndX = missileDirection.getX() * cannonLength;
        double cannonEndY = missileDirection.getY() * cannonLength;
        Coordinate missileStartPosition = new Coordinate(shipCenterX + cannonEndX - missileSize/2, 
                                                         shipCenterY + cannonEndY - missileSize/6);           
        Missile missile = new Missile(missileStartPosition, missileSize, missileAngle, 15);
        setAbleToShoot(false);      
        return missile;        
    }
    
    @Override
    public void paintMe(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g; 
        paintShip(g2d);
    }

    private void paintShip(Graphics2D g2d) {        
        if (isVisible) {
        	Graphics2D g2d2 = (Graphics2D) g2d;    	
        	g2d2.drawImage(shipImage, (int)getObjectPosition().getX(), (int)getObjectPosition().getY(), null);
        }        
    }
}
