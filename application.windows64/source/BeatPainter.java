import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BeatPainter extends PApplet {

/*
Processing Music Painter
By Will Brewer

Instructions:
Use dropdown lists to select song and effect
Press play effect button to play and pause
Use left-side checkboxes to switch between mic input and pre-loaded songs
Clear button clears the drawing area
*/





//variables
int beatCount;
int input = 0;
int drawCount = 0;
boolean isPlaid = false;
boolean isDots = false;
boolean isPlaidDots = false;
boolean isClear = false;
boolean isBeat = false;
boolean isSong = false;
boolean isColor = false;
boolean isSize = false;
boolean isSpeed = false;
ArrayList<Circle> circles;

//Audio Objects
Minim minim;
AudioPlayer song;
AudioInput in;
BeatDetect beat;
FFT fft;

//GUI Elements
ControlP5 dropP5;
ControlP5 cP5;
DropdownList dropSong;
DropdownList dropEffect;
RadioButton radioButton;
Button playSong;
Button playEffect;
Button clearScreen;
Button variableSize;
Button variableSpeed;
Textlabel title;

//performs initial setup for frame
public void setup() {
  size(512, 550);
  
  createGUI();
  setupAudio();
  
  circles = new ArrayList<Circle>();
  background(0);
}

//creates and draws gui components to the screen
public void createGUI() {
  cP5 = new ControlP5(this);
  dropP5 = new ControlP5(this);
  
  dropP5.setAutoDraw(false);
  cP5.setAutoDraw(false);
  
  radioButton = cP5.addRadioButton("radioButton")
                 .setPosition(15, 10)
                 .setSize(15, 15)
                 .setColorForeground(color(120))
                 .setColorActive(color(255))
                 .setColorLabel(color(255))
                 .setItemsPerRow(1)
                 .setSpacingRow(20)
                 .addItem("Mic", 1)
                 .addItem("Song", 2);
          
  dropSong = dropP5.addDropdownList("Choose Song")
                   .setPosition(100, 5)
                   .setBackgroundColor(color(190))
                   .setItemHeight(15)
                   .setBarHeight(15)
                   .setOpen(false)
                   .setColorBackground(color(60))
                   .setColorActive(color(255, 128))
                   .addItem("Tame Impala", 0)
                   .addItem("Bee Gees", 1)
                   .addItem("Data", 2)
                   .setHeight(60);

  dropEffect = dropP5.addDropdownList("Choose Effect")
                   .setPosition(250, 5)
                   .setBackgroundColor(color(190))
                   .setItemHeight(15)
                   .setBarHeight(15)
                   .setOpen(false)
                   .setColorBackground(color(60))
                   .setColorActive(color(255, 128))
                   .addItem("Plaid", 0)
                   .addItem("Dots", 1)
                   .addItem("Plaid Dots", 2)
                   .setHeight(60);
                
  clearScreen = cP5.addButton("Clear")
                   .setBroadcast(false)
                   .setValue(4)
                   .setPosition(400, 5)
                   .setSize(100, 25)
                   .setId(3)
                   .setBroadcast(true);
                   
  variableSize = cP5.addButton("Size")
                    .setBroadcast(false)
                    .setSwitch(true)
                    .setValue(4)
                    .setPosition(400, 35)
                    .setSize(48, 25)
                    .setBroadcast(true);
                    
  variableSpeed = cP5.addButton("Speed")
                    .setBroadcast(false)
                    .setSwitch(true)
                    .setValue(4)
                    .setPosition(452, 35)
                    .setSize(48, 25)
                    .setBroadcast(true);
                    
                   
  PFont font = loadFont("HarlowSolid-38.vlw"); 
  title = cP5.addTextlabel("title")
             .setText("Beat Painter")
             .setPosition(115, 25)
             .setColorValue(color(255))
             .setFont(font);
}

//sets up audio objects and loads mic
public void setupAudio() {
  minim = new Minim(this);
  in = minim.getLineIn(Minim.STEREO, 512);
  //in = minim.getLineIn();
  beat = new BeatDetect();
}

//draws components to the screen
public void draw() {
  refresh();

  detectBeat();
  drawScreen();
  
  //resets the beat boolean
  isBeat = false;
  drawCount++;
}

//refeshes the screen
public void refresh() {
 //refresh drawing area
  if(isClear || isDots) {
    background(0);
    isClear = false;
  }
  
  //refreshes control and waveform area
  fill(0);
  stroke(0);
  strokeWeight(1);
  rect(0, 0, width, 75);
  rect(0, 448, width, 110);
}

//detects the beat and drives the visuals
public void detectBeat() {
  try {
    if(input == 1) {
      beat.detect(in.mix);
      println(in.mix.level() + .3f);
    } else if(input == 2) {
      beat.detect(song.mix);
      println(song.mix.level() + .3f);
    }
  } catch(NullPointerException e) {
    //empty catch
  }
  
  if(beat.isOnset() && frameCount > 10) {
    isBeat = true;
    frameCount = 0;
    
    title.setColor(color(255, 0, 0, 128));
    drawCount = 0;
    
    for(int i = 0; i < 10; i++) {
      createCircle();
    }
  }
  
  if(drawCount == 10) {
    title.setColor(color(255));
  }
}

//draws the components on the screen
public void drawScreen() {
 //draw balls, beat line, and waveform
  for(int i = 0; i < 512 - 1; i++) { //waveform
    if(isBeat) {
      stroke(255, 0, 0, 128);
      strokeWeight(5);
    } else {
      stroke(255);
    }
    
    if(input == 1) {
      line(i, 500 + in.mix.get(i)*50, i+1, 500 + in.mix.get(i+1)*50);
    } else if(input == 2) {
      try {
        line(i, 500 + song.mix.get(i)*50, i+1, 500 + song.mix.get(i+1)*50);
      } catch(NullPointerException e) {
        line(i, 500, i+1, 500);
      }
    } else {
      line(i, 500, i+1, 500);
    }
  }
  
  for(int i = 0; i < circles.size() - 1; i++) { //balls
    if(isPlaidDots || isPlaid || isDots) {
      circles.get(i).display();
      circles.get(i).move();
      circles.get(i).removeLife();
    }
    
    if(circles.get(i).getLife() == 0) {
      circles.remove(i);
    }
    
    if(isBeat && (isPlaidDots || isDots)) {
      circles.get(i).setDiameter(40.0f);
      
      try {
        if(input == 1.0f) {
          circles.get(i).randomColor(in.mix.level());
        } else if(input == 2.0f) {
          circles.get(i).randomColor(song.mix.level());
        }
      } catch(NullPointerException e) {
        //empty catch
      }
      
    } else if(isBeat) {
      try {
        if(input == 1.0f) {
          circles.get(i).randomColor(in.mix.level());
        } else if(input == 2.0f) {
          circles.get(i).randomColor(song.mix.level());
        }
      } catch(NullPointerException e) {
        //empty catch
      }
      
    } else {
      circles.get(i).setDiameter(10.0f);
    }
    
    try {
      if(isSize && input == 1) {
        circles.get(i).setDiameter(in.mix.level() * 100);
      } else if(isSize && input == 2) {
        circles.get(i).setDiameter(song.mix.level() * 100);
      }
    } catch(NullPointerException e) {
      //empty catch
    }
    
    if(isBeat) { 
      try {
        if(isSpeed && input == 1) {
          circles.get(i).setSpeed(in.left.level() * 10 + 20, in.right.level() * 10 + 20);
        } else if(isSpeed && input == 2) {
          circles.get(i).setSpeed(song.left.level() * 10 + 20, song.right.level() * 10 + 20);
        }
      } catch(NullPointerException e) {
        //empty catch
      }
    } else {
      try {
        if(isSpeed && input == 1) {
          circles.get(i).setSpeed(in.left.level() * 10, in.right.level() * 10);
        } else if(isSpeed && input == 2) {
          circles.get(i).setSpeed(song.left.level() * 10, song.right.level() * 10);
        }
      } catch(NullPointerException e) {
         //empty catch 
      }
    }
    
    if(circles.get(i).getY() > 450 || circles.get(i).getY() < 73) {
      circles.remove(i);
    }
  }
  
  stroke(255);
  line(0, 73, 512, 73);
  line(0, 450, 512, 450);
  
  cP5.draw();
  dropP5.draw(); 
}

//creates a circle based on mic or song input
public void createCircle() {
  float ranDirection = random(4);
  float randomX = random(width);
  float randomY = random(83, height - 120);
  Circle temp;
  float x, y;
  
  if(isPlaid) {
    if(ranDirection <= 1) {
      circles.add(new Circle(1, randomY, 5, 1, 1));
    } else if(ranDirection <= 2) {
      circles.add(new Circle(1, randomY, 5, -1, 1));
    } else if(ranDirection <= 3) {
      circles.add(new Circle(1, randomY, 5, 1, -1));
    } else {
      circles.add(new Circle(1, randomY, 5, -1, -1));
    }
  } else {
    if(ranDirection <= 1) {
      circles.add(new Circle(randomX, randomY, 5, 1, 1));
    } else if(ranDirection <= 2) {
      circles.add(new Circle(randomX, randomY, 5, -1, 1));
    } else if(ranDirection <= 3) {
      circles.add(new Circle(randomX, randomY, 5, 1, -1));
    } else {
      circles.add(new Circle(randomX, randomY, 5, -1, -1));
    }
  }   
}

//detects GUI button presses
public void controlEvent(ControlEvent theEvent) throws IndexOutOfBoundsException {
  if(theEvent.isGroup()) {
    if(theEvent.group().getName() == "radioButton") {
      if(theEvent.group().getValue() == 1.0f) {
        if(isSong) {
          song.close();
        }
        
        input = 1;
      } else if(theEvent.group().getValue() == 2.0f) {
        input = 2;
      }
    }
    
  } else if(theEvent.isController()) {
    if(theEvent.controller().getName() == "Choose Song") {
      println("choose song activated");
      if(theEvent.controller().getValue() == 0.0f) {
        if(isSong) {
          song.close();
        }
        
        isSong = true;
        song = minim.loadFile("tameImpala.mp3", 512);
        song.play();
       
      } else if(theEvent.controller().getValue() == 1.0f) {
        if(isSong) {
          song.close();
        }
        
        isSong = true;
        song = minim.loadFile("beeGees.mp3", 512);
        song.play();
      
      } else if(theEvent.controller().getValue() == 2.0f) {
        if(isSong) {
          song.close();
        }
        
        isSong = true;
        song = minim.loadFile("dontSing.mp3", 512);
        song.play();
      }  
    }
    
    if(theEvent.controller().getName() == "Choose Effect") {
      println("choose effect activated");
      if(theEvent.controller().getValue() == 0.0f) {
        isPlaid = true;
        isDots = false;
        isPlaidDots = false;
      }
      
      if(theEvent.controller().getValue() == 1.0f) {     
        isPlaid = false;
        isDots = true;
        isPlaidDots = false;       
      }
      
      if(theEvent.controller().getValue() == 2.0f) {
        isPlaid = false;
        isDots = false;
        isPlaidDots = true;
      }   
    }
    
    if(theEvent.controller().getName() == "Size") {
      isSize = !isSize;
    }
    
    if(theEvent.controller().getName() == "Speed") {
      isSpeed = !isSpeed;
    }
    
    if(theEvent.controller().getName() == "Clear") {
      println("clear activated");
      isClear = true;
    }
  }
}

//maitenance calculations before program closes
public void stop() {
  song.close();
  in.close();
  minim.stop();
  super.stop();
}
class Circle {
  float x, y;
  float diameter;
  float vx = 2;
  float vy = 2;
  float dx;
  float dy;
  int lifeTime = 300;
  int c = color(255, 255, 255);
  
  Circle(float xin, float yin, float din, float dxin, float dyin) {
    x = xin;
    y = yin;
    dx = dxin;
    dy = dyin;
    diameter = din;
  }
  
  public void move() {
    x += vx * dx;
    y += vy * dy;
    
    if(x > width || x < 0) {
      dx *= -1;
    }
    
    if(y > height - 108 || y < 82) {
      dy *= -1; 
    }
  }
  
  public void setDiameter(float din) {
    diameter = din;
  }
  
  public void setSpeed(float vxin, float yvin) {
    vx = vxin;
    vy = yvin;
  }
  
  public int getLife() {
    return lifeTime;
  }
  
  public void removeLife() {
    lifeTime--;
  }
  
  public float getX() {
    return x;
  }
  
  public float getY() {
    return y;
  }
  
  public void randomColor(float level) {
    float r, g, b;
    
    //r = random(255);
    r = 255 * (level + .4f);
    
    if(r > 255) {
      r = 255;
    }
    
    g = random(255);
    b = random(255);
    
    c = color(r, g, b);
  }
  
  public void display() {
    stroke(c);
    fill(c);
    ellipse(x, y, diameter, diameter);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BeatPainter" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
