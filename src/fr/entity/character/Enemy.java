package fr.entity.character;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import fr.entity.weapon.Weapon;
import fr.util.Collisions;
import fr.util.Movable;
import fr.util.Rectangle;
import fr.world.World;

public class Enemy extends Movable implements Rectangle {
	
	private double speedMax; // D�finit la vitesse max que les ennemis atteindront.
	private int hp; // Points de vie des ennemis, � 1 pour commencer.
	private static int numberEnemy = 0; // On num�rote nos ennemis pour pouvoir g�rer leur suppression via une liste.
	private int id;
	private int numberFrame = 0;
	private int i; //  Variable pour un compteur
	private int agroDistance = 120;
	private boolean gauche;
	private Image arret, marched, marcheg;
	
	public Enemy () {
	super(); // r�cup�re les attributs de la classe Movable
	hp = 1; // On donne une valeur � hp
	x = r.nextInt(800-(int)width); // On place l'ennemi � une position al�atoire sur l'axe x
	testXPlayer(); // On v�rifie qu'on ne fait pas spawn SUR le joueur (reste � g�rer les obstacles)
	y = 600-74; // On pose l'ennemi sur le sol /!\ Gestion de la collision gravit� avec les plateformes � d�bugger.
	accelX = 0.02*r.nextDouble() + 0.005; // Acc�l�ration al�atoire d�finie ici (entre 0.025 et 0.005)
	speedMax = (r.nextInt(14))/500 + 0.2; // vitesse max al�atoire d�finie ici entre 1 et 15 (ou 14)
	isMoving = true; // Un booleen au cas o� le joueur puisse arr�ter le d�placement de l'ennemi
	World.getEnemies().add(this); //On ajoute l'ennemi actuel � la liste des ennemis
	id = numberEnemy; // On donne une id � notre ennemi
	numberEnemy++; // On incr�mente le nombre d'ennemis d�j� cr��s
	try {
	arret = new Image("D:/Camille/Documents/Coding night/IMAGEEEEEE/tgd-cn-v/mechant_milieu_poignee_verso.png");
	marcheg = new Image("D:/Camille/Documents/Coding night/IMAGEEEEEE/tgd-cn-v/mechant_droite_poignee_verso.png");
	marched = new Image("D:/Camille/Documents/Coding night/IMAGEEEEEE/tgd-cn-v/mechant_gauche_poignee_verso.png");
	}catch(SlickException e){}
	}
	
