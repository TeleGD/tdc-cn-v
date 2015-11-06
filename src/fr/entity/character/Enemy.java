package fr.entity.character;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import fr.util.Collisions;
import fr.util.Movable;
import fr.util.Rectangle;
import fr.world.World;

public class Enemy extends Movable implements Rectangle {
	
	private double speedMax; // D�finit la vitesse max que les ennemis atteindront.
	private int hp; // Points de vie des ennemis, � 1 pour commencer.
	private static int numberEnemy = 0; // On num�rote nos ennemis pour pouvoir g�rer leur suppression via une liste.
	private int id;
	private int i; //  Variable pour un compteur
	private int frameTest = 0;
	
	public Enemy () {
	super(); // r�cup�re les attributs de la classe Movable
	hp = 1; // On donne une valeur � hp
	x = r.nextInt(800-(int)width); // On place l'ennemi � une position al�atoire sur l'axe x
	testXPlayer(); // On v�rifie qu'on ne fait pas spawn SUR le joueur (reste � g�rer les obstacles)
	y = 600-32-(int)height; // On pose l'ennemi sur le sol
	accelX = 0.02*r.nextDouble() + 0.005; // Acc�l�ration al�atoire d�finie ici (entre 0.025 et 0.005)
	speedMax = (r.nextInt(14))/500 + 0.2; // vitesse max al�atoire d�finie ici entre 1 et 15 (ou 14)
	isMoving = true; // Un booleen au cas o� le joueur puisse arr�ter le d�placement de l'ennemi
	World.getEnemies().add(this);
	id = numberEnemy;
	numberEnemy++;
	}
	
	Random r = new Random(); // On se cr�e une variable pour utiliser la fonction random
	
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException { // gestion de l'affichage
		g.fillRect((float)x, (float)y, (float)width, (float)height);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException { // gestion des updates � chaque frame
		moveX(delta);
		frameTest ++;
		limitX();
		damageEnemy();
		testEnemy();
		deathEnemy();
		backX();
	}
	
	public void testXPlayer(){ //permet de reset position au spawn si collision ac joueur : OK
		while (Collisions.isCollisionRectRect(this,World.getPlayer())){ // Tant qu'il y a collision (g�rer le cas "apparition next le joueur")
			x = r.nextInt(800-(int)width); // On remet un X al�atoire
		}
	}
	
	public void limitX(){ //limite la vitesse
		if (Math.abs(speedX)<speedMax)
		{
			speedX+=accelX;
		}
	}
	
	public void damageEnemy(){ //diminue les points de vie si on subit des d�g�ts
		if (Collisions.isCollisionRectRect(this, World.getPlayer()))
		{
			if (y>= World.getPlayer().getY())
			{
				hp--;
			}
		}
	}
	
	public void deathEnemy(){ //on meurt si on a plus de points de vie, supprime l'entit�
		if (hp <= 0)
		{
			i = 0;
			while (World.getEnemies().get(i).id!=id)
			{
				i++;
			}
			World.getEnemies().remove(World.getEnemies().get(i));// Enl�ve l'ennemi avec l'id "id" de la liste ennemis
		}
	}

	public void testEnemy(){ //test de la mort
		if (frameTest==1000)
		{
			hp--;
		}
	}
	
	public void backX(){ //g�re la collision sur les bords de la map
		if (x>768 || x<0)
		{
			accelX=-accelX;
			speedX=-speedX;
		}
	}
	
	

}
