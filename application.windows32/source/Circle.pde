class Circle {
  float x, y;
  float diameter;
  float vx = 2;
  float vy = 2;
  float dx;
  float dy;
  int lifeTime = 300;
  color c = color(255, 255, 255);
  
  Circle(float xin, float yin, float din, float dxin, float dyin) {
    x = xin;
    y = yin;
    dx = dxin;
    dy = dyin;
    diameter = din;
  }
  
  void move() {
    x += vx * dx;
    y += vy * dy;
    
    if(x > width || x < 0) {
      dx *= -1;
    }
    
    if(y > height - 108 || y < 82) {
      dy *= -1; 
    }
  }
  
  void setDiameter(float din) {
    diameter = din;
  }
  
  void setSpeed(float vxin, float yvin) {
    vx = vxin;
    vy = yvin;
  }
  
  int getLife() {
    return lifeTime;
  }
  
  void removeLife() {
    lifeTime--;
  }
  
  float getX() {
    return x;
  }
  
  float getY() {
    return y;
  }
  
  void randomColor(float level) {
    float r, g, b;
    
    //r = random(255);
    r = 255 * (level + .4);
    
    if(r > 255) {
      r = 255;
    }
    
    g = random(255);
    b = random(255);
    
    c = color(r, g, b);
  }
  
  void display() {
    stroke(c);
    fill(c);
    ellipse(x, y, diameter, diameter);
  }
}
