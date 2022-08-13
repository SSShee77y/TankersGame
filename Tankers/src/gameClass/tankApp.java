package gameClass;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel; 
import javax.swing.Timer;

public class tankApp extends JPanel {
	// Fonts
	private Font font, fontMenu, fontTitle, fontMini, fontSmaller, fontBigger;
	
	// Screen Graphics
	private GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gDevice = gEnvironment.getDefaultScreenDevice();
	private static JFrame frame = new JFrame();
	private static tankApp panel;
	private static Container container;
	private static double scaleX = 1, scaleY = 1;
	private static final int WIDTH_DEFAULT = 1080;
	private static final int HEIGHT_DEFAULT = 640;
	private int WIDTH = WIDTH_DEFAULT;
	private int HEIGHT = HEIGHT_DEFAULT;
	private int wait = 4;
	private int FPS = 31;
	private int timeLast, timeDif;
	
	// Menu
	private ImageIcon arrowSelect;
	private int selectPosY = HEIGHT_DEFAULT - 305, posSelect = 1;
	private boolean tutorial, tutorialSpawned;
	private boolean debugging, setFullscreen;
	
	// For leaderboard
	private static ArrayList<String> leaderboard = new ArrayList<String>();
	private String leaderboardName = "";
	private int leaderboardScore, inputCooldown = 20;
	private boolean isHighscore, scoreEntered;
	
	// For Help Menu
	private BufferedImage[] helpImages = new BufferedImage[13];
	private String[] helpImagesURL = {
			"KeyPrompts/w_key.png",
			"KeyPrompts/a_key.png",
			"KeyPrompts/s_key.png",
			"KeyPrompts/d_key.png",
			"KeyPrompts/space_key_dark.png",
			"KeyPrompts/esc_key_dark.png",
			"KeyPrompts/enter_key_dark.png",
			"KeyPrompts/r_key_dark.png",
			"KeyPrompts/g_key_dark.png",
			"KeyPrompts/lmb_press_dark.png",
			"KeyPrompts/rmb_press_dark.png",
			};
	
	// Environment textures
	private ImageIcon backgroundImage, foregroundImage, menuBackgroundImage;
	
	// Game variables
	private int score;
	private boolean enemiesSpawned;
	private boolean invincibility;
	
	// Entities
	private Entity player;
	private ArrayList<Entity> shells = new ArrayList<Entity>();
	private ArrayList<Entity> bullets = new ArrayList<Entity>();
	private ArrayList<Entity> enemyBullets = new ArrayList<Entity>();
	private ArrayList<Entity> enemyTanks = new ArrayList<Entity>();
	private ArrayList<Entity> enemyInfantry = new ArrayList<Entity>();
	private ArrayList<Entity> smokes = new ArrayList<Entity>();
	private ArrayList<Entity> dirts = new ArrayList<Entity>();
	private ArrayList<Entity> sparks = new ArrayList<Entity>();
	private ArrayList<Entity> explosions = new ArrayList<Entity>();
	private ImageIcon placeHolder, playerHullImage, playerTurretImage, enemyHullImage, enemyTurretImage, enemyRiflemanImage, shellImage, bulletImage, crosshairImage, smokeImage, dirtImage;
	private String[] ImageURL = {
			"sword.png", //0
			"player_tank_hull.png", //1
			"player_tank_turret.png", //2
			"enemy_tank_hull.png", //3
			"enemy_tank_turret.png", //4
			"enemy_infantry_rifleman.png", //5
			"tank_shell.png", //6
			"gun_bullet.png", //7
			"crosshair.png", //8
			"smoke.png", //9
			"dirt_kickup.png", //10
			};
	private ImageIcon explodeF1, explodeF2, explodeF3, explodeF4;
	private String[] explosionsURL = {
			"explosion_frame1.png", //0
			"explosion_frame2.png", //1
			"explosion_frame3.png", //2
			"explosion_frame4.png", //3
	};
	
	// Player tank variables
	private double playerVelocity, playerRVelocity, playerRotation = -90;
	private boolean HEShell = true, APShell = false;
	private int playerBulletCD, playerBulletCDReset = 240, playerShellCD, playerShellCDReset = 200;
	private int HEShellCount = 35, APShellCount = 35, MGCount = 200, MGReserve = 600;
	private double playerHealthDefault = 6.0;
	
	// Enemy variables
	private int enemyCooldown = 300;
	
	// Mouse Inputs
	private double mouseX, mouseY, mouseRad;
	private boolean leftClick, rightClick;
	
	// Key Inputs
	private boolean rightPressed, leftPressed, upPressed, downPressed, spacePressed, reloadPressed, pause, gameOver;
	private int toggleShellCD;
	private int level = 1;
	
	// Sounds
	private Clip sound, mgFiring, engineIdleSound, engineLoadSound, menuMusic;
	private String[] soundStr = {
			"Sounds/war_thunder_menu_ost.wav", //0
			"Sounds/maingun_firing.wav", //1
			"Sounds/maingun_reloaded.wav", //2
			"Sounds/machinegun_reloaded.wav", //3
			"Sounds/m4a3_engine_idle.wav", //4
			"Sounds/m4a3_engine_load.wav", //5
			"Sounds/m4a3_engine_rev.wav", //6
			"Sounds/enemy_cannon_fire.wav", //7
			"Sounds/enemy_mg_fire.wav", //8
			"Sounds/radio_chatter_gun_reloaded_a.wav", //9
			"Sounds/radio_chatter_gun_reloaded_b.wav", //10
			"Sounds/radio_chatter_load_AP.wav", //11
			"Sounds/radio_chatter_load_frag.wav", //12
			"Sounds/radio_chatter_hit_confirmed.wav", //13
			"Sounds/radio_chatter_shell_missed.wav", //14 Unused
			"Sounds/radio_chatter_enemy_tank_destroyed.wav", //15
			"Sounds/sfx_explosion.wav", //16
			"Sounds/inside_bullet_hit.wav", //17
			"Sounds/inside_shell_hit.wav", //18
			"Sounds/machinegun_overheat.wav", //19
			"Sounds/gui_next_select.wav", //20
			"Sounds/gui_select_play.wav", //21
			"Sounds/gui_pause.wav", //22
			"Sounds/m4a3_engine_dec.wav", //23
			"Sounds/radio_chatter_player_death.wav", //24
			"Sounds/radio_chatter_area_cleared.wav", //25
			"Sounds/radio_chatter_area_sector.wav", //26
			"Sounds/radio_chatter_need_support.wav", //27
			"Sounds/sfx_plane_passby.wav", //28
			"Sounds/sfx_tank_reloaded.wav", //29
			"Sounds/sfx_tank_repair.wav", //30
			"Sounds/gui_uncheck.wav", //31 Unused
			};
	private int hitConfirmedCD, engineRevCD, engineDecCD;
	
	// Game states
	private enum STATE{MENU, PLAYMENU, GAME, LEADERBOARD, HELP};
	private STATE state = STATE.MENU;
	
