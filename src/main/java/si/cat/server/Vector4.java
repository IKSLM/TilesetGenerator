/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Creative Commons Attribution 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 3.0 Unported License
 * along with Parallax. If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package si.cat.server;

/**
 * This class is realization of (X, Y, Z, W) vector. Where: X - x coordinate of the vector. Y - y
 * coordinate of the vector. Z - z coordinate of the vector. W - w coordinate of the vector.
 * 
 * @author thothbot
 */
public class Vector4 extends Vector3 {
  /**
   * The W-coordinate
   */
  protected double w;

  // Temporary variables
  static Vector4 _min = new Vector4();
  static Vector4 _max = new Vector4();

  /**
   * This default constructor will initialize vector (0, 0, 0, 1);
   */
  public Vector4() {
    this(0, 0, 0, 1.0);
  }

  /**
   * This constructor will initialize vector (X, Y, Z, 1) from the specified X, Y, Z coordinates.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   * @param z the Z coordinate
   */
  public Vector4(double x, double y, double z) {
    this(x, y, z, 1.0);
  }

  /**
   * This constructor will initialize vector (X, Y, Z, W) from the specified X, Y, Z, W coordinates.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   * @param z the Z coordinate
   * @param w the W coordinate
   */
  public Vector4(double x, double y, double z, double w) {
    super(x, y, z);
    this.w = w;
  }

  /**
   * get W coordinate from the vector
   * 
   * @return a W coordinate
   */
  public double getW() {
    return w;
  }

  /**
   * This method will add specified value to W coordinate of the vector. In another words: w +=
   * value.
   * 
   * @param w the W coordinate
   */
  public void addW(double w) {
    this.w += w;
  }

  /**
   * This method sets W coordinate of the vector.
   * 
   * @param w the W coordinate
   */
  public void setW(double w) {
    this.w = w;
  }

  /**
   * Sets the value of this vector to the specified xyzw coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @param w the w coordinate
   */
  public Vector4 set(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    return this;
  }

  public void setComponent(int index, double value) {

    switch (index) {

      case 0:
        this.x = value;
        break;
      case 1:
        this.y = value;
        break;
      case 2:
        this.z = value;
        break;
      case 3:
        this.w = value;
        break;
      default:
        throw new Error("index is out of range: " + index);

    }

  }

  public double getComponent(int index) {

    switch (index) {

      case 0:
        return this.x;
      case 1:
        return this.y;
      case 2:
        return this.z;
      case 3:
        return this.w;
      default:
        throw new Error("index is out of range: " + index);

    }

  }

  /**
   * Set value of the vector from another vector.
   * 
   * @param v the other vector
   * 
   * @return the current vector
   */
  public Vector4 copy(Vector4 v) {
    return this.set(v.x, v.y, v.z, v.w);
  }

  /**
   * Set value of the vector from another vector.
   * 
   * @param v the other vector
   * 
   * @return the current vector
   */
  public Vector4 copy(Vector3 v) {
    return this.set(v.x, v.y, v.z, 1.0);
  }

  /**
   * Sets the value of this vector to the sum of vectors v1 and v2.
   * 
   * @param v1 the first vector
   * @param v2 the second vector
   */
  public Vector4 add(Vector4 v1, Vector4 v2) {
    this.x = v1.x + v2.x;
    this.y = v1.y + v2.y;
    this.z = v1.z + v2.z;
    this.w = v1.w + v2.w;
    return this;
  }

  /**
   * Sets the value of this vector to the sum of itself and v1.
   * 
   * @param v the other vector
   */
  public Vector4 add(Vector4 v) {
    return this.add(this, v);
  }

  public Vector4 add(double s) {
    this.x += s;
    this.y += s;
    this.z += s;
    this.w += s;
    return this;
  }

  /**
   * Sets the value of this vector to the difference of vectors v1 and v2 (this = v1 - v2).
   * 
   * @param v1 the first vector
   * @param v2 the second vector
   */
  public Vector4 sub(Vector4 v1, Vector4 v2) {
    this.x = v1.x - v2.x;
    this.y = v1.y - v2.y;
    this.z = v1.z - v2.z;
    this.w = v1.w - v2.w;
    return this;
  }

  /**
   * Sets the value of this vector to the difference of itself and v1 (this = this - v).
   * 
   * @param v the other vector
   */
  public Vector4 sub(Vector4 v) {
    return this.sub(this, v);
  }

  public Vector4 multiply(Vector4 v) {
    return this.multiply(this, v);
  }

  /**
   * Sets the value of this vector to the scalar multiplication of the scale factor with this.
   * 
   * @param s the scalar value
   */
  @Override
  public Vector4 multiply(double s) {
    this.x *= s;
    this.y *= s;
    this.z *= s;
    this.w *= s;
    return this;
  }

  public Vector4 multiply(Vector4 v1, Vector4 v2) {
    this.x = v1.x * v2.x;
    this.y = v1.y * v2.y;
    this.z = v1.z * v2.z;
    this.w = v1.w * v2.w;

    return this;
  }

