package edu.ucf.cop4520raytracing.demo;

import edu.ucf.cop4520raytracing.core.Raytracer;
import edu.ucf.cop4520raytracing.core.Scene;

import java.util.Arrays;
import java.util.NoSuchElementException;
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
        Thread commandListener = Thread.startVirtualThread(() -> {
            var input = new Scanner(System.in);
            cmdloop:
            while (true) {
                String s = input.nextLine();
                var commands = Arrays.stream(s.split(" ")).map(String::trim).iterator();

                try {
                    switch (commands.next()) {

                        case "debug" -> {
                            switch (commands.next()) {
                                case "camera" -> {
                                    var cam = raytracer.getCameraController().getActiveCamera();
                                    switch (commands.next()) {
                                        case "yaw" -> System.out.println(cam.getYaw());
                                        case "pitch" -> System.out.println(cam.getPitch());
//                                        case "facing" -> System.out.println(raytracer.getCameraController()
//                                                                                 .getActiveCamera()
//                                                                                 .getFacing());
                                        case "pos" -> System.out.println(cam.getPosition());
                                    }
                                }
                                case "scene" -> {
                                    switch (commands.next()) {
                                        case "solids" -> System.out.println(raytracer.getScene().getSolids());
                                        case "lights" -> System.out.println(raytracer.getScene().getLights());
                                    }
                                }
                            }
                        }
                        case "exit" -> {
                            raytracer.close();
                            System.exit(0);
                            break cmdloop;
                        }
                    }
                } catch (NoSuchElementException ignored) {
                }
            }
        });
    }

}
