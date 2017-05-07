package basestation.vision;

import util.MathUtilities;

import java.util.Optional;

/**
 * All vision coordinates are with respect to a coordinate systems.
 * The canonical coordinate system is such that 0 radians points in the positive x direction and positive
 * rotation is in the counter-clockwise direction. This means pi/2 radians is along the positive Y axis axis.
 */
public class VisionCoordinate {
    // Position in meters
    public final double x;
    public final double y;
    public final double vx;
    public final double vy;
    public double[][] P  = new double[4][4];

    // Radians, optional in case a system cannot present angles
    private final Optional<Double> theta;

    public VisionCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.theta = Optional.empty();
        initializeP();
    }

    public VisionCoordinate(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.theta = Optional.empty();
        initializeP();
    }

    public VisionCoordinate(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.theta = Optional.of(theta);
        initializeP();
    }

    /**
     *
     * @param x X portion of the coordinate with respect to the coordinate system origin
     * @param y Y portion of the coordinate with respect to the coordinate system origin
     * @param theta an angle in radians as defined above
     */
    public VisionCoordinate(double x, double y, double vx, double vy, double theta) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.theta = Optional.of(theta);
        initializeP();
    }

    public void initializeP() {
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                if (row == col) {
                    P[row][col] = 1;
                }
            }
        }
    }

    /**
     * Returns the angle from this coordinate to other in degrees.
     *
     * @param other The target coordinate
     * @return the desired angle in normalized radians
     */
    public double getAngleTo(VisionCoordinate other) {
        double dx = other.x - x;
        double dy = other.y - y;
        double res = Math.atan2(dy, dx);
        return normalize(res);
    }

    /**
     * Returns the 2d euclidean distances to the other coordinate, assuming they
     * are based on the same coordinate system
     * @param other The target coordinate
     * @return Euclidean distance in meters
     */
    public double getDistanceTo(VisionCoordinate other) {
        return Math.sqrt(Math.pow(other.x - x, 2.) + Math.pow(other.y - y, 2.));
    }

    public Optional<Double> getTheta() {
        return theta;
    }

    /**
     *
     * @return The angle of the coordinate or 0 if it is not set. Note, this is not
     * a recommended method since an optional method is presented.
     */
    public double getThetaOrZero(){
        return theta.orElse(0.0);
    }

    @Override
    public String toString() {
        return "(" + String.format("%.2f", x) + "," + String.format("%.2f", y) + "," + String.format("%.2f", theta.orElse(0.0)) + "radians)"; // TODO: Format in velocities and optional theta
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof VisionObject && other.toString().equals(toString()));
    }

    /**
     * Puts a normally created angle into the 0-2PI range
     * @param angle
     * @return
     */
    private double normalize(double angle) {
        return MathUtilities.normalizeAngle(angle);
    }

    public void setP(double[][] P) {
        this.P = P;
    }

    public double[][] getP() {
        return P;
    }

    /**
     * Returns x coordinate
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Returns y coordinate
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Returns x velocity
     * @return vx
     */
    public double getVelocityX() {
        return vx;
    }

    /**
     * Returns y velocity
     * @return vy
     */
    public double getVelocityY() {
        return vy;
    }

}
