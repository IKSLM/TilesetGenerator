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

import thothbot.parallax.core.shared.math.Mathematics;

/**
 * This class is realization of (X, Y, Z) vector. Where: X - x coordinate of the vector. Y - y
 * coordinate of the vector. Z - z coordinate of the vector.
 * 
 * @author thothbot
 */
public class Vector3 extends Vector2 {
  /**
   * The Z-coordinate
   */
  protected double z;

  // Temporary variables
  static Vector3 _min = new Vector3();
  static Vector3 _max = new Vector3();
  static Vector3 _v1 = new Vector3();

  /**
   * This default constructor will initialize vector (0, 0, 0);
   */
  public Vector3() {
    this(0, 0, 0);
  }

  /**
   * This constructor will initialize vector (X, Y, Z) from the specified X, Y, Z coordinates.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   * @param z the Z coordinate
   */
  public Vector3(double x, double y, double z) {
    super(x, y);
    this.z = z;
  }

  /**
   * get Z coordinate from the vector
   * 
   * @return a Z coordinate
   */
  public double getZ() {
    return this.z;
  }

  /**
   * This method will add specified value to Z coordinate of the vector. In another words: z +=
   * value.
   * 
   * @param z the Y coordinate
   */
  public void addZ(double z) {
    this.z += z;
  }

  /**
   * This method sets Z coordinate of the vector.
   * 
   * @param z the Z coordinate
   */
  public void setZ(double z) {
    this.z = z;
  }

