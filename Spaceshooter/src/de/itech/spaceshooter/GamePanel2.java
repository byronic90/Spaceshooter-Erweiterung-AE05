/**
 * Spaceshooter-Game, entstanden in AE-Workshop Projekt der IT5e und 
 * erweitert im Projekt 05.  
 *
 * @author Simeon Brkitsch, Yannik Wunderlich, Turag Nikandam, Malte Heinsohn
 * @version 2.0
 */

package de.itech.spaceshooter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static java.awt.event.KeyEvent.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class GamePanel2 extends JPanel{
    
	//Init-Area for variables of the class
    public static final String IMAGE_DIR = "images/";
    private final Dimension prefSize = new Dimension(800, 600);    
    private boolean gameOver = false;        
    private Timer t;
    private Spaceship playersShip = null;        
    private List<Missile> missiles;      
    private ArrayList<Asteroid> testAsts;           
    private ArrayList<Animation> explosionsList;
    private BufferedImage explosionAnimImg;
    private Random random;
    private int tickz;
    private int score;
    private int asteroidSpawnTime = 60;
    private int asteroidSpeed = 5;  
    private int spawnResetTime = asteroidSpawnTime;    
    private int gameState = 0; //0 - InitState, 1 - Scoreboard, 2 - Alive and Gaming, 3 - Input, 4 - Menu, 5 - Settings
    private int textPosX = 50;
    private int textPosY;
    private int resetY;
    private int snipTextPos = 0;
    private String[] introText; 
    private Image background;
    private ArrayList<String> playerString = new ArrayList<String>();
    private ArrayList<String> scoreString = new ArrayList<String>();
    JTextField inputText;
	JButton saveButton;
	private String[] menuText;
	private String[] settingsText;
	private String defaultFont = "Agency FB";
	private int menuSelectionPosY = 250;
	private int menuSelectedItem = 1; //1 - START, 2 - SETTINGS, 3 - SCOREBOARD, 4 - EXIT
	boolean showSelection;
	boolean showTitleBlink;
	int blinkState;
	private Spaceship shipModel;
	private Missile missileModel;
    	
    public GamePanel2() {        
        setFocusable(true);
        setPreferredSize(prefSize);
        URL imageURL = getClass().getResource("images/background/background.jpg");        
        try { background = ImageIO.read(imageURL); } 
        catch (Exception e) { System.out.println(e.getMessage()); }                
        init();           
        showMenu();        
    }

    public Spaceship getPlayersShip() { return playersShip; }   
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    //Sets the GameState(0 - InitState, 1 - Scoreboard, 2 - Alive and Gaming, 3 - Input, 4 - Men�)
    public void setGameState(int gameState) {
    	this.gameState = gameState;
    	if (gameState == 3) {
    		inputText = new JTextField();
    		saveButton = new JButton("SAVE");
    		inputText.setBounds(300, 300, 200, 50);
    		inputText.setBackground(Color.black);
    		inputText.setFont(new Font(defaultFont, Font.BOLD, 25));
    		inputText.setForeground(new Color(2, 98, 0));
    		saveButton.setBounds(530, 300, 150, 50);
    		saveButton.setBackground(Color.DARK_GRAY);
    		saveButton.setFont(new Font(defaultFont, Font.BOLD, 25));
    		saveButton.setForeground(new Color(2, 120, 0));
    		saveButton.addActionListener( new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent e){ 
    				Connection.setPlayer(inputText.getText(), score + "");
    				switchToScoreboard(); 
    			} 
    		});    		
    		inputText.addKeyListener(new KeyAdapter() {
    			public void keyTyped(KeyEvent e) {
    				char c = e.getKeyChar();
    				String preventSigns = "^!\"�$%&/()=?`�*+#~'-_.:,;<>|\\{[]} ";
    			    if (preventSigns.indexOf(c) != -1) {
    			    	e.consume();  // ignore event
    			    }
    			}
    		});
    		this.add(inputText);
    		this.add(saveButton);
    	} else {
    		setGameOver(false);
    		if (inputText != null && saveButton != null) {
    	    	inputText.setVisible(false);
    	    	saveButton.setVisible(false);
        	}
    	}
    }    
        
    public void switchToScoreboard() {
    	topTen();
    	this.gameState = 1;
    	if (inputText != null && saveButton != null) {
	    	inputText.setVisible(false);
	    	saveButton.setVisible(false);
    	}
    }    
    
    //Creates all game objects, registers keylisteners and starts the Timer for the main loop
    private void init() {
    	createGameObjects();
    	
    	addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
            	if (gameState == 2) { 
	                switch(e.getKeyCode()) {
	                    case VK_SPACE: 
	                        if (playersShip.isAbleToShoot()) { missiles.add(playersShip.shoot()); }                            
	                        break;	                        
	                    case VK_DOWN:                         
	                    case VK_UP: playersShip.stopShip(); break;                                           
	                }
            	}
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
            	if (gameState == 0) { startGame();} 
            	else if (gameState == 2){
	            	switch(e.getKeyCode()) {                                                            
	                    case VK_UP: playersShip.accelerateShip(); break;
	                    case VK_DOWN: playersShip.decelerateShip(); break;   
	                }
                } else if (gameState == 1) { showMenu(); } 
                else if (gameState == 3) { if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                		Connection.setPlayer(inputText.getText(), score + "");
                		switchToScoreboard();                		
                	}
                }
                else if (gameState == 4) { 
                	switch(e.getKeyCode()) {
                		case VK_ENTER: processMenuSelection(0); break;
                		case VK_UP: switchSelection(-1); break;
                		case VK_DOWN: switchSelection(1); break;
                	}; 
                }
                else if (gameState == 5) { 
                	switch(e.getKeyCode()) {
                		case VK_ENTER: processMenuSelection(1); break;
                		case VK_UP: switchSelection(-1); break;
                		case VK_DOWN: switchSelection(1); break;
                		case VK_LEFT: 	switchSkin(-1); break;                					  	
                		case VK_RIGHT: switchSkin(1); break;
                	}; 
                }
            }
        });
                                                                  
        t = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == 2) {
                    doOnTick();                    
                    score++;             
                }
                else if (gameState == 0) {                                    
                    repaint();
                    textPosY -= 1;
                    resetY -= 1;
                    if (textPosY < -200) { startGame(); }
                }
                else { repaint(); }
                tickz++;
            }
        });                   
    }
    
    private void intro() {
        introText = new String[]{   "             Willkommen zu Ihrem Briefing K�ptain!",
        							"",
	                                "        Die Konf�deration von Terra ben�tigt Ihr Schiff,",
	                                "               Ihre Mannschaft und Ihr K�nnen!",
	                                "",
	                                "      Nur Ihnen k�nnen wir diese f�r unsere �u�eren Kolonien",
	                                "             �berlebenswichtige Fracht anvertrauen.",
	                                "",
	                                "      Wir wissen um Ihre erfolge in den Galaktischen Kriegen,",
	                                "        Ihre Loyalit�t zu Terra und Ihre unvergleichlichen",
	                                "                   F�higkeiten als Pilot.",
	                                "",
	                                "  Wir gehen davon aus, dass Sie den Auftrag ausf�hren werden.",
	                                "",
	                                " Jedoch lauern auf dem Weg in die �u�eren Ausl�ufer der Galaxie",
	                                "                      viele Gefahren.",
	                                "",
	                                "      Unter Anderem m�ssen Sie das  Raumschiff durch einen",
	                                "                 Asteroideng�rtel steuern."};
        textPosY = 600;
        resetY = 400;
        snipTextPos = 0;
        playersShip.setVisible(false);
        setGameState(0);                               
    }
    
    private void switchSelection(int value) {    	
    	menuSelectedItem += value;
    	if (menuSelectedItem > 4) { menuSelectedItem = 1; }
    	if (menuSelectedItem < 1) { menuSelectedItem = 4; }
    }
    private void switchSkin(int value) {    	
    	int currentModel;
    	switch(menuSelectedItem) {
    		case 1: currentModel = shipModel.getShipModel();
    				currentModel += value;
		    		if (currentModel > 4) { currentModel = 1; }
		        	if (currentModel < 1) { currentModel = 4; }
    				shipModel.setShipModel(currentModel);
    				break;
    		case 2: currentModel = playersShip.getMissileColorInt();
    				currentModel += value;
    				if (currentModel > 4) { currentModel = 1; }
		        	if (currentModel < 1) { currentModel = 4; }
		        	missileModel.setColor(currentModel);
		        	playersShip.setMissileColor(currentModel);
		        	break;
    	}
    }
    private void processMenuSelection(int menuInt) {
    	if (menuInt == 0) {
	    	switch(menuSelectedItem) {
	    		case 1: restartGame();
	    				break;
	    		case 2: showSettings();
	    				break;
	    		case 3: switchToScoreboard();
	    				break;
	    		case 4: System.exit(0);
	    				break;
	    	}
    	} else {
    		switch(menuSelectedItem) {
	    		case 1: 
	    				break;
	    		case 2: 
	    				break;
	    		case 3: shipModel.setVisible(false);
						playersShip.setShipModel(shipModel.getShipModel());	    				
						showMenu();
	    				break;
    		}
    	}    	    	
    }
    private void showMenu() {
    	menuText = new String[]{
    								"       SPACESHOOTER",
    								"                             START",
    								"                           SETTINGS",
    								"                        SCOREBOARD",
    								"                              EXIT"    								
                					};
    	gameState = 4;
    	setGameOver(false);
    	menuSelectedItem = 1;
    }
    
    private void showSettings() {
    	settingsText = new String[]{
				"       SPACESHOOTER",
				"                          SHIPSKINS",
				"                       MISSILECOLOR",
				//"                       ASTEROIDSKINS",
				"                              BACK"    								
				};
    	gameState = 5;    	
    	menuSelectedItem = 1;
    	if (shipModel != null) { shipModel.setVisible(true); }
    }
    
    private void addToTopTen(ArrayList<String[]> playerList) {
    	for (String[] player : playerList) {
    		playerString.add(player[0]);
    		scoreString.add(player[1]);
		}    	
    }
    private void topTen() {    						      
    	playerString.clear();
    	scoreString.clear();    	    
    	addToTopTen(Connection.getInputAsHashmap((Connection.getTopTen("", ""))));    	
    }
    
    private void createGameObjects() {
        if (playersShip == null) { playersShip = new Spaceship(new Coordinate(600,150), 70, 45, Math.toRadians(180), 0); }
        initPlayersShip();        
        missiles = new LinkedList<>();                    
        random = new Random();        
        testAsts = new ArrayList<Asteroid>();        
        explosionsList = new ArrayList<Animation>();
        String imagePath = "images/explosion/explosion_anim.png";
        URL imageURL = getClass().getResource(imagePath);        
        try { explosionAnimImg = ImageIO.read(imageURL); } 
        catch (Exception e) { System.out.println("NO EXPL"); }
    }
    
    private void initPlayersShip() {        
      playersShip.setObjectPosition(new Coordinate(0, 300));
      playersShip.setMovingAngle(Math.toRadians(270));
      playersShip.setAngleCannon(360);
      playersShip.setEnergy(10);
    }
    
    private void updateExplosions() {
        for(int i = 0; i < explosionsList.size(); i++) {
            if(!explosionsList.get(i).active) explosionsList.remove(i);
        }
    }
    
    private void startGame() {                
        t.start();
        gameState = 2;
        score = 0; 
    	tickz = 0;
    	asteroidSpawnTime = 100;
        asteroidSpeed = 5; 
        setGameOver(false);
        playersShip.setVisible(true);
    }
    
    public void pauseGame() { t.stop(); }        
    public void continueGame() {  if (!isGameOver()) t.start(); }
   
    public void restartGame() {    	
    	score = 0; 
    	tickz = 0;
    	asteroidSpawnTime = 100;
        asteroidSpeed = 5;    	
        setGameOver(false);
        createGameObjects();
        playersShip.setVisible(true);
        intro();
        t.start();
    }
    
    public void endGame() {         
    	setGameOver(true);        
        setGameState(3);
    }       

	private void spawnAsteroidIfReady() {
		if (spawnResetTime < 1) {
			testAsts.add(new Asteroid(new Coordinate(900,random.nextInt(500) + 50), 50, 50, Math.toRadians(180),asteroidSpeed));
			spawnResetTime = asteroidSpawnTime / 2;
		} else { spawnResetTime = spawnResetTime - 1; }

		if (tickz % 250 == 0 && tickz != 0) { asteroidSpeed = asteroidSpeed + 2; }

		if (tickz % 300 == 0 && tickz != 0) {
			if (asteroidSpawnTime > 5) { asteroidSpawnTime = asteroidSpawnTime - 5; } 
			else { asteroidSpawnTime = 1; }
		}
	}
    
    private void doOnTick() {                
        if (gameState == 2) {
            if (playersShip.getEnergy() <= 0 && explosionsList.isEmpty()) { endGame(); }

            //Loop for collision handling
            for (Iterator<Missile> itMissiles = missiles.iterator(); itMissiles.hasNext();) {
                Missile missile = itMissiles.next();
                missile.makeMove();
                if (missile.getRange() <= 0) { itMissiles.remove(); }

                //Asteroid <> Missle collision/explosion handling
                if (!testAsts.isEmpty()) {
                    for (int i = 0; i < testAsts.size(); i++) {
                        Asteroid collAst = testAsts.get(i);
                        if (collAst.touches(missile) && missile.getRange() > 1) {
                            Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, (int) collAst.getObjectPosition().getX(), (int) collAst.getObjectPosition().getY() - explosionAnimImg.getHeight() / 3, 0);
                            explosionsList.add(expAnim);
                            itMissiles.remove();
                            testAsts.remove(i);
                            score += 1000;
                        }
                    }
                }
            }
            //Asteroid <> Playership collision/explosion handling
            if (!testAsts.isEmpty()) {
                for (int i = 0; i < testAsts.size(); i++) {
                    Asteroid collAst = testAsts.get(i);
                    if (playersShip.touches(collAst) && playersShip.getEnergy() != 0) {
                        for (int exNum = 0; exNum < 3; exNum++) {
                            Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, (int) playersShip.getObjectPosition().getX() + exNum * 60, (int) playersShip.getObjectPosition().getY() - random.nextInt(100), exNum * 200 + random.nextInt(100));
                            explosionsList.add(expAnim);
                        }
                        for (int exNum = 0; exNum < 3; exNum++) {
                            Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, (int) collAst.getObjectPosition().getX() + exNum * 60, (int) collAst.getObjectPosition().getY() - random.nextInt(100), exNum * 200 + random.nextInt(100));
                            explosionsList.add(expAnim);
                        }
                        testAsts.remove(i);
                        playersShip.setEnergy(0);
                        playersShip.setVisible(false);
                        break;
                    }
                }
            }
            for (int i=0; i < testAsts.size(); i++) {
            	testAsts.get(i);
            	if (testAsts.get(i).getObjectPosition().getX() < -50) {
            		testAsts.remove(testAsts.get(i));            		
            	} else { testAsts.get(i).makeMove(); }            	
            }            
            updateExplosions();            
            spawnAsteroidIfReady();            
            playersShip.makeMove();
        }
        repaint();        
    }
    
    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent(g);        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);                                  
        Graphics2D g2d2 = (Graphics2D) g;    	
        g2d2.drawImage(background, 0, 0, null);
        //Paint all Components while gaming
        if (gameState == 2) {                    
            g.setFont(new Font(defaultFont, Font.BOLD, 19));            
            g.setColor(Color.GREEN);
            g.drawString("Score: " + score, 22 , prefSize.height - 22);                    

            playersShip.paintMe(g);  
            for (Missile missile:missiles) { missile.paintMe(g); }
            for(int i = 0; i < explosionsList.size(); i++){ explosionsList.get(i).Draw(g2d); }
            for (Asteroid ast : testAsts) { ast.paintMe(g); }
        }
        
        //Intro text
        if (gameState == 0) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 19));            
            g.setColor(Color.RED);
            g.drawString("Press any key to start", 300, 40);            
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 19));            
            g.setColor(Color.WHITE);
            
            int dY = 0;
            for (int i = 0; i < introText.length; i++) {
                if (resetY == 0) {
                    introText[snipTextPos] = "";
                    if (introText.length-1 > snipTextPos) { snipTextPos +=1; }
                    resetY = 20;
                }
                g.drawString(introText[i], textPosX, textPosY + dY);
                dY += 20;                
            }
        }     
                
        //Scoreboard state
        if (gameState == 1) {                        
            g.setFont(new Font("OCR A Extended", Font.BOLD, 30));            
            g.setColor(new Color(80, 200, 80));                                                                                                                        
            g.drawString("SCOREBOARD", textPosX + 300, 50);
            
            g.setColor(new Color(2, 98, 0));
            g.setFont(new Font("OCR A Extended", Font.BOLD, 26));
            int dYTest = 60;
            for (int i = 0; i < playerString.size(); i++) {
            	g.drawString((i+1)+".", textPosX + 110, 50 + dYTest);
            	g.drawString(playerString.get(i), textPosX + 170, 50 + dYTest);
	            g.drawString(scoreString.get(i), textPosX + 480, 50 + dYTest);
	            dYTest += 30;
            }            
        }
        //Game-Over Input state
        if (gameState == 3) {                        
            g.setFont(new Font("OCR A Extended", 0, 30));            
            g.setColor(new Color(80, 222, 80));                                                                     
            g.drawString("NICE TRY, YOU GOT " + score + " POINTS", textPosX + 130, 200);            
            int dYTest = 50;
            g.drawString("PLEASE ENTER YOUR NAME...", textPosX + 220, 200 + dYTest);                                              
        }
        //Menu-state
        if (gameState == 4) {                                	
        	g.setFont(new Font("OCR A Extended", 0, 35));            
            g.setColor(new Color(100, 254, 100));                 
            g.drawString(menuText[0], textPosX + 110, 100);
                        
            int dYTest = 50;            
            for (int i = 1; i < menuText.length; i++) {            	
            	g.setFont(new Font(defaultFont, 0, 25));
            	g.setColor(new Color(3, 132, 0));
            	//Selection is drawn differently
            	if (i == menuSelectedItem) { 
            		menuSelectionPosY = 250+i*50; 
            		g.setColor(new Color(50, 220, 50));
            		g.setFont(new Font(defaultFont, 0, 27));
            	} 	            	            	            	           
            	g.drawString(menuText[i], textPosX + 190, 250 + dYTest);	            
	            dYTest += 50;
            }   
            //Selection blinking
            if (tickz%5==0) { showSelection = !showSelection; }
            if (showSelection) {
            	g.setFont(new Font(defaultFont, Font.BOLD, 35));
            	g.setColor(new Color(50, 220, 50));
            	g.drawString(">", textPosX + 250, menuSelectionPosY);
            	g.drawString("<", textPosX + 450, menuSelectionPosY);
            }
            //Title Effects
            g.setFont(new Font(defaultFont, 0, 70));
        	g.setColor(new Color(50, 190, 50));           
            if (tickz%20==0) { 
            	blinkState += 1;
            	if (blinkState > 3) { blinkState = 0; }
            }
	            switch (blinkState) {
	            	case 1: g.drawString("      <                       >", textPosX + 110, 110);
	            			break;
	            	case 2: g.drawString("    < <                       > >", textPosX + 100, 110);
	            			break;
	            	case 3: g.drawString("  < < <                       > > >", textPosX + 90, 110);
	            			break;
	            	case 0: break;
	            }            
        }
        //Settings State
        if (gameState == 5) {          	
        	int dYTest = 80;  
            int deltaY = 90;
            int startY = 190;
        	if (shipModel == null) { 
        		shipModel = new Spaceship(new Coordinate(370,startY+90), 70, 45, Math.toRadians(180), 0);
        		shipModel.setShipModel(playersShip.getShipModel());        		
        	}    
        	if (missileModel == null) {        		                
                double missileSize = playersShip.getWidth()*0.2;
                double missileAngle = 0;                               
                Coordinate missileStartPosition = new Coordinate(400, startY+200);                                                                 
        		missileModel = new Missile(missileStartPosition, missileSize, missileAngle, 15, playersShip.getMissileColor());
        	}
        	shipModel.paintMe(g);
        	missileModel.paintMe(g);
        	g.setFont(new Font("OCR A Extended", 0, 35));            
            g.setColor(new Color(100, 254, 100));                 
            g.drawString(settingsText[0], textPosX + 110, 100);
                                    
            for (int i = 1; i < settingsText.length; i++) {            	
            	g.setFont(new Font(defaultFont, 0, 25));
            	g.setColor(new Color(3, 132, 0));
            	//Selection is drawn differently
            	if (i == menuSelectedItem) { 
            		menuSelectionPosY = startY+i*deltaY+30; 
            		g.setColor(new Color(50, 220, 50));
            		g.setFont(new Font(defaultFont, 0, 27));
            	} 	            	            	            	           
            	g.drawString(settingsText[i], textPosX + 190, startY + dYTest);	            
	            dYTest += deltaY;
            }   
            //Selection blinking
            if (tickz%5==0) { showSelection = !showSelection; }
            if (showSelection && menuSelectedItem != 3) {
            	g.setFont(new Font(defaultFont, Font.BOLD, 35));
            	g.setColor(new Color(50, 220, 50));
            	g.drawString("<", textPosX + 250, menuSelectionPosY);
            	g.drawString(">", textPosX + 450, menuSelectionPosY);
            }
            //Title Effects
            g.setFont(new Font(defaultFont, 0, 70));
        	g.setColor(new Color(50, 190, 50));           
            if (tickz%20==0) { 
            	blinkState += 1;
            	if (blinkState > 3) { blinkState = 0; }
            }
	            switch (blinkState) {
	            	case 1: g.drawString("      <                       >", textPosX + 110, 110);
	            			break;
	            	case 2: g.drawString("    < <                       > >", textPosX + 100, 110);
	            			break;
	            	case 3: g.drawString("  < < <                       > > >", textPosX + 90, 110);
	            			break;
	            	case 0: break;
	            }            
        }
        
        //Gameover text
        if (isGameOver() && gameState != 1) {
            g.setFont(new Font(defaultFont, Font.BOLD, 50));
            g.setColor(Color.RED);
            g.drawString("GAME OVER!", prefSize.width/2 - 100, prefSize.height/5);
        }            
    }       
}
