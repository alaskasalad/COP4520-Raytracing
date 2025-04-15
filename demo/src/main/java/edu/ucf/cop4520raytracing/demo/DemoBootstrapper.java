package edu.ucf.cop4520raytracing.demo;

import edu.ucf.cop4520raytracing.core.Camera;
import edu.ucf.cop4520raytracing.core.Raytracer;
import edu.ucf.cop4520raytracing.core.Scene;
import edu.ucf.cop4520raytracing.core.light.DirectionalLight;
import edu.ucf.cop4520raytracing.core.light.Light;
import edu.ucf.cop4520raytracing.core.solid.Plane;
import edu.ucf.cop4520raytracing.core.solid.Solid;
import edu.ucf.cop4520raytracing.core.solid.Sphere;
import org.joml.Vector3d;

import java.awt.Color;
import java.util.*;
import java.util.function.IntPredicate;

public class DemoBootstrapper {
    static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        final var raytracer = new Raytracer();

        raytracer.initDefaultJFrame();
        raytracer.setScene(Scene.DEFAULT);
        raytracer.start();

//        Thread fpsTracker = Thread.startVirtualThread(() -> {
//            try {
//                while (!Thread.interrupted()) {
//                    Thread.sleep(1000);
//                    System.out.println("FPS: " + raytracer.numFramesCompletedPerTimeIncrement.get());
//                    raytracer.numFramesCompletedPerTimeIncrement.set(0);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });

