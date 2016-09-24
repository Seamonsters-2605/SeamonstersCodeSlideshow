import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.nio.file.Files; 
import javax.swing.JOptionPane; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SeamonstersCodeSlideshow extends PApplet {





// Config
boolean useOpenGL = true;
float scrollSpeed = 125.0f;
float codeFontSize = 28;
float codeFontLeading = 30;
int minLinesOnScreen = 32;
float locFontSize = 48;
float titleFontSize = 112;



boolean loaded = false;

List<String> codeFiles;
int linesOfCode = 0;
String currentFile = null;
String currentTypedFile = "";
int currentFileLines = 0;
boolean fading;
float fade = 255;
float scroll = 0;
PImage backgroundImage;
PImage overlayImage;
String message;
PFont codeFont;
PFont titleFont;

public void settings() {
  if(useOpenGL) {
    size(displayWidth, displayHeight, P2D);
  } else {
    fullScreen();
  }
}

public void setup() {
  codeFont = loadFont("codeFont.vlw");
  titleFont = loadFont("title.vlw");
  
  backgroundImage = loadImage("background.png");
  PGraphics backgroundTint = createGraphics(backgroundImage.width, backgroundImage.height);
  backgroundTint.beginDraw();
  backgroundTint.tint(127);
  backgroundTint.image(backgroundImage, 0, 0);
  backgroundTint.endDraw();
  backgroundImage = backgroundTint;
  
  overlayImage = loadImage("CTRLines.png");
  PGraphics overlayScale = createGraphics(width, height);
  overlayScale.beginDraw();
  overlayScale.tint(255, 192);
  overlayScale.image(overlayImage, 0, 0, width, height);
  overlayScale.endDraw();
  overlayImage = overlayScale;
  
  selectFolder("Folder to search?", "folderSelected");
}

public void draw() {
  if(!loaded)
    return;
  
  if(currentFile == null) {
    fade = 255;
    fading = false;
    currentFile = randomListItem(codeFiles);
    currentFileLines = countLines(currentFile);
    // make sure there is enough material on screen
    while(currentFileLines < minLinesOnScreen) {
      currentFile += "\n\n\n" + randomListItem(codeFiles);
      currentFileLines = countLines(currentFile);
    }
    scroll = -height/2;
  }
  
  background(0);
  image(backgroundImage, width/2 - backgroundImage.width/2, height/2 - backgroundImage.height/2);
  noTint();
  
  textFont(codeFont, codeFontSize);
  fill(0, 255, 0, fade);
  textAlign(LEFT, TOP);
  textSize(codeFontSize);
  textLeading(codeFontLeading);
  text(currentFile, 0, (int)(-scroll));
  scroll += scrollSpeed / (float)frameRate;
  
  if(fading) {
    if(fade <= 0)
      currentFile = null;
    fade -= scrollSpeed / (float)frameRate;
  } else if (scroll > 0 && scroll > (float)currentFileLines*codeFontLeading - height) {
    fading = true;
  }
  
  fill(0, 0, 0);
  rect(0, 0, width, titleFontSize + 4);
  textFont(titleFont, titleFontSize);
  
  fill(0, 255, 0);
  textAlign(RIGHT, TOP);
  textSize(locFontSize);
  text(linesOfCode + " lines of code.", width, 2);
  
  textAlign(LEFT, TOP);
  textSize(titleFontSize);
  text(message, 0, 2);
  
  image(overlayImage, 0, 0);
}

public void keyPressed() {
  if(key == ' ')
    currentFile = null;
}

<E> E randomListItem(List<E> list) {
  return list.get(floor(random(list.size())));
}

public int countLines(String text) {
  return text.split("\r\n|\r|\n").length;
}


public void folderSelected(File f) {
  if(f == null) {
    exit();
  } else {
    File codeRoot = f.toPath().toFile();
    
    //String fileExtension = JOptionPane.showInputDialog(frame, "File extension to search for (like 'py')");
    //if(fileExtension == null)
    //  exit();
    message = JOptionPane.showInputDialog(frame, "Message to display at top of screen (along with line count)");
    
    codeFiles = new ArrayList<String>();
    getFilesRecursive(codeRoot, "py");
    if(codeFiles.size() == 0) {
      JOptionPane.showMessageDialog(frame, "No files found.");
      exit();
    }
    
    loaded = true;
  }
}

public void getFilesRecursive(File f, String extension) {
  if(f.isDirectory()) {
    for(File child : f.listFiles())
      getFilesRecursive(child, extension);
  } else {
    if((!extension.isEmpty()) && !f.getName().endsWith("." + extension))
      return;
    println(f.getName());
    List<String> strings;
    try {
      strings = Files.readAllLines(f.toPath());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    String contents = "";
    for(String s : strings) {
      if(!s.trim().isEmpty())
        linesOfCode++;
      s = s.replace("\t", "    ");
      contents += s + "\n";
    }
    codeFiles.add(contents);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SeamonstersCodeSlideshow" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
