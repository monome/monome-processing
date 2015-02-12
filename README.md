# monome-processing

monome-processing is a library for interfacing with monome grid and arc devices over serialosc.

## Setup

See http://monome.github.io/grid-studies-processing for guide to getting started.  Essentially you will download and unzip the following two libraries in your processing libraries folder:

monome-processing library:
https://github.com/monome/monome-processing/releases/latest

oscP5 OSC library:
http://www.sojamo.de/libraries/oscp5

## Example Grid Script

```java
import org.monome.Monome;
import oscP5.*;

Monome m;
int[][] led = new int[8][8];

public void setup() {
  m = new Monome(this, "m0012345");
}

public void run() {
  led[0][0] = 1;
  m.refresh(led);
}

public void key(int x, int y, int s) {
  System.out.println("grid key received: " + x + ", " + y + ", " + s);
}
```

## Example Arc Script

```java
import org.monome.Monome;
import oscP5.*;

Monome m;
int[] led = new int[64];

public void setup() {
  m = new Monome(this, "m0000226");
}

public void run() {
  led[0] = 15;
  m.refresh(0, led);
}

public void key(int n, int s) {
  System.out.println("arc key received: " + n + ", " + s);
}

public void delta(int n, int d) {
  System.out.println("arc delta received: " + n + ", " + s);
}
```

## API Reference

### Constructor

The Monome() constructor can be called in two ways:

```java
// without device serial uses the first detected device
Monome m = new Monome(this);
```

```java
// with device serial binds to a specific device
Monome m = new Monome(this, "m0012345");
```

### Grid API

The Monome object in grid mode has one method, refresh(int[][]), which is used to set the LED state of the grid device.  The argument should be a two-dimensional array of integers that match the size of the grid.  For example, a 128 monome would use an 8x16 array while a 64 would use an 8x8 array.

```java
Monome m = new Monome(this);
int[][] led = new int[8][16];
m.refresh(led);
```

You can define a key method to listen for key press events:

```java
public void key(int x, int y, int s) {
  // x is the x coordinate of the press event
  // y is the y coordinate of the press event
  // s is the key state (1 = pressed, 0 = released)
}
```

### Arc API

The arc version of the refresh(int, int[]) method takes two arguments: an encoder number and an array of 64 LED states.  Encoder numbers start at 0 and go up to 3 for a 4-encoder device.

```java
Monome m = new Monome(this);
int[] led = new int[64];
// refresh the first encoder with the state in led
m.refresh(0, led);
```

You can define a delta method to listen for encoder delta events:

```java
public void delta(int n, int d) {
  // n is the encoder number (0-3)
  // d is the delta value
}

You can also define a key method to listen for encoder key press events:

```java
public void key(int n, int s) {
  // n is the encoder number (0-3)
  // s is the key state (1 = pressed, 0 = released)
}
```