        Thread commandListener = Thread.startVirtualThread(() -> {
            cmdloop:
            while (true) {
                if (!input.hasNext()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    String s = input.nextLine();
                    var commands = Arrays.stream(s.split(" ")).map(String::trim).iterator();

                    String next = commands.next();
                    switch (next) {
                        case "debug" -> onDebugCommand(raytracer, commands);
                        case "scene" -> onSceneCommand(raytracer, commands);
                        case "camera" -> onCameraCommand(raytracer, commands);
                        case "exit" -> {
                            raytracer.close();
                            System.exit(0);
                            break cmdloop; // should be unreachable
                        }
                        default -> System.out.println("Commands: debug, scene");
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private static void onCameraCommand(Raytracer raytracer, Iterator<String> commands) {
        switch (commands.next()) {
            case "new" -> {
                var active = raytracer.getCameraController().getActiveCamera();
                var id = raytracer.getCameraController().addCamera(Camera.builder().position(active.getPosition()).pitch(active.getPitch()).yaw(active.getYaw()).build());
                raytracer.getCameraController().setActiveCamera(id);
            }
            case "active" -> {
                var id = Integer.parseInt(commands.next());
                if (raytracer.getCameraController().setActiveCamera(id)) {
                    System.out.println("Switched to camera " + id);
                } else {
                    System.out.println("Camera not found: " + id);
                }
            }
            case "list" -> {
                var toPrint = raytracer.getCameraController().getCameras();
                for (int i = 0; i < toPrint.size(); i++) {
                    Object el = toPrint.get(i);
                    String repr = el.toString();
                    System.out.println(i + ": " + repr);
                }
            }
        }
    }


    //region Scene Commands
    static void onSceneCommand(Raytracer raytracer, Iterator<String> commands) {
        Scene scene = raytracer.getScene();
        switch (commands.next()) {
            case "add" -> {
                switch (commands.next()) {
                    case "solid" -> scene$addSolid(scene, commands);
                    case "light" -> scene$addLight(scene, commands);
                }
            }
            case "remove" -> scene$removeItem(scene, commands);
            case "list" -> scene$listItems(scene, commands);
            default -> System.out.println("Usage: scene [add|remove|list]");
        }
    }

    //region Scene Subcommands

    //region Add Item
    static void scene$addSolid(Scene scene, Iterator<String> commands) {
        if (!commands.hasNext()) { // print usage if no args given
            System.out.println("Usage: 'scene solid [plane|sphere] [pos]'");
            return;
        }

        Solid toAdd = switch (commands.next()) {
            case "sphere" -> {
                try {
                    // get pos
                    var pos = parsePosArg(commands);
                    // get radius
                    var radius = Double.parseDouble(commands.next());
                    Color color = (commands.hasNext()) ? parseColor(commands.next()) : Color.BLUE;
                    yield new Sphere(pos, radius, color);
                } catch (Exception e) {
                    System.out.println("Usage: 'scene solid sphere [pos] [radius] (color)");
                    System.out.println("Example: 'scene solid sphere 0 0 0 1.0 blue'");
                    yield null;
                }
            }
            case "plane" -> {
                try {
                    // get pos
                    var pos = parsePosArg(commands);
                    // get radius
                    var normal = parsePosArg(commands);
                    Color color = (commands.hasNext()) ? parseColor(commands.next()) : Color.BLUE;
                    yield new Plane(pos, normal, color);
                } catch (Exception e) {
                    System.out.println("Usage: 'scene solid plane [pos] [normal] (color)");
                    System.out.println("Example: 'scene solid plane 0 0 0 0 1 0 blue'");
                    yield null;
                }
            }
            default -> {
                System.out.println("Usage: 'scene [solid|plane]");
                yield null;
            }
        };

        if (toAdd == null) {
            return;
        }

        int id = scene.addSolid(toAdd);
        System.out.println("Added solid with id " + id);
    }

    static void scene$addLight(Scene scene, Iterator<String> commands) {
        if (!commands.hasNext()) { // print usage if no args given
            System.out.println("Usage: 'scene light [direction] [intensity] (color)");
            System.out.println("Example: 'scene light 1 0 0 0.5 white'");
            return;
        }

        Light light = null;
        try {
            // get direction
            var direction = parsePosArg(commands);
            // get intensity
            var intensity = Double.parseDouble(commands.next());
            Color color = (commands.hasNext()) ? parseColor(commands.next()) : Color.WHITE;

            light = new DirectionalLight(direction, color, intensity);
        } catch (Exception e) {
            System.out.println("Usage: 'scene light [direction] [intensity] (color)");
            System.out.println("Example: 'scene light 1 0 0 0.5 white'");
            System.out.println("\nCaused by exception: " + e.getMessage());
        }

        if (light == null)
            return;

        int id = scene.addLight(light);
        System.out.println("Added light with id " + id);
    }
    //endregion

    static void scene$removeItem(Scene scene, Iterator<String> commands) {
        final String usage = "Usage: 'scene remove [solid|light] [id]'\nExample: 'scene remove light 0'";
        if (!commands.hasNext()) {
            System.out.println(usage);
            return;
        }

        try {
            String next = commands.next();
            IntPredicate action = switch (next) {
                case "solid" -> scene::removeSolid;
                case "light" -> scene::removeLight;
                default -> throw new IllegalArgumentException("Bad argument \"" + next + "\". Options: solid, light");
            };
            int id = Integer.parseInt(commands.next());
            if (!action.test(id)) {
                System.out.println("Could not find " + next + " with id " + id);
            }
        } catch (Exception e) {
            System.out.println(usage);
            System.out.println("Problem: " + e.getMessage());
        }
    }

    static void scene$listItems(Scene scene, Iterator<String> commands) {
        final String usage = "Usage: 'scene list [solids|lights]'";
        if (!commands.hasNext()) {
            System.out.println(usage);
            return;
        }

        try {
            String next = commands.next();
            List<?> toPrint = switch (next) {
                case "solids" -> scene.getSolids();
                case "lights" -> scene.getLights();
                default -> throw new IllegalStateException("Unexpected value: " + next);
            };
            System.out.println(next + ":");
            for (int i = 0; i < toPrint.size(); i++) {
                Object el = toPrint.get(i);
                String repr = el.toString();
                System.out.println(i + ": " + repr);
            }
        } catch (Exception e) {
            System.out.println(usage);
            e.printStackTrace();
        }

    }
    //endregion
    //endregion


    static void onDebugCommand(Raytracer raytracer, Iterator<String> commands) {
        switch (commands.next()) {
            case "camera" -> {
                var cam = raytracer.getCameraController().getActiveCamera();
                switch (commands.next()) {
                    case "get" -> {
                        switch (commands.next()) {
                            case "yaw" -> System.out.println(cam.getYaw());
                            case "pitch" -> System.out.println(cam.getPitch());
                            case "pos", "position" -> System.out.println(cam.getPosition());
                        }
                    }
                    case "set" -> {
                        switch (commands.next()) {
                            case "pitch" -> cam.setPitch(Double.parseDouble(commands.next()));
                            case "yaw" -> cam.setYaw(Double.parseDouble(commands.next()));
                            case "pos" -> cam.getPosition().set(parsePosArg(commands));
                        }
                    }
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


    //region Utils
    static Vector3d parsePosArg(Iterator<String> commands) {
        return new Vector3d(Double.parseDouble(commands.next()), Double.parseDouble(commands.next()), Double.parseDouble(commands.next()));
    }

    static Color parseColor(String color) throws IllegalArgumentException {
        // get color by name using reflection
        try {
            return (Color) Color.class.getDeclaredField(color)
                                      .get(null); // we love ourselves some unsafe user-controlled reflection
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                return Color.decode(color); // if it's not a name, try parsing it as a color int instead
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Could not parse color: " + color);
            }
        }
    }
    //endregion
}