	Random r = new Random(); // On se cr�e une variable pour utiliser la fonction random
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException { // gestion de l'affichage
		//g.setColor(Color.magenta); // Rectangle de test type ennemi de couleur magenta (ici on choisit la couleur du pinceau)
		//g.fillRect((float)x, (float)y, (float)width, (float)height); // On cr�� le rectangle
		g.drawImage(arret, (float)x, (float)y-16);
		/*if (gauche){
			if ((numberFrame/30)%4==0 || (numberFrame/30)%4==2){
				g.drawImage(arret, (float)x, (float)y-16);
			}
			if ((numberFrame/30)%4==1){
				g.drawImage(marcheg, (float)x, (float)y-16);
			}
			else{
				g.drawImage(marched,  (float)x,  (float)y-16);
			}
		}*/
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException { // gestion des updates � chaque frame
		accelY=0.004;
		fallHandling();
		if (speedX>=0){
			gauche = false;
		}
		else{
			gauche = true;
		}
		speedY+=accelY;
		moveX(delta);
		moveY(delta);
		limitX();
		damageEnemy();
		deathEnemy();
		backX();
		backY();
		shoot();
		initiative();
		numberFrame++;
	}
	
	public void testXPlayer(){ //permet de reset position au spawn si collision ac joueur : OK
		while (Collisions.isCollisionRectRect(this,World.getPlayer())){ // Tant qu'il y a collision (g�rer le cas "apparition next le joueur")
			x = r.nextInt(800-(int)width); // On remet un X al�atoire
		}
	}
	
	public void limitX(){ //limite la vitesse OK
		if (Math.abs(speedX)<speedMax) //Tant qu'on ne va pas trop vite
		{
			speedX+=accelX; // On acc�l�re
		}
	}
	
	public void damageEnemy(){ //diminue les points de vie si on subit des d�g�ts OK
		if (Collisions.isCollisionRectRect(this, World.getPlayer())) // Si on collisionne le player
		{
			if (y> World.getPlayer().getY()) // Si on est en dessous du player
			{
				hp--; // On perd un hp
			}
		}
		for (i = 0; i< World.getProjectiles().size(); i++)
		{
			if (Collisions.isCollisionRectRect(this,World.getProjectiles().get(i)))
			{
				hp--;
			}
		}
		if (y>600) // Si on sort de l'�cran par le bas
		{
			hp = 0; // On passe � 0 hp
			
		}
	}
	
	public void deathEnemy(){ //on meurt si on a plus de points de vie, supprime l'entit� OK
		if (hp <= 0)
		{
			i = 0;
			while (World.getEnemies().get(i).id!=id) // On parcourt la liste des ennemis
			{
				i++;
			}
			World.getEnemies().remove(World.getEnemies().get(i));// Enl�ve l'ennemi avec l'id "id" de la liste ennemis
		}
	}

	public void backX(){ //g�re la collision sur les bords de la map OK
		if (x>768 || x<0)
		{
			accelX=-accelX; // On change de direction en cas de frappage de bord de map
			speedX=-speedX; // Pour la vitesse et l'acc�l�ration
		}
		for (i = 0 ; i <= World.getPlateforms().size() - 1 ; i++){ //change de sens si collision contre plateforme
			if (Collisions.isCollisionRectRect(this,World.getPlateforms().get(i))){
				if (x+this.width<=World.getPlateforms().get(i).getX() || x>=World.getPlateforms().get(i).getX()+World.getPlateforms().get(i).getWidth()){
					speedX=-speedX;
					accelX=-accelX;
				}
			}
		}
	}
	
	public void initiative(){ //l'ennemi se dirige vers le joueur en dessous d'une certaine distance OK
		if (Math.sqrt((Math.pow(x-World.getPlayer().getX(),2)+Math.pow(y-World.getPlayer().getY(),2))) <= agroDistance){ // Si la distance en module est inf�rieure � la distance d'agro
			if (x-World.getPlayer().getX() <= 0){ // Si on est � gauche du joueur
				speedX=Math.abs(speedX); // On se dirige vers la droite
				accelX=Math.abs(accelX); // On acc�l�re vers la droite
			}
			else{//Sinon
				speedX=-Math.abs(speedX); // On se dirige vers la gauche
				accelX=-Math.abs(accelX); // On acc�l�re vers la gauche
			}
		}
	}
	
	public void fallHandling(){ //gestion de la chute sur les obstacles
		{
			/*for (i = 0 ; i <= World.getPlateforms().size()-1 ; i++){
				if(Collisions.isCollisionRectRect(this,World.getPlateforms().get(i))){
					if(World.getPlateforms().get(i).getY()+World.getPlateforms().get(i).getHeight()>y){ //collision lors d'un saut
						speedY=-speedY;
					}
					if(World.getPlateforms().get(i).getY()<y+height-7){ //collision gravit�
						accelY=0;
						y--;
						speedY=0;
					}
				}
			}*/
			for (i=0;i<=World.getPlateforms().size()-1;i++){
				if (Collisions.isCollisionRectRect(this, World.getPlateforms().get(i))){
					if (y+height-7<=World.getPlateforms().get(i).getY()-1){
						accelY=0;
						speedY=0;
					}
				}
			}
		}
	}
	
	public void shoot(){ //tire OK
		if (numberFrame%100==0){
			if(speedX>0)
			{
			   new Weapon(this.x+33,this.y+20, !gauche);
			}
			if (speedX<=0)
			{
				new Weapon(this.x-1,this.y+20,gauche);
			}
		}
	}
	
	public void backY(){ //on touche plus au sol (enfin si mais bref)
		if (y >= 600 - height - World.getPlateforms().get(0).getHeight() - 7){
			y = 600 - height - World.getPlateforms().get(0).getHeight()- 7 - 1;
		}
	}
}