  /**
   * Set value of the vector to the specified (X, Y, Z) coordinates.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   * @param z the Z coordinate
   */
  public Vector3 set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  /**
   * Set value of the vector to the specified (A, A, A) coordinates.
   * 
   * @param a the X, Y and Z coordinate
   */
  public Vector3 set(double a) {
    this.x = a;
    this.y = a;
    this.z = a;
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
  public Vector3 copy(Vector3 v) {
    this.set(v.getX(), v.getY(), v.getZ());

    return this;
  }

  public Vector3 add(Vector3 v) {
    return this.add(this, v);
  }

  public Vector3 add(Vector3 v1, Vector3 v2) {
    this.x = v1.x + v2.x;
    this.y = v1.y + v2.y;
    this.z = v1.z + v2.z;

    return this;
  }

  @Override
  public Vector3 add(double s) {
    this.addX(s);
    this.addY(s);
    this.addZ(s);

    return this;
  }

  public Vector3 sub(Vector3 v) {
    return this.sub(this, v);
  }

  public Vector3 sub(Vector3 v1, Vector3 v2) {
    this.x = v1.x - v2.x;
    this.y = v1.y - v2.y;
    this.z = v1.z - v2.z;

    return this;
  }

  public Vector3 multiply(Vector3 v) {
    return this.multiply(this, v);
  }

  public Vector3 multiply(Vector3 v1, Vector3 v2) {
    this.x = v1.x * v2.x;
    this.y = v1.y * v2.y;
    this.z = v1.z * v2.z;

    return this;
  }

  public Vector3 multiply(double s) {
    this.x *= s;
    this.y *= s;
    this.z *= s;
    return this;
  }

  public Vector3 divide(Vector3 v) {
    return this.divide(this, v);
  }

  public Vector3 divide(Vector3 v1, Vector3 v2) {
    this.x = v1.x / v2.x;
    this.y = v1.y / v2.y;
    this.z = v1.z / v2.z;

    return this;
  }

  @Override
  public Vector3 divide(double scalar) {
    if (scalar != 0) {

      double invScalar = 1.0 / scalar;

      this.x *= invScalar;
      this.y *= invScalar;
      this.z *= invScalar;

    } else {

      this.x = 0;
      this.y = 0;
      this.z = 0;

    }

    return this;

  }

  public Vector3 min(Vector3 v) {
    if (this.x > v.x) {
      this.x = v.x;
    }

    if (this.y > v.y) {
      this.y = v.y;
    }

    if (this.z > v.z) {
      this.z = v.z;
    }

    return this;
  }

  public Vector3 max(Vector3 v) {
    if (this.x < v.x) {
      this.x = v.x;
    }

    if (this.y < v.y) {
      this.y = v.y;
    }

    if (this.z < v.z) {
      this.z = v.z;
    }

    return this;
  }

  /**
   * This function assumes min &#60; max, if this assumption isn't true it will not operate
   * correctly
   */
  public Vector3 clamp(Vector3 min, Vector3 max) {
    // This function assumes min < max, if this assumption isn't true it will not operate correctly

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

    return this;
  }

  public Vector3 clamp(double minVal, double maxVal) {
    _min.set(minVal, minVal, minVal);
    _max.set(maxVal, maxVal, maxVal);

    return this.clamp(_min, _max);
  }

  public Vector3 floor() {

    this.x = Math.floor(this.x);
    this.y = Math.floor(this.y);
    this.z = Math.floor(this.z);

    return this;

  }

  public Vector3 ceil() {

    this.x = Math.ceil(this.x);
    this.y = Math.ceil(this.y);
    this.z = Math.ceil(this.z);

    return this;

  }

  public Vector3 round() {

    this.x = Math.round(this.x);
    this.y = Math.round(this.y);
    this.z = Math.round(this.z);

    return this;

  }

  public Vector3 roundToZero() {

    this.x = (this.x < 0) ? Math.ceil(this.x) : Math.floor(this.x);
    this.y = (this.y < 0) ? Math.ceil(this.y) : Math.floor(this.y);
    this.z = (this.z < 0) ? Math.ceil(this.z) : Math.floor(this.z);

    return this;

  }

  @Override
  public Vector3 negate() {
    this.x = -this.x;
    this.y = -this.y;
    this.z = -this.z;

    return this;
  }

  /**
   * Computes the dot product of this vector and vector v1.
   * 
   * @param v1 the other vector
   * @return the dot product of this vector and v1
   */
  public double dot(Vector3 v1) {
    return (this.x * v1.x + this.y * v1.y + this.z * v1.z);
  }

  /**
   * Returns the squared length of this vector.
   * 
   * @return the squared length of this vector
   */
  public double lengthSq() {
    return dot(this);
  }

  /**
   * Returns the length of this vector.
   * 
   * @return the length of this vector
   */
  public double length() {
    return Math.sqrt(lengthSq());
  }

  public double lengthManhattan() {
    return Math.abs(this.x) + Math.abs(this.y) + Math.abs(this.z);
  }

  /**
   * Normalizes this vector in place.
   */
  @Override
  public Vector3 normalize() {
    return this.divide(this.length());
  }

  public Vector3 setLength(double l) {
    double oldLength = this.length();

    if (oldLength != 0 && l != oldLength) {

      this.multiply(l / oldLength);
    }

    return this;
  }

  public Vector3 lerp(Vector3 v1, double alpha) {
    this.x += (v1.x - this.x) * alpha;
    this.y += (v1.y - this.y) * alpha;
    this.z += (v1.z - this.z) * alpha;

    return this;
  }

  /**
   * Sets this vector to be the vector cross product of vectors v1 and v2.
   * 
   * @param a the first vector
   * @param b the second vector
   */
  public Vector3 cross(Vector3 a, Vector3 b) {
    double ax = a.x, ay = a.y, az = a.z;
    double bx = b.x, by = b.y, bz = b.z;

    this.x = ay * bz - az * by;
    this.y = az * bx - ax * bz;
    this.z = ax * by - ay * bx;

    return this;
  }

  public Vector3 cross(Vector3 v) {
    return cross(this, v);
  }

  public Vector3 projectOnVector(Vector3 vector) {
    _v1.copy(vector).normalize();

    double dot = this.dot(_v1);

    return this.copy(_v1).multiply(dot);
  }

  public Vector3 projectOnPlane(Vector3 planeNormal) {
    _v1.copy(this).projectOnVector(planeNormal);

    return this.sub(_v1);
  }

  /**
   * reflect incident vector off plane orthogonal to normal normal is assumed to have unit length
   * 
   * @param normal
   * @return
   */
  public Vector3 reflect(Vector3 normal) {
    return this.sub(_v1.copy(normal).multiply(2 * this.dot(normal)));
  }

  public double angleTo(Vector3 v) {
    double theta = this.dot(v) / (this.length() * v.length());

    // clamp, to handle numerical problems

    return Math.acos(Mathematics.clamp(theta, -1, 1));
  }

  public double distanceTo(Vector3 v1) {
    return Math.sqrt(distanceToSquared(v1));
  }

  public double distanceToSquared(Vector3 v1) {
    double dx = this.x - v1.x;
    double dy = this.y - v1.y;
    double dz = this.z - v1.z;
    return (dx * dx + dy * dy + dz * dz);
  }

  /**
   * Returns true if all of the data members of v1 are equal to the corresponding data members in
   * this Vector3.
   * 
   * @param v1 the vector with which the comparison is made
   * @return true or false
   */
  public boolean equals(Vector3 v1) {
    return (this.x == v1.x && this.y == v1.y && this.z == v1.z);
  }

  public Vector3 clone() {
    return new Vector3(this.getX(), this.getY(), this.getZ());
  }

  public String toString() {
    return "(" + this.x + ", " + this.y + ", " + this.z + ")";
  }
}
