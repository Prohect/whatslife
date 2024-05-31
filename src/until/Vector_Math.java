package until;

import java.util.Arrays;

public class Vector_Math implements Cloneable {

    private double[] vector;

    public Vector_Math(double[] vector) {
        this.vector = vector;
    }

    /**
     * @param b another V3d
     * @return the cross result or null if this is not a V3d
     */
    public Vector_Math cross(Vector_Math b) {
        if (getVector().length > 3) {
            return null;
        } else {

            double[] av = new double[3];
            for (int i = 0; i < av.length; i++) {
                if (i < vector.length) {
                    av[i] = vector[i];
                }
            }
            double[] bv = new double[3];
            for (int i = 0; i < bv.length; i++) {
                if (i < b.vector.length) {
                    bv[i] = b.vector[i];
                }

            }
            return new Vector_Math(new double[]{av[1] * bv[2] - av[2] * bv[1], av[2] * bv[0] - av[0] * bv[2], av[0] * bv[1] - av[1] * bv[0]});
        }
    }

    public Vector_Math add(Vector_Math b) {
        String s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        for (int i = 0; i < vector.length; i++) {
            if (i < b.vector.length) {
                vector[i] += b.vector[i];
            }
        }
        s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        return this;
    }

    public Vector_Math sub(Vector_Math b) {
        String s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        for (int i = 0; i < vector.length; i++) {
            if (i < b.vector.length) {
                vector[i] -= b.vector[i];
            }
        }
        s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        return this;
    }

    public Vector_Math multi(double b) {
        String s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= b;
        }
        s = String.valueOf(this.length());
        if (s.equals("NaN")) {
            s = "null";
        }
        return this;
    }

    public double[] getVector() {
        return vector;
    }

    public double dot(Vector_Math b) {

        double result = 0;

        for (int i = 0; i < vector.length; i++) {
            result += this.getVector()[i] * b.getVector()[i];
        }
        return result;
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    @Override
    public Vector_Math clone() {
        try {
            Vector_Math clone = (Vector_Math) super.clone();
            clone.vector = this.vector.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Vector_Math{" +
                "vector=" + Arrays.toString(vector) +
                '}';
    }
}

