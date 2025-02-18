package src.utility;

class Vector3D 
{
    double x, y, z; 

    // main constructor 
    public Vector3D (double x1, double y1, double z1)
    {
        x1 = x; 
        y1 = y; 
        z1 = z; 
    }

    // constructor if we dont have values
    public Vector3D()
    {
        this(0.0, 0.0, 0.0); // calling constructor above 
    }

    // constructor if we get a vector 
    public Vector3D(Vector3D v)
    {
        this(v.x, v.y, v.z);  
    }

    /* Vector3 result = vector1.add(vector2)
    ^ how it would look when using this */
    public Vector3D add(Vector3 v)
    {
        return new Vector3D(x+v.x, y+v.y, z+v.z); 
    }

    public Vector3D sub(Vector3D v)
    {
        return new Vector3D(x-v.x, y-v.y, z-v.z)
    }

    public Vector3D multiply(double scalar)
    {
        return new Vector3D(this.x * scalar, this.y * scalar, this.z*scalar); 
    }
    
    // new vector orthogonal to both vectors using determinate
    public Vector3 cross(Vector3 v) 
    {
        return new Vector3(
            (y * v.z) - (z * v.y),
            (z * v.x) - (x * v.z),
            (x * v.y) - (y * v.x)
        );
    }

    // returns scalar
    public double dot(Vector3D v)
    {
        return x*v.x + y*v.y + z*v.z; 
    }

    public double magnitude()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /* Vector3D unitVector = v.normalize() 
    how you would get the unit vec (only dir, 1 len) */
    public void normalize()
    {
        double length = magnitude(); 
        x /= length; 
        y /= length; 
        z /= length; 
    }
}