package es.csic.iiia.planes.custom;

import java.util.Random;

public final class BernoulliModel {

    private static Random random;    // pseudo-random number generator
    private static long seed;        // pseudo-random number generator seed

    // static initializer
    static {
        seed = System.currentTimeMillis();
        random = new Random(seed);
    }

    public BernoulliModel() {}

    /**
     * Returns a random real number uniformly in [0, 1).
     *
     * @return a random real number uniformly in [0, 1)
     */
    public static double uniform() {
        return random.nextDouble();
    }

    public static int uniform(int n) {
        if (n <= 0) throw new IllegalArgumentException("argument must be positive: " + n);
        return random.nextInt(n);
    }

    public static boolean bernoulli(double p) {
        if (!(p >= 0.0 && p <= 1.0))
            throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
        return uniform() < p;
    }

    public static boolean bernoulli() {
        return bernoulli(0.5);
    }
}