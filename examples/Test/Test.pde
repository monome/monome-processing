import org.monome.Monome;

public class Test extends PApplet {
  
  Monome m;
  int[][] led;
  
  public void setup() {
    m = new Monome(this);
    led = new int[8][8];
  }
  
  public void draw() {
    led[1][2] = 15;
    m.refresh(led);
  }
  
  public void press(int x, int y, int s) {
    System.out.println("press received: " + x + ", " + y + ", " + s);
  }
  
}