	public static void main(String[] args) {
		scaleX = (WIDTH_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 160));
		scaleY = (HEIGHT_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 90));
		
		frame.setTitle("TONKERS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize((int)(WIDTH_DEFAULT*(1/scaleX)), (int)(HEIGHT_DEFAULT*(1/scaleY)));
		panel = new tankApp(Color.DARK_GRAY, (int)(WIDTH_DEFAULT*(1/scaleX)), (int)(HEIGHT_DEFAULT*(1/scaleY)));
		container = frame.getContentPane();
		container.add(panel);
		frame.setUndecorated(true); // Removes window bar
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.isAlwaysOnTop();
		
		// Set the cursor to blank
		ImageIcon cursorImg = new ImageIcon(ClassLoader.getSystemResource(""));
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg.getImage(), new Point(0, 0), "blank");
		frame.setCursor(blankCursor);
	}
	
	public tankApp(Color bk, int width, int height) {
		setBackground(bk);
		setPreferredSize(new Dimension(width, height));
		
		// [---- Start of read leaderboard file ----]
		try {
			File leaderboardFile = new File(System.getProperty("user.dir"), "tankers_leaderboard.txt");
			URL url = ClassLoader.getSystemResource("tankers_leaderboard.txt");
			Scanner input;
			if (!leaderboardFile.exists() || leaderboardFile.length() <= 1) {
				leaderboardFile.createNewFile();
				input = new Scanner(url.openStream());
			} else input = new Scanner(leaderboardFile);
			input.useDelimiter("-|\n");
			
			for(int i = 1; input.hasNext(); i++) {
				leaderboard.add(new String(input.next()));
			}
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
		// [---- End of read leaderboard file ----]
		
		// Placeholder image (sword)
		placeHolder = new ImageIcon(ClassLoader.getSystemResource(ImageURL[0]));
		
		// Menu Images
		arrowSelect = new ImageIcon(ClassLoader.getSystemResource("arrow_select_right.png"));
		
		// Help Menu Images
		for(int i = 0; i < helpImagesURL.length; i++) {
			try {
				URL url = ClassLoader.getSystemResource(helpImagesURL[i]);
				helpImages[i] = ImageIO.read(url);
			} catch (IOException e1) {System.out.println("An error occured - " + e1.getMessage());}
		}
		
		// Environment Images
		backgroundImage = new ImageIcon(ClassLoader.getSystemResource("background.png"));
		foregroundImage = new ImageIcon(ClassLoader.getSystemResource("foreground_trees.png"));
		menuBackgroundImage = new ImageIcon(ClassLoader.getSystemResource("menu_background.png"));
		
		// Entity Images
		playerHullImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[1]));
		playerTurretImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[2]));
		enemyHullImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[3]));
		enemyTurretImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[4]));
		enemyRiflemanImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[5]));
		shellImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[6]));
		bulletImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[7]));
		crosshairImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[8]));
		smokeImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[9]));
		dirtImage = new ImageIcon(ClassLoader.getSystemResource(ImageURL[10]));
		
		// Initialize explosion images
		explodeF1 = new ImageIcon(ClassLoader.getSystemResource(explosionsURL[0]));
		explodeF2 = new ImageIcon(ClassLoader.getSystemResource(explosionsURL[1]));
		explodeF3 = new ImageIcon(ClassLoader.getSystemResource(explosionsURL[2]));
		explodeF4 = new ImageIcon(ClassLoader.getSystemResource(explosionsURL[3]));
		
		// Entity Initial Spawning
		player = new Entity (0, 0, placeHolder, playerHealthDefault);
		
		/* [--- Entity Testing Spawns ---]
		if (true) {
			enemyTanks.add(new Entity(WIDTH/2, 0, enemyHullImage, 3, 0, 0, true));
			enemyTanks.add(new Entity(-50, HEIGHT+50, enemyHullImage, 3, 0.2, -0.6, false));
			enemyTanks.add(new Entity(250, HEIGHT/2, enemyHullImage, 3, 0.2, true, false));
	
			enemyInfantry.add(new Entity(WIDTH/2, HEIGHT-150, enemyRiflemanImage, 3, 0.2, true, false));
			enemyInfantry.add(new Entity(WIDTH/2+50, HEIGHT-150, enemyRiflemanImage, 3, 0.2, true, false));
			enemyInfantry.add(new Entity(WIDTH/2+100, HEIGHT-150, enemyRiflemanImage, 3, 0.2, true, false));
			enemyInfantry.add(new Entity(WIDTH/2-50, HEIGHT-150, enemyRiflemanImage, 3, 0.2, true, false));
			enemyInfantry.add(new Entity(WIDTH/2-100, HEIGHT-150, enemyRiflemanImage, 3, 0.2, true, false));
		} // [--- End of Entity Testing Spawns ---] */
		
		// Fonts
		fontMini = new Font("Bahnschrift Light", Font.BOLD, 12);
		fontSmaller = new Font("Bahnschrift Light", Font.BOLD, 16);
		font = new Font("Bahnschrift", Font.BOLD, 24);
		fontBigger = new Font("Bahnschrift", Font.BOLD, 32);
		fontMenu = new Font("Bahnschrift", Font.BOLD, 48);
		fontTitle = new Font("Bahnschrift", Font.BOLD, 116);
		
		//System.out.println("GAME LAUNCHED");
		
		// Loop sounds
		playMGSound();
		playTankIdleEngine();
		playTankLoadEngine();
		playMenuMusic();
		
		// Key and Mouse Listeners
		addKeyListener(new Key());
		addMouseListener(new Mouse());
		addMouseMotionListener(new Mouse());
		setFocusable(true);
		
		Timer timer = new Timer(1000/FPS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// FPS Checker
				timeDif = (int)System.currentTimeMillis() - timeLast;
				timeLast = (int)System.currentTimeMillis();
				
				// Get mouse location
				if (wait >= 1) wait--;
				else if (wait <= 0) {
					mouseX = MouseInfo.getPointerInfo().getLocation().getX()*scaleX-container.getLocationOnScreen().getX()*scaleX + (player.getX() - WIDTH/2);
					mouseY = MouseInfo.getPointerInfo().getLocation().getY()*scaleY-container.getLocationOnScreen().getY()*scaleY + (player.getY() - HEIGHT/2);
				}
				
				if (state == STATE.MENU) {
					if (!menuMusic.isActive()) menuMusic.start();
					mgFiring.stop();
					engineIdleSound.stop();
					engineLoadSound.stop();
				}
				
				if (gameOver == true) {
					mgFiring.stop();
					engineIdleSound.stop();
					engineLoadSound.stop();
				}
				
				if (state == STATE.GAME && gameOver == false && pause == false) {
					if (menuMusic.isActive()) menuMusic.stop();
					
					if (invincibility == true) {
						player.setLifetime(playerHealthDefault);
						HEShellCount = 35; APShellCount = 35; MGCount = 200; MGReserve = 600;
					}
					
					// Spawn enemyTanks
					if (tutorial == true && tutorialSpawned == false) {
						enemyTanks.add(new Entity(250, -250, enemyHullImage, 3, 0, 0, true));
						enemyTanks.get(enemyTanks.size()-1).setAngle(90);
						
						enemyTanks.add(new Entity(-250, -250, enemyHullImage, 3, 0, 0, true));
						enemyTanks.get(enemyTanks.size()-1).setAngle(90);
						enemyTanks.get(enemyTanks.size()-1).setTurretRotation(Math.toRadians(180));
						
						enemyTanks.add(new Entity(0, 500, enemyHullImage, 3, 0.2, -0.6, false));
						enemyTanks.add(new Entity(0, -500, enemyHullImage, 3, 0.2, -0.6, false));
						enemyTanks.get(enemyTanks.size()-1).setAngle(180);
						
						enemyTanks.add(new Entity(0, 250, enemyHullImage, 3, 0.2, true, true));
						enemyTanks.get(enemyTanks.size()-1).setAngle(-90);
						enemyTanks.get(enemyTanks.size()-1).setTurretRotation(Math.toRadians(-90));
						
						enemyInfantry.add(new Entity(190, -200, enemyRiflemanImage, 3, 0, false, true));
						enemyInfantry.add(new Entity(300, -200, enemyRiflemanImage, 3, 0, false, true));
						enemyInfantry.add(new Entity(-200, -200, enemyRiflemanImage, 3, 0, false, true));
						enemyInfantry.add(new Entity(-310, -200, enemyRiflemanImage, 3, 0, false, true));
						for (int i = 0; i < enemyInfantry.size(); i++) {
							enemyInfantry.get(i).setAngle(90);
						}
						
						enemyInfantry.add(new Entity(60, 950, enemyRiflemanImage, 3, 0, false, true));
						enemyInfantry.get(enemyInfantry.size()-1).setAngle(-90);
						enemyInfantry.add(new Entity(-50, 950, enemyRiflemanImage, 3, 0, false, true));
						enemyInfantry.get(enemyInfantry.size()-1).setAngle(-90);
						
						tutorialSpawned = true;
					}
					
					if (enemyCooldown == 0 && enemiesSpawned == false && tutorial == false) {
						int tankSpawns = 0, infSpawns = 0;
						if (level <= 8) {
							tankSpawns = 0 + (int)(level/5);
							infSpawns = 6 + level*2 - tankSpawns;
						} else if (level <= 16) {
							tankSpawns = 0 + (int)(level/4);
							infSpawns = 5 + level - tankSpawns;
						} else if (level <= 20) {
							tankSpawns = 4 + (int)((level-16)/2);
							infSpawns = 4 + (int)(level/2) - tankSpawns;
						} else if (level <= 24) {
							tankSpawns = 6 + (level-20);
							infSpawns = 0;
						} else if (level <= 28) {
							tankSpawns = 10 + (level-24)*2;
							infSpawns = 0;
						} else if (level <= 32) {
							tankSpawns = 18 + (level-28)*4;
							infSpawns = 0;
						}else if (level > 32) {
							tankSpawns = 2 + (int)(level * (level/32));
							infSpawns = 0;
						}
						for (int tankI = 0; tankI < tankSpawns; tankI++) {
							int side = (int)(Math.random()*4+1);
							int position = (int)(Math.random()*HEIGHT*(4/2)-HEIGHT*(3/4));
							int sign = 1;
							if (side == 1 || side == 3) {
								if (side == 3) sign = -1;
								enemyTanks.add(new Entity(sign*HEIGHT*2, position, enemyHullImage, 3, 0.2, true, false));
								enemyTanks.get(enemyTanks.size()-1).setAngle(90+(sign*90));
							} if (side == 2 || side == 4) {
								if (side == 4) sign = -1;
								enemyTanks.add(new Entity(position, sign*HEIGHT*2, enemyHullImage, 3, 0.2, true, false));
								enemyTanks.get(enemyTanks.size()-1).setAngle(sign*-90);
							}
						} for (int infI = 0; infI < infSpawns; infI++) {
							int side = (int)(Math.random()*4+1);
							int position = (int)(Math.random()*HEIGHT*(4/2)-HEIGHT*(3/4));
							int sign = 1;
							if (side == 1 || side == 3) {
								if (side == 3) sign = -1;
								enemyInfantry.add(new Entity(sign*HEIGHT*2, position, enemyRiflemanImage, 3, 0.2, true, false));
								enemyInfantry.get(enemyInfantry.size()-1).setAngle(90+(sign*90));
							} if (side == 2 || side == 4) {
								if (side == 4) sign = -1;
								enemyInfantry.add(new Entity(position, sign*HEIGHT*2, enemyRiflemanImage, 3, 0.2, true, false));
								enemyInfantry.get(enemyInfantry.size()-1).setAngle(sign*-90);
							}	
						} enemiesSpawned = true;
					} if (tutorial == false) {
						if (enemyTanks.size() == 0 && enemyInfantry.size() == 0 && enemiesSpawned == true) {
							level++;
							if (level % 5 == 0) enemyCooldown = 600;
							else enemyCooldown = 300;
							enemiesSpawned = false;
						} else if (enemyCooldown > 0) enemyCooldown--;
						if (level % 5 == 0) {
							if (enemyCooldown == 600 - FPS) playSound(27);
							else if (enemyCooldown == 450 - FPS) playSound(28);
							else if (enemyCooldown == 360 - FPS) playSound(30);
							else if (enemyCooldown == 290 - FPS) {
								playSound(29);
								player.modifyLifetime(1.0 + .5*(playerHealthDefault-player.getLifetime()));
								if (player.getLifetime() >= playerHealthDefault) player.setLifetime(playerHealthDefault);
								playerBulletCD = 0; playerShellCD = 0;
								HEShellCount = 35; APShellCount = 35; MGCount = 200; MGReserve = 600;
							}
						} else if (level > 1 && enemyCooldown == 300 - FPS) playSound(25);
						else if (level <= 1 && enemyCooldown == 300 - FPS) playSound(26);
					}
					
					// Player Movement
					if (upPressed == true && playerVelocity <= 6 && Math.abs(player.getX())+20 <= WIDTH_DEFAULT && Math.abs(player.getY())+20 <= WIDTH_DEFAULT){
						playerVelocity += 0.2;}
					if (downPressed == true && playerVelocity >= -2 && Math.abs(player.getX())+20 <= WIDTH_DEFAULT && Math.abs(player.getY())+20 <= WIDTH_DEFAULT) {
						playerVelocity -= 0.2;}
					if (leftPressed == true && playerRVelocity >= -2) {
						if (Math.abs(playerVelocity) >= 1 && playerRVelocity <= -1) playerRVelocity = -1;
						else playerRVelocity -= 0.5;}
					if (rightPressed == true && playerRVelocity <= 2) {
						if (Math.abs(playerVelocity) >= 1 && playerRVelocity >= 1) playerRVelocity = 1;
						else playerRVelocity += 0.5;}
					
					// Engine sound
					if (playerVelocity <= 0.2 && playerVelocity >= -0.2) {
						engineLoadSound.stop();
						engineIdleSound.start();
						engineIdleSound.loop(engineIdleSound.LOOP_CONTINUOUSLY);
						engineRevCD = 0;
						engineDecCD = 1;
					} else if (playerVelocity >= 2 && playerVelocity < 3) {
						if (upPressed == true && engineRevCD == 0) {
							playSound(6);
							engineRevCD = 1;
						} else if (engineDecCD == 0) {
							playSound(23);
							engineDecCD = 1;
						}
					} else if (playerVelocity >= 3) {
						engineIdleSound.stop();
						engineLoadSound.start();
						engineLoadSound.loop(engineLoadSound.LOOP_CONTINUOUSLY);
						engineDecCD = 0;
						engineRevCD = 1;
					}
					
					// Dirt kickup from player movement
					if (Math.abs(playerVelocity) >= 0.5 || Math.abs(playerRVelocity) >= 0.5) {
						dirtKickup(player, playerRotation);}
					
					// Player movement restrictions (Borders)
					if (player.getX()-20 < -WIDTH_DEFAULT) player.move(2, 0);
					if (player.getX()+20 > WIDTH_DEFAULT) player.move(-2, 0);
					if (player.getY()-20 < -WIDTH_DEFAULT) player.move(0, 2);
					if (player.getY()+20 > WIDTH_DEFAULT) player.move(0, -2);
	
					// Player rotation and velocity variables applied
					playerRotation += playerRVelocity;
					player.setAngle(playerRotation);
					player.move((Math.cos(player.getAngle()) * playerVelocity), (Math.sin(player.getAngle()) * playerVelocity));
					
					// All player rotation and velocity variables decrease (acceleration)
					if (playerRVelocity >= 0.25) playerRVelocity -= 0.25;
					if (playerRVelocity <= -0.25) playerRVelocity += 0.25;
					if (playerVelocity >= 0.01) playerVelocity -= 0.1;
					if (playerVelocity <= -0.01) playerVelocity += 0.1;
					
					// Player turret rotation
					turnTurret(player, mouseX, mouseY, 0.06);
					mouseRad = Math.atan2(mouseY-player.getY(), mouseX-player.getX());
					
					// Shell fired from player tank
					if (playerShellCD <= playerShellCDReset-30) hitConfirmedCD = 1;
					if (playerShellCD == 60) playSound(2);
					if (playerShellCD == 5 && invincibility == false) if (Math.random() > 0.5) playSound(9); else playSound(10);
					if (leftClick == true && playerShellCD == 0) {
						if (APShell == true) APShellCount--;
						if (HEShell == true) HEShellCount--;
						playSound(1);
						fireEntity(player, player.getTurretRotation(), HEShell, shells);
						hitConfirmedCD = 0;
						if (invincibility == false) playerShellCD = playerShellCDReset;
						else playerShellCD = 15;
					} if (playerShellCD != 0) playerShellCD--;
					// If shell is 0
					if (APShellCount == 0 && HEShellCount == 0) playerShellCD = playerShellCDReset;
					else if (APShellCount == 0) {APShell = false; HEShell = true;}
					else if (HEShellCount == 0) {HEShell = false; APShell = true;}
					// Switching shell and hit-confirmed cooldown management
					if (toggleShellCD > 0) toggleShellCD--;
					
					// Shell fired from enemy tank
					for (int j = 0; j < enemyTanks.size(); j++) {
						turnTurret(enemyTanks.get(j), player.getX(), player.getY(), 0.02);
						if (enemyTanks.get(j).getReloadTime() <= 0 && enemyTanks.get(j).getPassive() == false
								&& !(enemyTanks.get(j).getY() <= player.getY()-HEIGHT/2-50 || enemyTanks.get(j).getY() >= player.getY()+HEIGHT/2+50
								|| enemyTanks.get(j).getX() >= player.getX()+WIDTH/2+50 || enemyTanks.get(j).getX() <= player.getX()-WIDTH/2-50)) {
							playSound(7);
							fireEntity(enemyTanks.get(j), enemyTanks.get(j).getTurretRotation(), true, shells);
							enemyTanks.get(j).setReloadTime(240);
						} if (enemyTanks.get(j).getReloadTime() != 0) enemyTanks.get(j).modifyReloadTime(-1);;
					}
					
					// Bullet ammo check and/or reloading before firing
					if (MGCount == 0 && MGReserve == 0) playerBulletCD = playerBulletCDReset;
					else if ((MGCount == 0 || reloadPressed == true) && playerBulletCD == 0 && MGCount < 200) playerBulletCD = playerBulletCDReset;
					if (playerBulletCD == 40) playSound(3);
					if (playerBulletCD == 3) {
						if (MGReserve + MGCount >= 200) {MGReserve -= 200 - MGCount; MGCount += 200 - MGCount;}
						else if (MGReserve + MGCount < 200) {MGCount += MGReserve; MGReserve -= MGReserve;}
					}
					// Bullet fired from player tank
					if ((rightClick == true || spacePressed == true) && playerBulletCD == 0) {
						MGCount--;
						fireEntity(player, player.getTurretRotation(), false, bullets);
						mgFiring.start();
						mgFiring.loop(mgFiring.LOOP_CONTINUOUSLY);
						playerBulletCD = 3;
						if (MGCount == 0) {mgFiring.stop(); playSound(19);}
					} else if (playerBulletCD == 0 || playerBulletCD > 3 || MGCount == 0) mgFiring.stop();
					
					if (playerBulletCD != 0) playerBulletCD--;
					
					// Bullet fired from enemy infantry
					for (int j = 0; j < enemyInfantry.size(); j++) {
						turnTurret(enemyInfantry.get(j), player.getX(), player.getY(), 0.02);
						if (enemyInfantry.get(j).getReloadTime() <= 0 && enemyInfantry.get(j).getPassive() == false
								&& !(enemyInfantry.get(j).getY() <= player.getY()-HEIGHT/2-50 || enemyInfantry.get(j).getY() >= player.getY()+HEIGHT/2+50
								|| enemyInfantry.get(j).getX() >= player.getX()+WIDTH/2+50 || enemyInfantry.get(j).getX() <= player.getX()-WIDTH/2-50)) {
							playSound(8);
							fireEntity(enemyInfantry.get(j), enemyInfantry.get(j).getTurretRotation(), false, enemyBullets);
							enemyInfantry.get(j).setReloadTime(5);
						} if (enemyInfantry.get(j).getReloadTime() != 0) enemyInfantry.get(j).modifyReloadTime(-1);;
					}
					
					// Shell movement and removal
					projectilesMoving(shells);
					
					// Bullet movement and removal
					projectilesMoving(bullets);
					projectilesMoving(enemyBullets);
					
					// Get player hitbox
					getHitbox(player);
					
					// Get enemy hitbox
					for (int j = 0; j < enemyTanks.size(); j++) {getHitbox(enemyTanks.get(j));}
					for (int j = 0; j < enemyInfantry.size(); j++) {getHitbox(enemyInfantry.get(j));}
					
					// Enemy movement and interaction with player hitbox
					enemyMovement(enemyTanks);
					enemyMovement(enemyInfantry);
					
					// Re-Get enemy hitbox
					for (int j = 0; j < enemyTanks.size(); j++) {getHitbox(enemyTanks.get(j));}
					
					// Shell hits player detection
					for (int i = 0; i < shells.size(); i++) {
						if (intersects(player.getShape(), shells.get(i).getX(), shells.get(i).getY())) {
							if (shells.get(i).getShell() == true) {
								player.modifyLifetime(-0.3);
								playSound(16);
								explosions.add(new Entity(shells.get(i).getX(), shells.get(i).getY(), explodeF1, 8));
								getHitbox(explosions.get(explosions.size()-1));
							}
							else {player.modifyLifetime(-1.5); playSound(18);}
							shells.remove(i);
							if (player.getLifetime() <= 0) {
								playSound(24);
								gameOver = true;
								explosions.add(new Entity(player.getX(), player.getY(), explodeF1, 8));
								getHitbox(explosions.get(explosions.size()-1));
							}
						}
					}
					
					// Bullet hits player detection
					for (int i = 0; i < enemyBullets.size(); i++) {
						if (intersects(player.getShape(), enemyBullets.get(i).getX(), enemyBullets.get(i).getY())) {
							player.modifyLifetime(-.001);
							enemyBullets.remove(i);
							if (player.getLifetime() <= 0) {
								playSound(24);
								gameOver = true;
								explosions.add(new Entity(player.getX(), player.getY(), explodeF1, 8));
								getHitbox(explosions.get(explosions.size()-1));
							}
						}
					}
					
					// Shell hits enemy detection
					for (int j = 0; j < enemyTanks.size(); j++) {
						for (int i = 0; i < shells.size(); i++) {
							if (intersects(enemyTanks.get(j).getShape(), shells.get(i).getX(), shells.get(i).getY())) {
								if (shells.get(i).getShell() == true) {
									enemyTanks.get(j).modifyLifetime(-0.3);
									playSound(16);
									explosions.add(new Entity(shells.get(i).getX(), shells.get(i).getY(), explodeF1, 8));
									// Get explosion hitbox
									getHitbox(explosions.get(explosions.size()-1));
								}
								else {enemyTanks.get(j).modifyLifetime(-1.5); playSound(18);}
								shells.remove(i);
								if (enemyTanks.get(j).getLifetime() <= 0) {
									playSound(16);
									explosions.add(new Entity(enemyTanks.get(j).getX(), enemyTanks.get(j).getY(), explodeF1, 8));
									getHitbox(explosions.get(explosions.size()-1));
									enemyTanks.remove(j); score += 200;
									playSound(15);
								} else if (hitConfirmedCD == 0) {playSound(13); hitConfirmedCD = 1;}
								break;
							}
						}
					}
					
					// Bullet hits enemy detection
					for (int j = 0; j < enemyTanks.size(); j++) {
						for (int i = 0; i < bullets.size(); i++) {
							if (intersects(enemyTanks.get(j).getShape(), bullets.get(i).getX(), bullets.get(i).getY())) {
								enemyTanks.get(j).modifyLifetime(-.01);
								playSound(17);
								bullets.remove(i);
								if (enemyTanks.get(j).getLifetime() <= 0) {
									playSound(16);
									explosions.add(new Entity(enemyTanks.get(j).getX(), enemyTanks.get(j).getY(), explodeF1, 8));
									getHitbox(explosions.get(explosions.size()-1));
									enemyTanks.remove(j); score += 200;
									playSound(15);
								}
								break;
							}
						}
					}
					
					// Shell hits enemy detection
					for (int j = 0; j < enemyInfantry.size(); j++) {
						for (int i = 0; i < shells.size(); i++) {
							if (intersects(enemyInfantry.get(j).getShape(), shells.get(i).getX(), shells.get(i).getY())) {
								if (shells.get(i).getShell() == true) {
									enemyInfantry.get(j).modifyLifetime(-100);
									playSound(16);
									explosions.add(new Entity(shells.get(i).getX(), shells.get(i).getY(), explodeF1, 8));
									// Get explosion hitbox
									getHitbox(explosions.get(explosions.size()-1));
									shells.remove(i);
								} else enemyInfantry.get(j).modifyLifetime(-20);	
								if (enemyInfantry.get(j).getLifetime() <= 0) {enemyInfantry.remove(j); score += 10;}
								break;
							}
						}
					}
					
					// Explosion intersects enemy infantry
					for (int j = 0; j < enemyInfantry.size(); j++) {
						for (int k = 0; k < explosions.size(); k++) {
							if (intersects(enemyInfantry.get(j).getShape(), explosions.get(k).getShape())) {
								enemyInfantry.remove(j); score += 10;
								break;
							}
						}
					}
							
					// Bullet hits enemy detection
					for (int j = 0; j < enemyInfantry.size(); j++) {
						for (int i = 0; i < bullets.size(); i++) {
							if (intersects(enemyInfantry.get(j).getShape(), bullets.get(i).getX(), bullets.get(i).getY())) {
								enemyInfantry.get(j).modifyLifetime(-1.8);
								bullets.remove(i);
								if (enemyInfantry.get(j).getLifetime() <= 0) {enemyInfantry.remove(j); score += 10;}
								break;
							}
						}
					}
					
					// Smokes movement and removal
					particlesMoving(smokes);
					
					// Sparks
					
					
					
					// Explosions
					for (int i = 0; i < explosions.size(); i++) {
						explosions.get(i).modifyLifetime(-1);
						if (explosions.get(i).getLifetime() == 6) explosions.get(i).setImage(explodeF2);
						else if (explosions.get(i).getLifetime() == 4) explosions.get(i).setImage(explodeF3);
						else if (explosions.get(i).getLifetime() == 2) explosions.get(i).setImage(explodeF4);
						else if (explosions.get(i).getLifetime() <= 0) explosions.remove(i);
					}
					
					// Dirts movement and removal
					particlesMoving(dirts);
				}
				
				
				
				// Have everything above this repaint
				repaint();
			}});
		timer.start();
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		Color defaultColor = g2.getColor();
		Color blackTrans = new Color(20, 20, 20, 150);
		Color grayTrans = new Color(50, 50, 50, 150);
		Color playerColor = new Color(75, 140, 50);
		Color allyColor = new Color(200, 120, 65);
		Color enemyColor = new Color(190, 55, 50);
		
		g2.scale(1/scaleX, 1/scaleY);
		
		if (state == STATE.GAME) {
			g2.setStroke(new BasicStroke(5));
			g2.setColor(defaultColor);
			g2.setFont(fontSmaller);
			g2.translate(WIDTH/2 - player.getX(), HEIGHT/2 - player.getY());
			g2.drawImage(backgroundImage.getImage(), -WIDTH-800, -WIDTH-800, WIDTH*2+1600, WIDTH*2+1600, this);
			//g2.drawRect(-WIDTH, -WIDTH, WIDTH*2, WIDTH*2); //Draw borders of map
			
			// Dirt draw
			for (int i = 0; i < dirts.size(); i++) {
				g2.translate(dirts.get(i).getX(), dirts.get(i).getY());
				g2.rotate(dirts.get(i).getVelocityAngle());
				g2.drawImage(dirts.get(i).getImage().getImage(), -3, -3, 6, 6, this);
				g2.rotate(-dirts.get(i).getVelocityAngle());
				g2.translate(-dirts.get(i).getX(), -dirts.get(i).getY());
			} // End of dirt draw
			
			//* Enemy Infantry Draw
			for (int i = 0; i < enemyInfantry.size(); i++) {
				g2.setColor(enemyColor);
				// g2.fillPolygon(enemyInfantry.get(i).getShape());
				g2.translate(enemyInfantry.get(i).getX(), enemyInfantry.get(i).getY());
				g2.rotate(enemyInfantry.get(i).getAngle());
				g2.drawImage(enemyInfantry.get(i).getImage().getImage(), -21, -21, 42, 42, this);
				g2.rotate(-enemyInfantry.get(i).getAngle());
				g2.fillRect(-15, -30, (int)(10*enemyInfantry.get(i).getLifetime()), 5);
				g2.translate(-enemyInfantry.get(i).getX(), -enemyInfantry.get(i).getY());
			} // End of Enemy Infantry Draw
					
			// Enemy tank Draw
			for (int i = 0; i < enemyTanks.size(); i++) {
				g2.setColor(enemyColor);
				// g2.fillPolygon(enemyTanks.get(i).getShape());
				g2.translate(enemyTanks.get(i).getX(), enemyTanks.get(i).getY());
				g2.rotate(enemyTanks.get(i).getAngle());
				g2.drawImage(enemyTanks.get(i).getImage().getImage(), -enemyHullImage.getIconWidth()/2, -enemyHullImage.getIconHeight()/2, this);
				g2.rotate(-enemyTanks.get(i).getAngle());
				g2.fillRect(-30, -60, (int)(20*enemyTanks.get(i).getLifetime()), 10);
				g2.rotate(enemyTanks.get(i).getTurretRotation());
				g2.drawImage(enemyTurretImage.getImage(), -enemyTurretImage.getIconWidth()/2, -enemyTurretImage.getIconHeight()/2, this);
				g2.rotate(-enemyTanks.get(i).getTurretRotation());
				g2.translate(-enemyTanks.get(i).getX(), -enemyTanks.get(i).getY());
			} // End of Enemy Tank Draw
			
			
			g2.setColor(playerColor);
			// g2.fillPolygon(player.getShape());
			g2.translate(-(WIDTH/2 - player.getX()), -(HEIGHT/2 - player.getY()));
			
			// Player Draw
			g2.translate(WIDTH/2, HEIGHT/2);
			g2.rotate(Math.toRadians(playerRotation));
			g2.drawImage(playerHullImage.getImage(), -playerHullImage.getIconWidth()/2, -playerHullImage.getIconHeight()/2, this);
			g2.rotate(Math.toRadians(-playerRotation));
			g2.fillRect(-30, -60, (int)(20*player.getLifetime()/(playerHealthDefault/3)), 10);
			g2.rotate(player.getTurretRotation());
			g2.drawImage(playerTurretImage.getImage(), -playerTurretImage.getIconWidth()/2, -playerTurretImage.getIconHeight()/2, this);
			g2.rotate(-player.getTurretRotation());
			g2.translate(-WIDTH/2, -HEIGHT/2);
			// End of Player Draw
			
			g2.translate(WIDTH/2 - player.getX(), HEIGHT/2 - player.getY());
			// Shell draw
			for (int i = 0; i < shells.size(); i++) {
				g2.translate(shells.get(i).getX(), shells.get(i).getY());
				g2.rotate(shells.get(i).getVelocityAngle());
				g2.drawImage(shellImage.getImage(), (int)(-shellImage.getIconWidth()*1.6/2), (int)(-shellImage.getIconHeight()*1.6/2), (int)(shellImage.getIconWidth()*1.6), (int)(shellImage.getIconHeight()*1.6), this);
				g2.rotate(-shells.get(i).getVelocityAngle());
				g2.translate(-shells.get(i).getX(), -shells.get(i).getY());
			} // End of Shell Draw
			
			// Bullet draw
			for (int i = 0; i < bullets.size(); i++) {
				g2.translate(bullets.get(i).getX(), bullets.get(i).getY());
				g2.rotate(bullets.get(i).getVelocityAngle());
				g2.drawImage(bulletImage.getImage(), -bulletImage.getIconWidth()/2, -bulletImage.getIconHeight()/2, this);
				g2.rotate(-bullets.get(i).getVelocityAngle());
				g2.translate(-bullets.get(i).getX(), -bullets.get(i).getY());
			} for (int i = 0; i < enemyBullets.size(); i++) {
				g2.translate(enemyBullets.get(i).getX(), enemyBullets.get(i).getY());
				g2.rotate(enemyBullets.get(i).getVelocityAngle());
				g2.drawImage(bulletImage.getImage(), -bulletImage.getIconWidth()/2, -bulletImage.getIconHeight()/2, this);
				g2.rotate(-enemyBullets.get(i).getVelocityAngle());
				g2.translate(-enemyBullets.get(i).getX(), -enemyBullets.get(i).getY());
			} // End of Bullet Draw
			
			// Smoke draw
			for (int i = 0; i < smokes.size(); i++) {
				g2.translate(smokes.get(i).getX(), smokes.get(i).getY());
				g2.rotate(smokes.get(i).getVelocityAngle());
				g2.drawImage(smokes.get(i).getImage().getImage(), -30, -30, 60, 60, this);
				g2.rotate(-smokes.get(i).getVelocityAngle());
				g2.translate(-smokes.get(i).getX(), -smokes.get(i).getY());
			} // End of smoke draw
			
			// Sparks draw
			
			// End of sparks draw
			
			// Explosions draw
			for (int i = 0; i < explosions.size(); i++) {
				// // g2.fillPolygon(explosions.get(i).getShape());
				g2.translate(explosions.get(i).getX(), explosions.get(i).getY());
				g2.drawImage(explosions.get(i).getImage().getImage(), -125, -125, 250, 250, this);
				g2.translate(-explosions.get(i).getX(), -explosions.get(i).getY());
			} // End of explosions draw
			
			g2.drawImage(foregroundImage.getImage(), -WIDTH-800, -WIDTH-800, WIDTH*2+1600, WIDTH*2+1600, this);
			g2.translate(-(WIDTH/2 - player.getX()), -(HEIGHT/2 - player.getY()));
			
			
			g2.setStroke(new BasicStroke(2));
			g2.setFont(fontSmaller);
			
			// Draw MiniMap
			g2.setColor(grayTrans);
			g2.fillRect(WIDTH-160, 20, 140, 140);
			g2.setColor(defaultColor);
			g2.drawRect(WIDTH-160, 20, 140, 140);
			// [---- Enemy
			g2.setColor(enemyColor);
			for (int i = 0; i < enemyTanks.size(); i++) {
				if (enemyTanks.get(i).getX() <= Math.abs(WIDTH+100) && enemyTanks.get(i).getY() <= Math.abs(WIDTH+100)) {
					g2.translate(WIDTH-90+enemyTanks.get(i).getX()/15, 90+enemyTanks.get(i).getY()/15);
					g2.rotate(enemyTanks.get(i).getAngle());
					g2.fillRect(-3, -3, 6, 6);
					g2.fillPolygon(new int[] {4, 6, 4}, new int[] {-2, 0, 2}, 3);
					g2.rotate(-enemyTanks.get(i).getAngle());
					g2.translate(-(WIDTH-90+enemyTanks.get(i).getX()/15), -(90+enemyTanks.get(i).getY()/15));
				}
			} for (int i = 0; i < enemyInfantry.size(); i++) {
				if (enemyInfantry.get(i).getX() <= Math.abs(WIDTH+100) && enemyInfantry.get(i).getY() <= Math.abs(WIDTH+100)) {
					g2.translate(WIDTH-90+enemyInfantry.get(i).getX()/15, 90+enemyInfantry.get(i).getY()/15);
					g2.rotate(enemyInfantry.get(i).getAngle());
					g2.fillOval(-2, -2, 4, 4);
					g2.fillPolygon(new int[] {2, 3, 2}, new int[] {-1, 0, 1}, 3);
					g2.rotate(-enemyInfantry.get(i).getAngle());
					g2.translate(-(WIDTH-90+enemyInfantry.get(i).getX()/15), -(90+enemyInfantry.get(i).getY()/15));
				}
			} // [---- Player
			g2.setColor(playerColor);
			g2.translate(WIDTH-90+player.getX()/15, 90+player.getY()/15);
			g2.rotate(Math.toRadians(playerRotation));
			g2.fillRect(-3, -3, 6, 6);
			g2.fillPolygon(new int[] {4, 6, 4}, new int[] {-2, 0, 2}, 3);
			g2.setColor(Color.LIGHT_GRAY);
			g2.rotate(Math.toRadians(-playerRotation));
			g2.setStroke(new BasicStroke(0));
			g2.drawRect(-WIDTH/30, -HEIGHT/30, WIDTH/15, HEIGHT/15);
			g2.translate(-(WIDTH-90+player.getX()/15), -(90+player.getY()/15));
			g2.setStroke(new BasicStroke(2));
			
			// Draw ToolTips Bar
			g2.setColor(grayTrans);
			g2.fillRect(20, 20, 60, 60);
			g2.fillRect(80, 20, 60, 60);
			g2.setColor(Color.LIGHT_GRAY);
			
			// ToolTip slot 1 | Shell Count
			String shellstr = "";
			if (HEShell == true) shellstr = "HE";
			else if (APShell == true) shellstr = "AP";
			g2.drawString(shellstr, 39, 45);
			g2.drawString("SHELL", 23, 65);
			g2.setFont(fontMini);
			shellstr = String.format("AP %02d", APShellCount);
			g2.drawString(shellstr, 34, 94);
			shellstr = String.format("HE %02d", HEShellCount);
			g2.drawString(shellstr, 35, 106);
			g2.setColor(blackTrans);
			g2.fillRect(20, 80-(60 * playerShellCD/playerShellCDReset), 60, 60 * playerShellCD/playerShellCDReset);
			
			// Tooltip slot 2 | M2HB Count
			g2.setColor(Color.LIGHT_GRAY);
			g2.setFont(fontSmaller);
			g2.drawString("M2HB", 88, 56);
			g2.setFont(fontMini);
			shellstr = String.format("%03d/%03d", MGCount, MGReserve);
			g2.drawString(shellstr, 88, 94);
			g2.setColor(blackTrans);
			g2.fillRect(80, 80-(60 * playerBulletCD/playerBulletCDReset), 60, 60 * playerBulletCD/playerBulletCDReset);
			
			
			g2.setColor(defaultColor);
			g2.drawRect(20, 20, 60, 60);
			g2.drawRect(80, 20, 60, 60);
			
			// Draw Next Spawn Wave
			if (enemyCooldown >= 345 && enemyCooldown <= 525) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.setFont(font);
				g2.drawString(String.format("SUPPLIES INCOMING!"), WIDTH/2-124, HEIGHT-160);
			}
			if (level == 1 && enemyCooldown >= 180 && enemyCooldown < 300) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.setFont(font);
				g2.drawString(String.format("SURVIVE"), WIDTH/2-54, HEIGHT-160);
			}
			if (enemyCooldown >= 3 && enemyCooldown <= 156) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.setFont(font);
				g2.drawString(String.format("Next Enemy Push in: "), WIDTH/2-120, HEIGHT-160);
				g2.setFont(fontBigger);
				g2.drawString(String.format("%02d:%02d", (int)(enemyCooldown/FPS), (int)((enemyCooldown%FPS)*(100/FPS))), WIDTH/2-42, HEIGHT-120);
			}
			
			// Draw Level & Score (Bottom Right)
			g2.setColor(Color.LIGHT_GRAY);
			g2.setFont(font);
			g2.drawString(String.format("Level: %02d Score: %06d", level, score), WIDTH-285, HEIGHT-20);
			
			// Draw HP (Bottom Left)
			g2.setColor(Color.LIGHT_GRAY);
			g2.setFont(font);
			if (invincibility == false) g2.drawString(String.format("HP: %02.0f%%", player.getLifetime()*100/6), 20, HEIGHT-20);
			else if (invincibility == true) g2.drawString(String.format("HP: INVINCIBLE"), 20, HEIGHT-20);
			
			// Pause screen
			if (pause == true) {
				g2.setColor(grayTrans);
				g2.fillRect(0, 0, WIDTH, HEIGHT);
				g2.setColor(Color.LIGHT_GRAY);
				g2.setFont(fontTitle);
				g2.drawString("PAUSED", WIDTH/2-230, 150);
				g2.setFont(fontBigger);
				g2.drawString("PRESS ESC TO RESUME", WIDTH/2-185, HEIGHT-160);
				g2.drawString("PRESS \"R\" TO RESTART", WIDTH/2-183, HEIGHT-120);
				g2.drawString("PRESS ENTER TO EXIT", WIDTH/2-173, HEIGHT-80);
				
			}
			
			// Game over screen
			if (gameOver == true) {
				g2.setColor(grayTrans);
				g2.fillRect(0, 0, WIDTH, HEIGHT);
				g2.setColor(Color.LIGHT_GRAY);
				g2.setFont(fontTitle);
				g2.drawString("GAME OVER", WIDTH/2-340, 150);
				if (scoreEntered == false && score > Integer.parseInt(leaderboard.get(1).toString().trim())) {
					isHighscore = true;
					g2.setFont(fontBigger);
					g.drawString("NEW HIGHSCORE", WIDTH/2-137, 340);
					g.drawString("INPUT NAME:", WIDTH/2-103, 380);
					g2.setFont(fontMenu);
					if (inputCooldown == 0) g.drawString(leaderboardName, WIDTH/2-(leaderboardName.length()*16), 440);
					else if (inputCooldown != 0) inputCooldown--;
					g.setFont(font);
				} else if (scoreEntered == true) {
					g2.setFont(fontBigger);
					g2.drawString("PRESS \"R\" TO RESTART", WIDTH/2-183, HEIGHT-120);
					g2.drawString("PRESS ENTER TO EXIT", WIDTH/2-173, HEIGHT-80);
				}
			}
		}
		
		// Draw Menu
		if (state == STATE.MENU) {
			g2.drawImage(menuBackgroundImage.getImage(), 0, 0, WIDTH, HEIGHT, this);
			g2.setColor(new Color(20, 20, 20, 60));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			g2.setColor(new Color(220, 220, 220));
			g2.setFont(fontTitle);
			g2.drawString("TANKERS", 105, 190);
			g2.setFont(fontMenu);
			g2.drawString("PLAY", 105, HEIGHT_DEFAULT - 235);
			g2.drawString("LEADERBOARDS", 105, HEIGHT_DEFAULT - 180);
			g2.drawString("HELP", 105, HEIGHT_DEFAULT - 125);
			g2.drawString("EXIT", 105, HEIGHT_DEFAULT - 70);
			g2.drawImage(arrowSelect.getImage(), 45, selectPosY, this);
		}
		
		// Draw Menu
		if (state == STATE.PLAYMENU) {
			g2.drawImage(menuBackgroundImage.getImage(), 0, 0, WIDTH, HEIGHT, this);
			g2.setColor(new Color(20, 20, 20, 60));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			g2.setColor(new Color(220, 220, 220));
			g2.setFont(fontTitle);
			g2.drawString("TANKERS", 105, 190);
			g2.setFont(fontMenu);
			g2.drawString("TRAINING GROUNDS", 105, HEIGHT_DEFAULT - 125);
			g2.drawString("ENDLESS SURVIVAL", 105, HEIGHT_DEFAULT - 70);
			g2.drawImage(arrowSelect.getImage(), 45, selectPosY, this);
		}
		
		if (state == STATE.LEADERBOARD) {
			g.setColor(new Color(120, 120, 120));
			g2.setFont(fontTitle);
			g2.drawString("LEADERBOARDS", 75, HEIGHT_DEFAULT - 285);
			g2.setColor(new Color(20, 20, 20, 60));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			g.setColor(new Color(220, 220, 220));
			g.setFont(fontMenu);
			for (int i = 0; i < leaderboard.size(); i += 2) {
				String lbStr = String.format(leaderboard.get(i).toString());
				g.drawString(lbStr, 60, 600 - (i * 30));
				lbStr = String.format("%08d", Integer.parseInt(leaderboard.get(i+1).toString().trim()));
				g.drawString(lbStr, 810, 600 - (i * 30));
			}
		}
		
		if (state == STATE.HELP) {
			g.setColor(new Color(120, 120, 120));
			g2.setFont(fontTitle);
			g2.drawString("HELP", 395, HEIGHT_DEFAULT - 285);
			g2.setColor(new Color(20, 20, 20, 60));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			// Actual help
			g.setColor(new Color(220, 220, 220));
			g.setFont(fontBigger);
			g.drawString("CONTROLS", 45, 65);
			g.drawString("ARMAMENTS", 565, 65);
			// For Controls
			g.drawImage(helpImages[0], 110, 70, 80, 80, this);
			g.drawImage(helpImages[1], 40, 140, 80, 80, this);
			g.drawImage(helpImages[2], 110, 140, 80, 80, this);
			g.drawImage(helpImages[3], 180, 140, 80, 80, this);
			g.drawImage(helpImages[4], 50, 200, 120, 120, this);
			g.drawImage(helpImages[10], 180, 220, 80, 80, this);
			g.drawImage(helpImages[9], 110, 305, 80, 80, this);
			g.drawImage(helpImages[5], 65, 380, 80, 80, this);
			g.drawImage(helpImages[6], 155, 380, 80, 80, this);
			g.drawImage(helpImages[7], 110, 460, 80, 80, this);
			g.drawImage(helpImages[8], 110, 540, 80, 80, this);
			g.setFont(font);
			g.drawString("NAVIGATION", 285, 117+20);
			g.drawString("IN MENU AND GAME", 285, 117+71-20);
			g.drawString("FIRE MACHINE GUN", 285, 117+152);
			g.drawString("FIRE MAIN GUN", 285, 117+237);
			g.drawString("BACK  |  ACCPET", 285, 117+309);
			g.drawString("RELOAD MACHINE GUN", 285, 117+392);
			g.drawString("SWITCH TANK SHELL (HE / AP)", 285, 117+473);
			
			// For Armaments
			g.drawString("HE SHELL", 585, 117);
			g.drawString("AP SHELL", 585, 197);
			g.drawString("M2HB", 585, 257);
			g.drawString("SUPPLIES", 585, 337);
			
			g.setFont(fontSmaller);
			g.drawString("HIGH EXPLOSIVE AND LOTS OF FRAGMENTATION,", 587, 137);
			g.drawString("BUT LOW PENETRATION", 587, 157);
			g.drawString("ARMOUR PIERCING, BUT LOW EXTERNAL FRAGMENTATION", 587, 217);
			g.drawString("BROWNING .50 CALIBER MACHINE GUN,", 587, 277);
			g.drawString("ABLE TO PENETRATE THROUGH LIGHT ARMOUR", 587, 297);
			g.drawString("IMMEDIATELY GIVEN AT THE START OF EVERY 5TH LEVEL,", 587, 357);
			g.drawString("REPLENISHES AMMO AND GIVES UP TO 66% HP", 587, 377);
		}
		
		//* Draw info (Bottom Left | Debugging only)
		if (debugging == true) {
			g2.setColor(defaultColor);
			g2.setFont(fontSmaller);
			String infoStr = String.format("pX %.2f, pY %.2f, mX %.2f, mY %.2f, Sc %04d", player.getX(), player.getY(), mouseX, mouseY, score);
			g2.drawString(infoStr, 140, HEIGHT-38);
			infoStr = String.format("pR %.2f, mR %.2f, FPS Ac/Ex %d/%d, Scale %01.2f, %01.2f", player.getTurretRotation(), mouseRad, 1000/timeDif, FPS, scaleX, scaleY);
			g2.drawString(infoStr, 140, HEIGHT-20);} //*/
		
		// EVERYTHING BEFORE THIS (Except for mouse)
		g2.translate(WIDTH/2 - player.getX(), HEIGHT/2 - player.getY());
		
		// Mouse draw
		g2.translate(mouseX, mouseY);
		g2.drawImage(crosshairImage.getImage(), -12, -12, 24, 24, this);
		g2.translate(-mouseX, -mouseY);
		
	}
	
	public boolean intersects(Shape s1, Shape s2) {
		Area a1 = new Area(s1);
		Area a2 = new Area(s2);
		a1.intersect(a2);
		return !a1.isEmpty();
	}
	
	public boolean intersects(Shape s, double x, double y) {
		Area area = new Area(s);
		return area.contains(x, y);
	}
	
	public void turnTurret(Entity ent, double targetX, double targetY, double rotationSpeed) {
		double targetRad = Math.atan2(targetY-ent.getY(), targetX-ent.getX());
		if (Math.abs(ent.getTurretVelocity()) <= rotationSpeed) {
			double supplementAngle  = Math.PI-Math.abs(ent.getTurretRotation());
			if ((ent.getTurretRotation() < targetRad+0.01 && ent.getTurretRotation() > targetRad-0.01) || (Math.abs(ent.getTurretRotation()) >= 3.14 && Math.abs(targetRad) >= 3.14)) ent.setTurretVelocity(0);
			else if (ent.getTurretRotation() < -Math.PI/2 && targetRad > supplementAngle) ent.modifyTurretVelocity(-0.02);
			else if (ent.getTurretRotation() > Math.PI/2 && targetRad < -supplementAngle) ent.modifyTurretVelocity(0.02);
			else if (ent.getTurretRotation() < 0 && targetRad > supplementAngle) ent.modifyTurretVelocity(-0.02);
			else if (ent.getTurretRotation() > 0 && targetRad < -supplementAngle) ent.modifyTurretVelocity(0.02);
			else if (ent.getTurretRotation() < targetRad) ent.modifyTurretVelocity(0.02);
			else if (ent.getTurretRotation() > targetRad) ent.modifyTurretVelocity(-0.02);} 
		if (ent.getTurretRotation() > Math.PI) ent.setTurretRotation(-Math.PI);
		if (ent.getTurretRotation() < -Math.PI) ent.setTurretRotation(Math.PI);
		ent.modifyTurretRotation(ent.getTurretVelocity());

		if (ent.getTurretVelocity() >= 0.01) ent.modifyTurretVelocity(-0.01);
		if (ent.getTurretVelocity() <= -0.01) ent.modifyTurretVelocity(0.01);
	}
	
	public void turnTank(Entity ent, double targetX, double targetY, double rotationSpeed) {
		double defaultSpeed;
		if (ent.getImage() == enemyRiflemanImage) defaultSpeed = 2;
		else defaultSpeed = 0.9;
		double targetRad = Math.atan2(targetY-ent.getY(), targetX-ent.getX());
		if (Math.abs(ent.getDefaultAngle()) <= rotationSpeed) {
			double supplementAngle  = Math.PI-Math.abs(ent.getAngle());
			if ((ent.getAngle() < targetRad+0.01 && ent.getAngle() > targetRad-0.01) || (Math.abs(ent.getAngle()) >= 3.14 && Math.abs(targetRad) >= 3.14)) ent.setDefaultAngle(0);
			else if (ent.getAngle() < -Math.PI/2 && targetRad > supplementAngle) ent.setDefaultAngle(-defaultSpeed);
			else if (ent.getAngle() > Math.PI/2 && targetRad < -supplementAngle) ent.setDefaultAngle(defaultSpeed);
			else if (ent.getAngle() < 0 && targetRad > supplementAngle) ent.setDefaultAngle(-defaultSpeed);
			else if (ent.getAngle() > 0 && targetRad < -supplementAngle) ent.setDefaultAngle(defaultSpeed);
			else if (ent.getAngle() < targetRad) ent.setDefaultAngle(defaultSpeed);
			else if (ent.getAngle() > targetRad) ent.setDefaultAngle(-defaultSpeed);}
		if (ent.getTurretVelocity() != 0 && ent.getImage() != enemyRiflemanImage) dirtKickup(ent, Math.toDegrees(ent.getAngle()));
		if (ent.getAngle() > Math.PI) {ent.setAngle(-180);}
		if (ent.getAngle() < -Math.PI) {ent.setAngle(180);}
		ent.addAngle();
	}
	
	public void fireEntity(Entity ent, double tRot, boolean shell, ArrayList<Entity> projectile) {
		if (projectile == shells) {
			projectile.add(new Entity((ent.getX() + Math.cos(tRot)*32), (ent.getY() + Math.sin(tRot)*32), shellImage, shell));
			for (int i = 0; i < 50; i++) {
				smokes.add(new Entity((ent.getX() + Math.cos(tRot)*48), (ent.getY() + Math.sin(tRot)*48), smokeImage,
						20+(int)(Math.random()*30), -0.05, Math.toDegrees(tRot)-119+Math.random()*240));
				smokes.get(smokes.size()-1).setAngle(smokes.get(smokes.size()-1).getDefaultAngle());
				smokes.get(smokes.size()-1).setVelocity(1+Math.random()*4);
			}
			projectile.get(projectile.size() - 1).setXVelocity((Math.cos(tRot) * 24));
			projectile.get(projectile.size() - 1).setYVelocity((Math.sin(tRot) * 24));
		} else if (projectile == bullets || projectile == enemyBullets) {
			if (ent.getImage() == enemyRiflemanImage) {
				projectile.add(new Entity((ent.getX() + Math.cos(tRot)*6), (ent.getY() + Math.sin(tRot)*6), bulletImage));
				smokes.add(new Entity((ent.getX() + Math.cos(tRot)*14), (ent.getY() + Math.sin(tRot)*14), smokeImage,
						10+(int)(Math.random()*15), -0.05, Math.toDegrees(tRot)-29+Math.random()*60));
			} else {
				projectile.add(new Entity((ent.getX() + Math.cos(tRot+Math.toRadians(75))*9), (ent.getY() + Math.sin(tRot+Math.toRadians(75))*9), bulletImage));
				smokes.add(new Entity((ent.getX() + Math.cos(tRot+Math.toRadians(25))*25), (ent.getY() + Math.sin(tRot+Math.toRadians(25))*25), smokeImage,
						10+(int)(Math.random()*15), -0.05, Math.toDegrees(tRot)-29+Math.random()*60));
			}
			
			
			smokes.get(smokes.size()-1).setAngle(smokes.get(smokes.size()-1).getDefaultAngle());
			smokes.get(smokes.size()-1).setVelocity(1+Math.random()*1);
			projectile.get(projectile.size() - 1).setXVelocity((Math.cos(tRot) * 27));
			projectile.get(projectile.size() - 1).setYVelocity((Math.sin(tRot) * 27));
		}
	}
	
	public void projectilesMoving(ArrayList<Entity> projectile) {
		for (int i = 0; i < projectile.size(); i++) {
			projectile.get(i).move(projectile.get(i).getXVelocity(), projectile.get(i).getYVelocity()); 
			if (projectile.get(i).getY() <= player.getY()-HEIGHT/2-250 || projectile.get(i).getY() >= player.getY()+HEIGHT/2+250
				|| projectile.get(i).getX() >= player.getX()+WIDTH/2+250 || projectile.get(i).getX() <= player.getX()-WIDTH/2-250) projectile.remove(i);
		}
	}
	
	public void particlesMoving(ArrayList<Entity> particles) {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).move(Math.cos(particles.get(i).getAngle()) * particles.get(i).getVelocity(), Math.sin(particles.get(i).getAngle()) * particles.get(i).getVelocity());
			particles.get(i).modifyLifetime(-0.5);
			particles.get(i).modifyVelocity();
			if (particles.get(i).getLifetime() <= 0) particles.remove(i);
		}
	}
	
	public void dirtKickup(Entity ent, double rotation) {
		int subAng = 0, randomInt = 1;
		randomInt = (int)(Math.random()*4+1);
		if (randomInt == 1) subAng = 30;
		else if (randomInt == 2) subAng = -30;
		else if (randomInt == 3) subAng = -150;
		else if (randomInt == 4) subAng = 150;
		dirts.add(new Entity((ent.getX() + Math.cos(Math.toRadians(rotation+subAng))*42), (ent.getY() + Math.sin(Math.toRadians(rotation+subAng))*42), dirtImage,
				3+(int)(Math.random()*15), -0.05, Math.random()*361));
		dirts.get(dirts.size()-1).setAngle(dirts.get(dirts.size()-1).getDefaultAngle());
		dirts.get(dirts.size()-1).setVelocity(0.5+Math.random()*1);
	}
	
	public void getHitbox(Entity ent) {
		if (ent.getImage() == enemyRiflemanImage) ent.setShape(new Polygon(
				new int[] {(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(65))*11),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(80))*20),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(145))*30),
					(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(160))*26)},
				new int[] {(int)(ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(65))*11),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(80))*20),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(145))*30),
					(int) (ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(160))*26)}, 4));
		else if (ent.getImage() == explodeF1) ent.setShape(new Polygon(
				new int[] {(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(0))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(45))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(90))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(135))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(180))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(135))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(90))*100),
					(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(45))*100)},
				new int[] {(int)(ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(0))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(45))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(90))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(135))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(180))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(135))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(90))*100),
					(int) (ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(45))*100)}, 8));
		else if (ent.getImage() != enemyRiflemanImage) ent.setShape(new Polygon(
				new int[] {(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(30))*45),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(30))*45),
					(int) (ent.getX()+Math.cos(ent.getAngle()-Math.toRadians(150))*45),
					(int) (ent.getX()+Math.cos(ent.getAngle()+Math.toRadians(150))*45)},
				new int[] {(int)(ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(30))*45),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(30))*45),
					(int) (ent.getY()+Math.sin(ent.getAngle()-Math.toRadians(150))*45),
					(int) (ent.getY()+Math.sin(ent.getAngle()+Math.toRadians(150))*45)}, 4));
	}
	
	public void tankCollision(Entity ent1, Entity ent2, double ent2Velocity) {
		double ent1RadToEnt2 = Math.atan2(ent1.getY()-ent2.getY(), ent1.getX()-ent2.getX());
		double ent2RadToEnt1 = Math.atan2(ent2.getY()-ent1.getY(), ent2.getX()-ent1.getX());
		ent1.move((Math.cos(ent1RadToEnt2) * (1 + Math.abs(ent2Velocity))), (Math.sin(ent1RadToEnt2) * (1 + Math.abs(ent2Velocity))));
		ent2.move((Math.cos(ent2RadToEnt1) * Math.abs(ent2Velocity)), (Math.sin(ent2RadToEnt1) *  Math.abs(ent2Velocity)));
		if (Math.abs(Math.toDegrees(ent1RadToEnt2-ent1.getAngle())) % 90 != 0) {
			if (Math.abs(Math.toDegrees(ent1RadToEnt2-ent1.getAngle())) % 90 <= 45) ent1.addAngle(-1 - Math.abs(ent2Velocity));
			if (Math.abs(Math.toDegrees(ent1RadToEnt2-ent1.getAngle())) % 90 > 45) ent1.addAngle(1 + Math.abs(ent2Velocity));
		}
	}
	
	public void enemyMovement(ArrayList<Entity> entity) {
		for (int i = 0; i < entity.size(); i++) {
			if (entity.get(i).getVelocity() <= 6 && entity.get(i).getFollow() == false) entity.get(i).modifyVelocity(entity.get(i).getDefaultVelocity());
			if (Math.abs(entity.get(i).getVelocity()) >= 0.5 && entity != enemyInfantry) {dirtKickup(entity.get(i), Math.toDegrees(entity.get(i).getAngle()));}
			if (entity == enemyInfantry && entity.get(i).getFollow() != false && (entity.get(i).getY() <= player.getY()-HEIGHT_DEFAULT/2+10 || entity.get(i).getY() >= player.getY()+HEIGHT_DEFAULT/2-10
					|| entity.get(i).getX() >= player.getX()+HEIGHT_DEFAULT/2-10 || entity.get(i).getX() <= player.getX()-HEIGHT_DEFAULT/2+10)) entity.get(i).setVelocity(3);
			// Enemy hitbox hits enemy hitbox
			for (int j = 0; j < entity.size(); j++) {
				if (j != i && intersects(entity.get(j).getShape(), entity.get(i).getShape()) == true) {
					tankCollision(entity.get(i), entity.get(j), entity.get(j).getVelocity());
					entity.get(j).setVelocity(-entity.get(j).getVelocity());
				}
			} if (entity == enemyTanks) for (int j = 0; j < enemyInfantry.size(); j++) {
				if (j != i && intersects(enemyInfantry.get(j).getShape(), enemyTanks.get(i).getShape()) == true) {
					tankCollision(enemyTanks.get(i), enemyInfantry.get(j), enemyInfantry.get(j).getVelocity());
					enemyInfantry.get(j).setVelocity(-enemyInfantry.get(j).getVelocity());
				}
			}
			// If enemy hitbox doesn't hit player (Do normal tasks)
			if (intersects(player.getShape(), entity.get(i).getShape()) == false) {
				if (entity.get(i).getFollow() == false) {
					entity.get(i).addAngle();
				} else if (entity.get(i).getFollow() == true) {
					if (entity == enemyInfantry) turnTank(entity.get(i), player.getX(), player.getY(), 100);
					else turnTank(entity.get(i), player.getX(), player.getY(), 6);
					if ((entity.get(i).getY() <= player.getY()-HEIGHT_DEFAULT/2+10 || entity.get(i).getY() >= player.getY()+HEIGHT_DEFAULT/2-10
							|| entity.get(i).getX() >= player.getX()+HEIGHT_DEFAULT/2-10 || entity.get(i).getX() <= player.getX()-HEIGHT_DEFAULT/2+10)) {
						if (entity.get(i).getVelocity() <= 6) entity.get(i).modifyVelocity(0.2);
					} else {
						if (entity.get(i).getVelocity() > 0) entity.get(i).modifyVelocity(-0.1);
						if (entity.get(i).getVelocity() < 0) entity.get(i).modifyVelocity(0.1);
					}
				} entity.get(i).move((Math.cos(entity.get(i).getAngle()) * entity.get(i).getVelocity()), (Math.sin(entity.get(i).getAngle()) * entity.get(i).getVelocity()));

			} // Enemy hitbox hits player hitbox
			else if (intersects(player.getShape(), entity.get(i).getShape()) == true) {
				if (entity != enemyInfantry) {
					tankCollision(entity.get(i), player, playerVelocity);
					playerVelocity = -playerVelocity;
				} else {entity.remove(i); score += 10;}
			}
		}
	}
	
	private void playSound(int i) {
		// System.out.println("Sound played");
		try {
			URL url = ClassLoader.getSystemResource(soundStr[i]);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			sound = AudioSystem.getClip();
			sound.open(audioInputStream);
			sound.start();
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
	}
	
	private void playMGSound() {
		try {
			AudioInputStream mgFiringStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("Sounds/machinegun_firing.wav"));
			mgFiring = AudioSystem.getClip();
			mgFiring.open(mgFiringStream);
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
	}
	
	private void playTankIdleEngine() {
		try {
			AudioInputStream audioIdleEngineStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(soundStr[4]));
			engineIdleSound = AudioSystem.getClip();
			engineIdleSound.open(audioIdleEngineStream);
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
	}
	
	private void playTankLoadEngine() {
		try {
			AudioInputStream audioLoadEngineStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(soundStr[5]));
			engineLoadSound = AudioSystem.getClip();
			engineLoadSound.open(audioLoadEngineStream);
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
	}
	
	private void playMenuMusic() {
		try {
			AudioInputStream audioMenuMusic = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(soundStr[0]));
			menuMusic = AudioSystem.getClip();
			menuMusic.open(audioMenuMusic);
		} catch (Exception e) {System.out.println("An error occured - " + e.getMessage());}
	}
	
	public void resetGame() {
		posSelect = 1;
		if (state != STATE.GAME) tutorial = false;
		tutorialSpawned = false;
		score = 0;
		player = new Entity (0, 0, placeHolder, playerHealthDefault);
		player.setTurretRotation(Math.toRadians(-90));
		shells.clear();
		bullets.clear();
		enemyBullets.clear();
		enemyTanks.clear();
		enemyInfantry.clear();
		smokes.clear();
		dirts.clear();
		sparks.clear();
		explosions.clear();
		playerVelocity = 0;
		playerRVelocity = 0;
		playerRotation = -90;
		HEShell = true;
		APShell = false;
		playerBulletCD = 0;
		playerBulletCDReset = 240;
		playerShellCD = 0;
		playerShellCDReset = 200;
		HEShellCount = 35;
		APShellCount = 35;
		MGCount = 200;
		MGReserve = 600;
		playerHealthDefault = 6.0;
		enemyCooldown = 300;
		leftClick = false;
		rightClick = false;
		rightPressed = false;
		leftPressed = false;
		upPressed = false;
		downPressed = false;
		spacePressed = false;
		reloadPressed = false;
		pause = false;
		gameOver = false;
		toggleShellCD = 0;
		level = 1;
		hitConfirmedCD = 0;
		engineRevCD = 0;
		engineDecCD = 0;
		isHighscore = false;
		scoreEntered = false;
	}
	
	public void softResetGame() {
		
	}
	
	public void toggleFullscreen () {
		if (setFullscreen == true) {
			gDevice.setFullScreenWindow(frame);
			scaleX = (WIDTH_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getWidth()-1.6));
			scaleY = (HEIGHT_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getHeight()-.9));
		} else {
			gDevice.setFullScreenWindow(null);
			scaleX = (WIDTH_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getWidth()-160));
			scaleY = (HEIGHT_DEFAULT / (Toolkit.getDefaultToolkit().getScreenSize().getHeight()-90));
		}
	}
	
	private class Key extends KeyAdapter{
		public void keyPressed (KeyEvent e) {
			if (state == STATE.PLAYMENU) {
				if ((e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) && posSelect < 2) {selectPosY += 55; posSelect += 1; playSound(20);}
				if ((e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) && posSelect > 1) {selectPosY -= 55; posSelect -= 1; playSound(20);}
				if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
					playSound(21);
					if (posSelect == 1) {
						resetGame();
						tutorial = true;
					} else if (posSelect == 2) resetGame();
					selectPosY = HEIGHT_DEFAULT - 305;
					state = STATE.GAME;
				}
			}
			
			if (state == STATE.MENU) {
				if ((e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) && posSelect < 4) {selectPosY += 55; posSelect += 1; playSound(20);}
				if ((e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) && posSelect > 1) {selectPosY -= 55; posSelect -= 1; playSound(20);}
				if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
					playSound(21);
					if (posSelect == 1) {
						state = STATE.PLAYMENU;
						selectPosY = HEIGHT_DEFAULT - 195;
						posSelect = 1;
					} else if (posSelect == 2) {
						state = STATE.LEADERBOARD;
					} else if (posSelect == 3) {
						state = STATE.HELP;
					} else System.exit(0);
				}
			}
			
			if (state != STATE.MENU && state != STATE.GAME) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					playSound(22);
					if (state == STATE.PLAYMENU) {
						selectPosY = HEIGHT_DEFAULT - 305;
						posSelect = 1;
					}
					state = STATE.MENU;
				}
			}
			
			if (pause == true || (gameOver == true && isHighscore == false)) {
				if (e.getKeyCode() == KeyEvent.VK_R) {
					playSound(21);
					pause = !pause;
					resetGame();
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					playSound(21);
					pause = !pause;
					state = STATE.MENU;
					resetGame();
				}	
			}
			
			if (state == STATE.GAME) {
				if (e.getKeyCode() == KeyEvent.VK_D) rightPressed = true;
				if (e.getKeyCode() == KeyEvent.VK_A) leftPressed = true;
				if (e.getKeyCode() == KeyEvent.VK_W) upPressed = true;
				if (e.getKeyCode() == KeyEvent.VK_S) downPressed = true;
				if (e.getKeyCode() == KeyEvent.VK_SPACE) spacePressed = true;
				if (e.getKeyCode() == KeyEvent.VK_R) reloadPressed = true;
				if (e.getKeyCode() == KeyEvent.VK_G && APShellCount > 0 && HEShellCount > 0 && toggleShellCD <= 0) {
					APShell = !APShell; HEShell = !HEShell; playerShellCD = playerShellCDReset; toggleShellCD = 45;
					if (APShell == true) playSound(11);
					else if (HEShell == true) playSound(12);
				}
				// if (e.getKeyCode() == KeyEvent.VK_F9) invincibility = !invincibility;
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameOver == false) {
					if (!pause) {
						playSound(22);
						mgFiring.stop();
						engineIdleSound.stop();
						engineLoadSound.stop();
					} pause = !pause;
				}
				if (gameOver == true && isHighscore == true && tutorial == false && inputCooldown == 0) {
					if (Character.toString(e.getKeyChar()).matches("[a-zA-Z0-9 ]+") && leaderboardName.length() < 13) leaderboardName += Character.toString(e.getKeyChar()).toUpperCase();
					if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && leaderboardName.length() >= 1) leaderboardName = leaderboardName.substring(0, leaderboardName.length() - 1);
					if (e.getKeyCode() == KeyEvent.VK_ENTER && leaderboardName.length() >= 1) {
						isHighscore = false;
						scoreEntered = true;
						leaderboardScore = score;
						int leaderboardPos = 0;
						leaderboard.clear();
						// [---- Start of read leaderboard file ----]
						try {
							File leaderboardFile = new File(System.getProperty("user.dir"), "tankers_leaderboard.txt");
							URL url = ClassLoader.getSystemResource("tankers_leaderboard.txt");
							Scanner input;
							if (!leaderboardFile.exists() || leaderboardFile.length() <= 1) {
								leaderboardFile.createNewFile();
								input = new Scanner(url.openStream());
							} else input = new Scanner(leaderboardFile);
							input.useDelimiter("-|\n");
							
							for(int i = 1; input.hasNext(); i++) {
								leaderboard.add(new String(input.next()));
							}
						} catch (Exception hsE) {System.out.println("An error occured - " + hsE.getMessage());}
						// [---- End of read leaderboard file ----]
						while (score > Integer.parseInt(leaderboard.get(leaderboardPos+1).toString().trim()) && leaderboardPos < 18) leaderboardPos += 2;
						if (score > Integer.parseInt(leaderboard.get(19).toString().trim())) {
							leaderboard.add(new String(leaderboardName));
							leaderboard.add(new String(String.format("%d", leaderboardScore)));
						} else if (leaderboardPos <= 18) {
							leaderboard.add(leaderboardPos, new String(leaderboardName));
							leaderboard.add(leaderboardPos+1, new String(String.format("%d", leaderboardScore)));
						} leaderboard.remove(0); leaderboard.remove(0);
						File leaderboardFile = new File(System.getProperty("user.dir"), "tankers_leaderboard.txt");
						try(FileWriter fw = new FileWriter(leaderboardFile, false); // true = append file || false = overwrite file
								BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
							for(int i = 0; i < leaderboard.size(); i += 2) {
								out.println(String.format("%s-%s", leaderboard.get(i).toString(), leaderboard.get(i+1).toString()));
							} out.close();
						} catch (IOException fe) {System.out.println("An error occured - " + fe.getMessage());}	
					}
				}
			}
			
			// Below is for debugging purposes, make sure to comment out
			if (e.getKeyCode() == KeyEvent.VK_F3) debugging = !debugging;
			if (debugging == true) {
				if (e.getKeyCode() == KeyEvent.VK_EQUALS) {scaleX += 0.05; scaleY += 0.05;}
				if (e.getKeyCode() == KeyEvent.VK_MINUS) {scaleX -= 0.05; scaleY -= 0.05;}
			}
			if (e.getKeyCode() == KeyEvent.VK_F11) {
				setFullscreen = !setFullscreen;
				toggleFullscreen();
			}
				
		}
		
		public void keyReleased (KeyEvent e) {

			if (state == STATE.GAME) {
				if (e.getKeyCode() == KeyEvent.VK_D) rightPressed = false;
				if (e.getKeyCode() == KeyEvent.VK_A) leftPressed = false;
				if (e.getKeyCode() == KeyEvent.VK_W) upPressed = false;
				if (e.getKeyCode() == KeyEvent.VK_S) downPressed = false;
				if (e.getKeyCode() == KeyEvent.VK_SPACE) spacePressed = false;
				if (e.getKeyCode() == KeyEvent.VK_R) reloadPressed = false;
			}
		}
	}
	
	public class Mouse extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) leftClick = true;
			if (e.getButton() == MouseEvent.BUTTON3) rightClick = true;
		}
		
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) leftClick = false;
			if (e.getButton() == MouseEvent.BUTTON3) rightClick = false;	
		}
	}
}