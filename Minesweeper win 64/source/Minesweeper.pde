int squares;
boolean[][] mines;
boolean[][] flagged;
boolean[][] uncovered;
int[][] surrounding;
int mineAmount;
int scale;
PImage bomb, flag;
boolean play = true;
void setup() {
  size(600, 600);
  
  // Load config file
  String[] config = loadStrings("config.txt");
  squares = int(config[1]);
  mines = new boolean[squares][squares];
  flagged = new boolean[squares][squares];
  uncovered = new boolean[squares][squares];
  surrounding = new int[squares][squares];
  mineAmount = int(config[3]);
  
  surface.setTitle("Minesweeper - " + str(mineAmount) + " mines");
  scale = width/squares;
  textAlign(CENTER, CENTER);
  textSize(scale/2);
  imageMode(CENTER);
  bomb = loadImage("pictures/bomb.png");
  flag = loadImage("pictures/flag.png");
  surface.setIcon(flag);
  bomb.resize(int(scale*0.75), int(scale*0.75));
  flag.resize(int(scale*0.75), int(scale*0.75));
  generate();
  display();
}
void draw() {
}

void mouseClicked() {
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

void generate() {
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
int surround(int x, int y) {
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

boolean[][] uncover(int x, int y) {
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
