package src.GeneticAlgorithmMultiMSCL.ExtensionMethods;

public class Vector2fExtensions {
    
    // Sets x and y to zero
    public static void Zero(Vector2f v){
        v.X = 0;
        v.Y = 0;
    }

    // Returns true if both x and y are zero
    public static boolean IsZero(Vector2f v)
    {
        return v.X == 0 && v.Y == 0;
    }

    //returns the length of the vector
    public static double Magnitude(Vector2f v)
    {
        return Math.sqrt(v.X * v.X + v.Y * v.Y);
    }

    public static double GetAngle(Vector2f v)
    {
        return Math.atan2(v.Y, v.X);
    }

    //returns the length of the vector
    public static double Magnitude(Vector2f v, Vector2f v2)
    {
        return Math.sqrt(Math.pow(v.X - v2.X, 2) + Math.pow(v.Y - v2.Y, 2));
    }

    //returns the squared length of the vector (thereby avoiding the sqrt)
    public static double LengthSq(Vector2f v)
    {
        return v.X * v.X + v.Y * v.Y;
    }

    //returns the length of the vector
    public static double MagnitudeSquared(Vector2f v, Vector2f v2)
    {
        return v.X * v2.X + v.Y * v2.Y;
    }

    //returns the length of the vector
    public static double MagnitudeSquared(Vector2f v)
    {
        return v.X * v.X + v.Y * v.Y;
    }

    public static Vector2f normalize(Vector2f v)
    {
        double magnitude = v.magnitude();
        if (magnitude == 0)
        {
            return new Vector2f();
        }

        return new Vector2f(v.X / magnitude, v.Y / magnitude);
    }

    // Scale the vector by 'scale'
    public static void Scale(Vector2f v, float scale)
    {
        v.X *= scale;
        v.Y *= scale;
    }

    //returns the dot product of this and v2
    public static double Dot(Vector2f v, Vector2f vector)
    {
        return v.X * vector.X + v.Y * vector.Y;
    }
    
    //returns the vector that is perpendicular to this one
    public static Vector2f PerendicularClockwise(Vector2f v)
    {
        return new Vector2f(v.Y, -v.X);
    }

    //returns the vector that is perpendicular to this one
    public static Vector2f PerendicularCounterClockwise(Vector2f v)
    {
        return new Vector2f(-v.Y, v.X);
    }

    //adjusts x and y so that the length of the vector does not exceed max
    public static void Truncate(Vector2f v, float max)
    {
        if (v.magnitude() < max)
        {
            return;
        }

        v.normalize();
        Scale(v, max);
    }

    // returns the distance between this vector and the one passed as a parameter
    public static double Distance(Vector2f v, Vector2f v2)
    {
        return Math.sqrt(Math.pow(v.X - v2.X, 2) + Math.pow(v.Y - v2.Y, 2));
    }

    ////squared version of above
    public static double DistanceSquared(Vector2f v, Vector2f v2)
    {
        return Math.pow(v.X - v2.X, 2) + Math.pow(v.Y - v2.Y, 2);
    }

    //returns the vector that is the reverse of this vector
    public static Vector2f GetReverse(Vector2f v)
    {
        return new Vector2f(-v.X, -v.Y);
    }
}
