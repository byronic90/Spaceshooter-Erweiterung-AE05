package de.itech.spaceshooter;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow extends JFrame{
     
	private final GamePanel2 shipGamePanel;
    
    public GameWindow() {                           
    	this.shipGamePanel = new GamePanel2();                
        registerWindowListener();
        createMenu();          
        add(shipGamePanel);
        pack();        
        setTitle("Space-Shooter");
        setLocation(10, 10);
        setResizable(false);        
        setVisible(true);
    }
    
    private void registerWindowListener() {        
        addWindowListener(new WindowAdapter() {            
            @Override
            public void windowClosing(WindowEvent e) { 
                System.exit(0); 
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
                // Game pauses            	
                shipGamePanel.pauseGame();
            }
            @Override
            public void windowActivated(WindowEvent e) {
                // Game continues
                shipGamePanel.continueGame();
            }            
        });        
    }
    
    private void createMenu() {        
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);        
        JMenu fileMenu = new JMenu("File");
        JMenu gameMenu = new JMenu("Game");        
        menuBar.add(fileMenu);        
        menuBar.add(gameMenu);                
        addFileMenuItems(fileMenu);  
        addGameMenuItems(gameMenu);        
    }
    
    private void addFileMenuItems(JMenu fileMenu) {        
        JMenuItem quitItem = new JMenuItem("Quit");
        fileMenu.add(quitItem);
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }); 
    }
    
    private void addGameMenuItems(JMenu gameMenu) {                
        JMenuItem restartItem = new JMenuItem("Restart");
        gameMenu.add(restartItem);       
        restartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                shipGamePanel.restartGame();
            }
        });
        
        //>sibr
        gameMenu.addSeparator();
        JMenuItem scoreItem = new JMenuItem("Scoreboard");
        gameMenu.add(scoreItem);       
        scoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                shipGamePanel.setGameState(1);                
            }
        });
        
        gameMenu.addSeparator();
        JMenuItem mainMenuItem = new JMenuItem("Show Menu");
        gameMenu.add(mainMenuItem);
        mainMenuItem.addActionListener(new ActionListener() {
        	
        	@Override
            public void actionPerformed(ActionEvent e) {                
                shipGamePanel.setGameOver(false);
                shipGamePanel.setGameState(4);
            }
        });
        
        //<sibr
    }       
}
