void display() {
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

void gameOver(String displayText){
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
      text(displayText, width/2+x, height/2*0.9);
      text(displayText, width/2, height/2*0.9+x);
      textSize(height/20);
      text("Click to play again", width/2+x, height/2+height/20);
      text("Click to play again", width/2, height/2+height/20+x);
  }
  textSize(height/10);
  fill(255,100,150);
  text(displayText, width/2, height/2*0.9);
  textSize(height/20);
  text("Click to play again", width/2, height/2+height/20);
}
