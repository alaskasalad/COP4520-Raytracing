package src.utility; 

public class Point3D 
{
    public double x, y, z;  

    public void Point3D()
    {
        x = 0.0; 
        y = 0.0;
        z = 0.0; 
    }

    public Point3D(point3D point)
    {
        x = point.x; 
        y = point.y; 
        z = point.z; 
    }

    public Point3D(double x, double y, double z)
    {
        this.x = x; 
        this.y = y; 
        this.z = z; 
    }

    public Point3D add(point3D point)
    {
        return new point3D(x+point.x, y+point.y, z+point.z); 
    }

    public Point3D sub(point3D point)
    {
        return new point3D(x-point.x, y-point.y, z-point.z); 
    }