package gameClass;

import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.ImageIcon;

public class Entity {
	private double x, y, b, h, lifetime;
	private ImageIcon img;
	private Polygon shape;
	private double angle, tx, ty, velocity, xVelocity, yVelocity, tVelocity, defaultAngle, defaultVelocity, reloadTime, turretRotation;
	private boolean shell, passive, follow;
	
	public Entity(double x, double y, ImageIcon img) {
		this.x = x; this.y = y; this.img = img;
		b = img.getIconWidth(); h = img.getIconHeight();
		angle = Math.toRadians(-90);
		getCoordinate();
	}
	
	public Entity(double x, double y, ImageIcon img, double lifetime) {
		this.x = x; this.y = y; this.img = img; this.lifetime = lifetime;
		b = img.getIconWidth(); h = img.getIconHeight();
	}
	
	public Entity(double x, double y, ImageIcon img, double lifetime, double dVel, double dAng) {
		this.x = x; this.y = y; this.img = img; this.lifetime = lifetime; defaultVelocity = dVel; defaultAngle = dAng;
		b = img.getIconWidth(); h = img.getIconHeight();
	}
	
	public Entity(double x, double y, ImageIcon img, double lifetime, double dVel, double dAng, boolean p) {
		this.x = x; this.y = y; this.img = img; this.lifetime = lifetime; defaultVelocity = dVel; defaultAngle = dAng; passive = p;
		b = img.getIconWidth(); h = img.getIconHeight();
	}
	
	public Entity(double x, double y, ImageIcon img, double lifetime, double dVel, boolean followPlayer, boolean p) {
		this.x = x; this.y = y; this.img = img; this.lifetime = lifetime; defaultVelocity = dVel; follow = followPlayer; passive = p;
		b = img.getIconWidth(); h = img.getIconHeight();
	}
	
	public Entity(double x, double y, ImageIcon img, boolean shell) {
		this.x = x; this.y = y; this.img = img; this.shell = shell;
		b = img.getIconWidth(); h = img.getIconHeight();
	}
	
	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	
	public double getB() {return b;}
	public void setB(double b) {this.b = b;}
	
	public double getH() {return h;}
	public void setH(double h) {this.h = h;}
	
	public double getDefaultAngle() {return defaultAngle;}
	public void setDefaultAngle(double ang) {defaultAngle = ang;}
	
	public double getDefaultVelocity() {return defaultVelocity;}
	public void setDefaultVelocity(double vel) {defaultVelocity = vel;}
	
	public double getVelocity() {return velocity;}
	public void setVelocity(double v) {velocity = v;}
	public void modifyVelocity(double v) {velocity += v;}
	public void modifyVelocity() {velocity += defaultVelocity;}
	
	public double getXVelocity() {return xVelocity;}
	public void setXVelocity(double xV) {xVelocity = xV;}
	
	public double getYVelocity() {return yVelocity;}
	public void setYVelocity(double yV) {yVelocity = yV;}
	
	public double getTurretVelocity() {return tVelocity;}
	public void setTurretVelocity(double v) {tVelocity = v;}
	public void modifyTurretVelocity(double v) {tVelocity += v;}
	
	public double getVelocityAngle() {return Math.atan2(getYVelocity(), getXVelocity());}
	
	public double getAngle() {return angle;}
	public void setAngle(double ang) {
		angle = Math.toRadians(ang);
		if (ang == 0) angle = 0;
		getCoordinate();
	}
	public void addAngle(double ang) {
		angle += Math.toRadians(ang);
		getCoordinate();
	}
	public void addAngle() {
		angle += Math.toRadians(defaultAngle);
		getCoordinate();
	}
	
	public double getTX() {return tx;}
	public void setTX(double tx) {this.tx = tx;}
	
	public double getTY() {return ty;}
	public void setTY(double ty) {this.ty = ty;}
	
	public double getLifetime() {return lifetime;}
	public void setLifetime(double lifetime) {this.lifetime = lifetime;}
	public void modifyLifetime(double num) {lifetime += num;}
	
	public double getReloadTime() {return reloadTime;}
	public void setReloadTime(double reloadTime) {this.reloadTime = reloadTime;}
	public void modifyReloadTime(double num) {reloadTime += num;}
	
	public double getTurretRotation() {return turretRotation;}
	public void setTurretRotation(double turretRotation) {this.turretRotation = turretRotation;}
	public void modifyTurretRotation(double num) {turretRotation += num;}
	
	public boolean getPassive() {return passive;}
	public void setPassive(boolean p) {passive = p;}
	
	public boolean getFollow() {return follow;}
	public void setFollow(boolean f) {follow = f;}
	
	public boolean getShell() {return shell;}
	public void setShell(boolean shell) {this.shell = shell;}
	
	public ImageIcon getImage() {return img;}
	public void setImage(ImageIcon img) {this.img = img;}
	
	public Polygon getShape() {return shape;}
	public void setShape(Polygon  s) {shape = s;}
	
	public void draw(Graphics g) {
		img.paintIcon(null, g, (int)(x-(b/2)), (int)(y-(b/2)));
	}
	
	public void move (double xAmount, double yAmount) {
		x += xAmount;
		y += yAmount;
	}
	
	public void move (double ang) {
		angle += Math.toRadians(ang);
		getCoordinate();
	}
	
	public void getCoordinate() {
		tx = (b/2) * Math.cos(angle);
		ty = (b/2) * Math.sin(angle);
	}
	
	public void resetEnemy () {
		x = (double)(Math.random()*700) + 51;
		y = -25;
	}
	
}