import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper extends PApplet {

int squares;
boolean[][] mines;
boolean[][] flagged;
boolean[][] uncovered;
int[][] surrounding;
int mineAmount;
int scale;
PImage bomb, flag;
boolean play = true;
public void setup() {
  
  
  // Load config file
  String[] config = loadStrings("config.txt");
  squares = PApplet.parseInt(config[1]);
  mines = new boolean[squares][squares];
  flagged = new boolean[squares][squares];
  uncovered = new boolean[squares][squares];
  surrounding = new int[squares][squares];
  mineAmount = PApplet.parseInt(config[3]);
  
  surface.setTitle("Minesweeper - " + str(mineAmount) + " mines");
  scale = width/squares;
  textAlign(CENTER, CENTER);
  textSize(scale/2);
  imageMode(CENTER);
  bomb = loadImage("pictures/bomb.png");
  flag = loadImage("pictures/flag.png");
  surface.setIcon(flag);
  bomb.resize(PApplet.parseInt(scale*0.75f), PApplet.parseInt(scale*0.75f));
  flag.resize(PApplet.parseInt(scale*0.75f), PApplet.parseInt(scale*0.75f));
  generate();
  display();
}
public void draw() {
}

public void mouseClicked() {
  // Uncover tile etc.
  if (play) {
    // Register what tile was clicked
    int xClicked = round(mouseX/scale);
    int yClicked = round(mouseY/scale);
    // If shift pressed flag the tile
    if (keyPressed) {
      if (keyCode==SHIFT) {
        // Toggle flagged
        if (!flagged[xClicked][yClicked]) {
          flagged[xClicked][yClicked] = true;
        } else if (flagged[xClicked][yClicked]) {
          uncovered[xClicked][yClicked] = false;
          flagged[xClicked][yClicked] = false;
        }
      }
      
    // If shift not pressed uncover tile
    } else {
      uncovered = uncover(xClicked, yClicked);
      // If uncovered tile is bomb, game over
      if (mines[xClicked][yClicked] == true) {
        play = false;
      }
    }
    display();
    if (play == false) {
      gameOver("GAME OVER");
    }
    // LOGICAL AND flagged and mines to add one to correct if both are true
    int correct = 0;
    for(int i = 0; i < squares; i++){
      for(int j = 0; j < squares; j++){
        // If tile is correctly identified
        if((flagged[i][j] && mines[i][j]) || (!flagged[i][j] && !mines[i][j])){
          correct += 1;
        }
      }
    }
    // If there are as many correct as mines AND not correct as free spaces, you win
    if(correct == squares*squares){
      gameOver("You Won");
    }
  // Restart game if pressed
  } else {
    play = true;
    generate();
    display();
  }
}

public void generate() {
  // Set every spot to be 0
  for (int i = 0; i < mines.length; i++) {
    for (int j = 0; j < mines.length; j++) {
      mines[i][j] = false;
      flagged[i][j] = false;
      uncovered[i][j] = false;
    }
  }
  // Generate mines
  for (int i = 0; i < mineAmount; i++) {
    int tryX = round(random(0, mines.length-1));
    int tryY = round(random(0, mines.length-1));
    if (mines[tryX][tryY] == false) {
      mines[tryX][tryY] = true;
    }
  }
  // Calculate surrounding mines
  for (int i = 0; i < mines.length; i++) {
    for (int j = 0; j < mines.length; j++) {
      surrounding[i][j] = surround(i, j);
    }
  }
}

// DOESNT CHECK ON THE SIDES
public int surround(int x, int y) {
  //Create var to store amount
  int surround = 0;
  // Go through 9 surrounding squares
  for (int i = x-1; i <= x+1; i++) {
    for (int j = y-1; j <= y+1; j++) {
      if (i >= 0 && i <= squares-1) {
        if (j >= 0 && j <= squares-1) {
          // Add up mines
          if (mines[i][j] == true) {
            surround += 1;
          }
        }
      }
    }
  }
  // Set to -1 if is mine
  if (mines[x][y] == true) {
    surround = -1;
  }
  return surround;
}

public boolean[][] uncover(int x, int y) {
  if (x<0 || y < 0 || x > squares-1 || y > squares-1) {
    return uncovered;
  }
  // Save that tile is uncovered
  if (!uncovered[x][y]) {
    uncovered[x][y] = true;
    // Uncover surrounding tiles
    if (surround(x, y) == 0) {
      uncover(x-1, y);
      uncover(x+1, y);
      uncover(x, y-1);
      uncover(x, y+1);
    }
  }
  return uncovered;
}
public void display() {
  // Go through every cell
  for(int i = 0; i < squares; i++){
      for(int j = 0; j < squares; j++){
        if(uncovered[i][j]){ //uncovered[i][j]
          // White out uncovered cells
          fill(255);
          rect(i*scale, j*scale, scale, scale);
          if(surrounding[i][j] != 0 && surround(i, j) != -1){
            // Display how many surrounding bombs
            textSize(scale/2);
            switch(surrounding[i][j]){
              case 1:
                fill(255, 100, 150);
                break;
              case 2:
                fill(255, 150, 100);
                break;
              case 3:
                fill(150, 100, 255);
                break;
              case 4:
                fill(100, 50, 150);
                break;
              case 5:
                fill(100, 100, 100);
                break;
              case 6:
                fill(50, 100, 100);
                break;
              case 7:
                fill(100, 50, 100);
                break;
              case 8:
                fill(100, 100, 50);
                break;
            }
            text(surrounding[i][j], i*scale+scale/2, j*scale+scale/2);
          }
          if(surround(i, j) == -1){
            // Display bomb
            fill(255,0,0);
            rect(i*scale, j*scale, scale, scale);
            image(bomb, i*scale+scale/2, j*scale+scale/2);
          }
        } else if(flagged[i][j]){
          // Flag flagged cells thats flagged
          image(flag, i*scale+scale/2, j*scale+scale/2);
        } else {
          // Grey out covered cells
          fill(220);
          rect(i*scale, j*scale, scale, scale);
        }
      }
  }
}

public void gameOver(String displayText){
  // Show game over text and reveal the bombs
  play = false;
  for(int i = 0; i < squares; i++){
      for(int j = 0; j < squares; j++){
        if(mines[i][j] == true){
          fill(255,0,0);
          rect(i*scale, j*scale, scale, scale);
          image(bomb, i*scale+scale/2, j*scale+scale/2);
        }
      }
  }
  
  fill(0,0,0);
  for(int x = -2; x < 3; x++){
      textSize(height/10);
      text(displayText, width/2+x, height/2*0.9f);
      text(displayText, width/2, height/2*0.9f+x);
      textSize(height/20);
      text("Click to play again", width/2+x, height/2+height/20);
      text("Click to play again", width/2, height/2+height/20+x);
  }
  textSize(height/10);
  fill(255,100,150);
  text(displayText, width/2, height/2*0.9f);
  textSize(height/20);
  text("Click to play again", width/2, height/2+height/20);
}
  public void settings() {  size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
