package edu.ucf.cop4520raytracing.demo;

import edu.ucf.cop4520raytracing.core.Raytracer;
import edu.ucf.cop4520raytracing.core.Scene;

import javax.swing.JFrame;

public class DemoBootstrapper {

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        var raytracer = new Raytracer();
        
        raytracer.initJFrame();
        raytracer.setScene(Scene.DEFAULT);
        raytracer.start();
    }
}
