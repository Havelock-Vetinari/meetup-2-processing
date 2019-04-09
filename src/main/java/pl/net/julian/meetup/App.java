package pl.net.julian.meetup;

import gab.opencv.OpenCV;
import processing.core.PApplet;
import processing.serial.Serial;
import processing.video.Capture;

import java.awt.*;

public class App extends PApplet {

    private String serialPortName = "/dev/cu.usbmodem143101";

    private int numFaces = 0;

    private Capture video;
    private OpenCV opencv;
    private Serial serialPort;

    public static void main(String[] args) {
        PApplet.main(App.class, args);
    }

    public void settings() {
        size(480, 320);
    }

    public void setup() {
        String[] cameras = Capture.list();
        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (String camera : cameras) {
                println(camera);
            }
        }
        video = new Capture(this, this.width, this.height, cameras[0], 30);
        opencv = new OpenCV(this, this.width, this.height);
        opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
        video.start();

        serialPort = new Serial(this, serialPortName, 9600);
    }

    public void draw() {

        frameRate(3);
        opencv.loadImage(video);
        image(video, 0, 0);
        noFill();
        stroke(0, 255, 0);
        strokeWeight(3);
        Rectangle[] faces = opencv.detect();

        if (faces.length > 0) {
            for (Rectangle face : faces) {
                rect(face.x, face.y, face.width, face.height);
            }
        }
        if (numFaces != faces.length) {
            numFaces = faces.length;
            serialPort.write(constrain(numFaces, 0, 100));
        }
        noFill();
    }

    public void captureEvent(Capture c) {
        c.read();
    }
}
