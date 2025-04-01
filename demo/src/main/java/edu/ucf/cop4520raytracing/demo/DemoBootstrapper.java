package edu.ucf.cop4520raytracing.demo;

import edu.ucf.cop4520raytracing.core.Raytracer;
import edu.ucf.cop4520raytracing.core.Scene;

import java.util.Scanner;

public class DemoBootstrapper {
    public static void main(String[] args) {
        @SuppressWarnings("resource")
        var raytracer = new Raytracer();

        raytracer.initDefaultJFrame();
        raytracer.setScene(Scene.DEFAULT);
        raytracer.start();
        
//        // read
//        Scanner input = new Scanner(System.in);
//        while (true) {
//            String next = input.nextLine();
//            if ()
//        }
    }
}
