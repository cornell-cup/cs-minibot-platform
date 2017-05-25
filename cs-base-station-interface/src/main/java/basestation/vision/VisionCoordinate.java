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

    // Radians, optional in case a system cannot present angles
    private final Optional<Double> theta;

    public VisionCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
        this.theta = Optional.empty();
    }

    /**
     * @param x     X portion of the coordinate with respect to the coordinate system origin
     * @param y     Y portion of the coordinate with respect to the coordinate system origin
     * @param theta an angle in radians as defined above
     */
    public VisionCoordinate(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = Optional.of(theta);
    }

    /**
     * Returns the angle from this coordinate to other in rad.
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
     *
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
     * @return The angle of the coordinate or 0 if it is not set. Note, this is not
     * a recommended method since an optional method is presented.
     */
    public double getThetaOrZero() {
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
     *
     * @param angle
     * @return
     */
    private double normalize(double angle) {
        return MathUtilities.normalizeAngle(angle);
    }
}
