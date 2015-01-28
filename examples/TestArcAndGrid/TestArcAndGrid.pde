import org.monome.Monome;

Monome grid;
Monome arc;

int[][] gridLed;
int col;
int pos;
int[] arcLed;

public void setup() {
  grid = new Monome(this, "m256-184");
  arc = new Monome(this, "m0000226");
  arcLed = new int[64];
  col = 0;
}

public void draw() {
  gridLed = new int[8][8];
  col++;
  col = col % 8;
  for (int y = 0; y < 8; y++) {
    gridLed[y][col] = y * 2 + 1;
  }
  grid.refresh(gridLed);

  pos++;
  pos = pos % 16;
  for (int i = 0; i < 64; i++) {
    arcLed[i] = (i + pos) % 16;
  }
  arc.refresh(0, arcLed);
}

public void key(int n, int s) {
  System.out.println("arc key received: " + n + ", " + s);
}

public void key(int x, int y, int s) {
  System.out.println("grid key received: " + x + ", " + y + ", " + s);
}

public void delta(int n, int d) {
  System.out.println("delta received: " + n + ", " + d);
}
