package src.utility;

// ray is represented by origin and a direction
public class Ray 
{
    public Point3D origin; 
    public Vector3D direction; 
    
    public ray(Point3D origin, Vector3D direction)
    {
        this.origin = new point3D(origin); 
        this.direction = new vector3D(direction); 
    }
}