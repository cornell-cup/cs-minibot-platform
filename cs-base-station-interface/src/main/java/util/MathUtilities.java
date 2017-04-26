package util;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

/**
 * Utility functions for math
 */
public final class MathUtilities {
    /**
     * Normalize an angle.
     *
     * @param radians input angle in radians
     * @return the angle normalized to be between 0 and 2pi
     */
    public static double normalizeAngle(double radians) {
        return MathUtils.normalizeAngle(radians, FastMath.PI);
    }
}
