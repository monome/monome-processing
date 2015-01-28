import org.monome.Monome;

Monome m;
int[] led;
int pos;
  
public void setup() {
  m = new Monome(this, "m0000226");
  led = new int[64];
  pos = 0;
}
  
public void draw() {
  pos++;
  pos = pos % 16;
  for (int i = 0; i < 64; i++) {
    int l = (i + pos) % 16;
    led[i] = l;
  }
  m.refresh(0, led);
}
  
public void key(int n, int s) {
  System.out.println("key received: " + n + ", " + s);
}
  
public void delta(int n, int d) {
  System.out.println("delta received: " + n + ", " + d);
}
