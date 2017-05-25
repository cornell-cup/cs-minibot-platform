package simulator.physics;

/**
 * The Physics engine contains Mass, Speed, Direction, Velocity, Accleration, Friction, Force, and Momentum
 */
public class Physics {
    private double mass; //in kg
    private double speed; //in m/s
    private double direction; //in radians
    private double velocityX;
    private double velocityY;
    private double acceleration; //in m/s^2
    private double staticFriction; //constant
    private double dynamicFriction; //constant
    private double force; //in Newtons
    private double momentum; //kg*m/s
    private double angularVelocity; // radians/s, counter-clockwise

    public Physics() {
        mass = 0;
        speed = 0;
        direction = 0;
        velocityX = 0;
        velocityY = 0;
        acceleration = 0;
        staticFriction = 0;
        dynamicFriction = 0;
        force = 0;
        momentum = 0;
        this.angularVelocity = 0;
    }

    public Physics(double mass, double speed, double direction,
                   double acceleration, double staticFriction,
                   double dynamicFriction, double force,
                   double momentum, double angularVelocity) {
        this.mass = mass;
        this.speed = speed;
        this.direction = direction;
        this.velocityX = this.speed * Math.cos(this.direction);
        this.velocityY = this.speed * Math.sin(this.direction);
        this.acceleration = acceleration; //check this with meche maybe
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.force = force;
        this.momentum = momentum;
        this.angularVelocity = angularVelocity;
    }

    /**
     * @return the mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * @param mass becomes the new mass
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed becomes the new speed
     */
    public void setSpeed(double speed) {
        System.out.println("Set speed: " + speed);
        this.angularVelocity = 0;
        this.speed = speed;
        this.velocityX = this.speed * Math.cos(this.direction);
        this.velocityY = this.speed * Math.sin(this.direction);
    }

    /**
     */
    public void setVelocity(double vx, double vy) {
        this.angularVelocity = 0;
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public void setAngularVelocity(double vang) {
        this.velocityX = 0;
        this.velocityY = 0;
        this.angularVelocity = vang;
    }

    /**
     * @return the direction in radians
     */
    public double getDirection() {
        return direction;
    }

    /**
     * @param direction becomes the new direction
     */
    public void setDirection(double direction) {
        this.direction = direction % (2 * Math.PI);
        this.velocityX = this.speed * Math.cos(this.direction);
        this.velocityY = this.speed * Math.sin(this.direction);
    }

    public double getAngularVel() {
        return this.angularVelocity;
    }

    /**
     * @return the x-velocity
     */
    public double getXVelocity() {
        return velocityX;
    }

    /**
     * @return the y-velocity
     */
    public double getYVelocity() {
        return velocityY;
    }

    /**
     * @return the acceleration
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * @param acceleration becomes the new acceleration
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * @return the static friction coefficient
     */
    public double getStaticFriction() {
        return staticFriction;
    }

    /**
     * @param staticFriction becomes the new static friction coefficient
     */
    public void setStaticFriction(double staticFriction) {
        this.staticFriction = staticFriction;
    }

    /**
     * @return the dynamic friction coefficient
     */
    public double getDynamicFriction() {
        return dynamicFriction;
    }

    /**
     * @param dynamicFriction becomes the new dynamic friction coefficient
     */
    public void setDynamicFriction(double dynamicFriction) {
        this.dynamicFriction = dynamicFriction;
    }

    /**
     * @return the force
     */
    public double getForce() {
        return force;
    }

    /**
     * @param force becomes the new force
     */
    public void setForce(double force) {
        this.force = force;
    }

    /**
     * @return the momentum
     */
    public double getMomentum() {
        return momentum;
    }

    /**
     * @param momentum becomes the new momentum
     */
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    /**
     * @return a string to print for Physics
     */
    public String toString() {
        return "Mass: " + this.mass + "\nSpeed: " + this.speed +
                "\nDirection: " + this.direction + "\nXVelocity: " +
                this.velocityX + "\nYVelocity: " + this.velocityY + "\nAngular Velocity: " + this.angularVelocity;
    }
}
