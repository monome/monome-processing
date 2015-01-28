import org.monome.Monome;

Monome m;
int[][] led;
int col = 0;
  
public void setup() {
  m = new Monome(this);
  led = new int[8][8];
}
  
public void draw() {
  led = new int[8][8];
  col++;
  col = col % 8;
  for (int y = 0; y < 8; y++) {
    led[y][col] = y * 2 + 1;
  }
  m.refresh(led);
}
  
public void key(int x, int y, int s) {
  System.out.println("key received: " + x + ", " + y + ", " + s);
}

