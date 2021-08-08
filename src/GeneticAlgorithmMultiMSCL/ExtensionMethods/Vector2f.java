package src.GeneticAlgorithmMultiMSCL.ExtensionMethods;

public class Vector2f extends Vector2fExtensions implements java.io.Serializable {

    // Combatible with 1.1
    static final long serialVersionUID = -2168194326883512320L;

    public double X;
    public double Y;

    public Vector2f()
    {
        this.X = 0;
        this.Y = 0;
    }

    public Vector2f(double x, double y)
    {
        this.X = x;
        this.Y = y;
    }

    public final double dot(Vector2f v1)
    {
        return (this.X * v1.X + this.Y*v1.Y);
    }

    public final double magnitude()
    {
        return Math.sqrt(this.X*this.X + this.Y*this.Y);
    }

    public final double lengthSquared()
    {
        return (this.X*this.X + this.Y*this.Y);
    }

    public double Distance(Vector2f v2)
    {
        return Math.sqrt(Math.pow(this.X - v2.X, 2) + Math.pow(this.Y - v2.Y, 2));
    }

    public final void normalize()
    {
        double norm;

        norm = (double) (1.0/Math.sqrt(this.X*this.X + this.Y*this.Y));
        this.X *= norm;
        this.Y *= norm;
    }

   public final double angle(Vector2f v1)
   {
      double vDot = this.dot(v1) / ( this.magnitude()*v1.magnitude() );
      if( vDot < -1.0) vDot = -1.0;
      if( vDot >  1.0) vDot =  1.0;
      return((double) (Math.acos( vDot )));
   }
}