  public Vector4 divide(Vector4 v1, Vector4 v2) {
    this.x = v1.x / v2.x;
    this.y = v1.y / v2.y;
    this.z = v1.z / v2.z;
    this.w = v1.w / v2.w;
    return this;
  }

  public Vector4 divide(Vector4 v) {
    return this.divide(this, v);
  }

  public Vector4 divide(double scalar) {
    if (scalar != 0) {

      double invScalar = 1.0 / scalar;

      this.x *= invScalar;
      this.y *= invScalar;
      this.z *= invScalar;
      this.w *= invScalar;

    } else {

      this.x = 0;
      this.y = 0;
      this.z = 0;
      this.w = 1;

    }

    return this;

  }

  public Vector4 min(Vector4 v) {
    if (this.x > v.x) {
      this.x = v.x;
    }

    if (this.y > v.y) {
      this.y = v.y;
    }

    if (this.z > v.z) {
      this.z = v.z;
    }

    if (this.w > v.w) {
      this.w = v.w;
    }

    return this;
  }

  public Vector4 max(Vector4 v) {
    if (this.x < v.x) {
      this.x = v.x;
    }

    if (this.y < v.y) {
      this.y = v.y;
    }

    if (this.z < v.z) {
      this.z = v.z;
    }

    if (this.w < v.w) {
      this.w = v.w;
    }

    return this;
  }

  /**
   * This function assumes min &#60; max, if this assumption isn't true it will not operate
   * correctly
   */
  public Vector4 clamp(Vector4 min, Vector4 max) {
    if (this.x < min.x) {
      this.x = min.x;
    } else if (this.x > max.x) {
      this.x = max.x;
    }

    if (this.y < min.y) {
      this.y = min.y;
    } else if (this.y > max.y) {
      this.y = max.y;
    }

    if (this.z < min.z) {
      this.z = min.z;
    } else if (this.z > max.z) {
      this.z = max.z;
    }

    if (this.w < min.w) {
      this.w = min.w;
    } else if (this.w > max.w) {
      this.w = max.w;
    }

    return this;
  }

  public Vector4 clamp(double minVal, double maxVal) {
    _min.set(minVal, minVal, minVal, minVal);
    _max.set(maxVal, maxVal, maxVal, maxVal);

    return this.clamp(_min, _max);
  }

  public Vector4 floor() {

    this.x = Math.floor(this.x);
    this.y = Math.floor(this.y);
    this.z = Math.floor(this.z);
    this.w = Math.floor(this.w);

    return this;

  }

  public Vector4 ceil() {

    this.x = Math.ceil(this.x);
    this.y = Math.ceil(this.y);
    this.z = Math.ceil(this.z);
    this.w = Math.ceil(this.w);

    return this;

  }

  public Vector4 round() {

    this.x = Math.round(this.x);
    this.y = Math.round(this.y);
    this.z = Math.round(this.z);
    this.w = Math.round(this.w);

    return this;

  }

  public Vector4 roundToZero() {

    this.x = (this.x < 0) ? Math.ceil(this.x) : Math.floor(this.x);
    this.y = (this.y < 0) ? Math.ceil(this.y) : Math.floor(this.y);
    this.z = (this.z < 0) ? Math.ceil(this.z) : Math.floor(this.z);
    this.w = (this.w < 0) ? Math.ceil(this.w) : Math.floor(this.w);

    return this;

  }

  /**
   * Negates the value of this vector in place.
   */
  public Vector4 negate() {
    this.x = -this.x;
    this.y = -this.y;
    this.z = -this.z;
    this.w = -this.w;

    return this;
  }

  /**
   * returns the dot product of this vector and v1
   * 
   * @param v1 the other vector
   * @return the dot product of this vector and v1
   */
  public double dot(Vector4 v1) {
    return (this.x * v1.x + this.y * v1.y + this.z * v1.z + this.w * v1.w);
  }

  /**
   * Returns the length of this vector.
   * 
   * @return the length of this vector as a double
   */
  public double length() {
    return Math.sqrt(lengthSq());
  }

  /**
   * Returns the squared length of this vector
   * 
   * @return the squared length of this vector as a double
   */
  public double lengthSq() {
    return dot(this);
  }

  public double lengthManhattan() {
    return Math.abs(this.x) + Math.abs(this.y) + Math.abs(this.z) + Math.abs(this.w);
  }

  /**
   * Normalizes this vector in place.
   */
  @Override
  public Vector4 normalize() {
    return divide(length());
  }

  public Vector4 setLength(double l) {
    double oldLength = this.length();

    if (oldLength != 0 && l != oldLength) {

      this.multiply(l / oldLength);

    }

    return this;

  }

  public Vector4 lerp(Vector4 v1, double alpha) {
    this.x += (v1.x - this.x) * alpha;
    this.y += (v1.y - this.y) * alpha;
    this.z += (v1.z - this.z) * alpha;
    this.w += (v1.w - this.w) * alpha;

    return this;
  }

  public boolean equals(Vector4 v) {
    return ((v.x == this.x) && (v.y == this.y) && (v.z == this.z) && (v.w == this.w));
  }

  public Vector4 clone() {
    return new Vector4(this.x, this.y, this.z, this.w);
  }

  public String toString() {
    return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
  }
}
