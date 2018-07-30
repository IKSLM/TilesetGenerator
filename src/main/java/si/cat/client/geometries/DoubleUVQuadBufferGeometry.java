package si.cat.client.geometries;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import thothbot.parallax.core.client.gl2.arrays.Float32Array;
import thothbot.parallax.core.client.gl2.arrays.Uint16Array;
import thothbot.parallax.core.shared.core.BufferAttribute;
import thothbot.parallax.core.shared.core.BufferGeometry;
import thothbot.parallax.core.shared.math.Box2;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * @author XP
 *
 *         Generates N equally sized quads on given positions. The position is in the center of the
 *         quad. Each quad has the UV coordinates set.
 */
public class DoubleUVQuadBufferGeometry extends BufferGeometry {

  public DoubleUVQuadBufferGeometry(Box2 UV, double width, double height) {
    this(new Vector3(), UV, width, height);
  }

  public DoubleUVQuadBufferGeometry(Vector3 position, Box2 UV, double width, double height) {
    this(Collections.singletonList(position), Collections.singletonList(UV), width, height);
  }

  public DoubleUVQuadBufferGeometry(Collection<Vector3> positions, Collection<Box2> UVs, double width, double height) {

    int numQuads = positions.size();
    int triangles = numQuads * 2;
    int chunkSize = 20000;

    Uint16Array indices = Uint16Array.create(triangles * 3);

    for (int i = 0; i < indices.getLength(); i++) {
      indices.set(i, i % (3 * chunkSize));
    }

    Float32Array vertices = Float32Array.create(triangles * 3 * 3);
    Float32Array normals = Float32Array.create(triangles * 3 * 3);
    Float32Array uvs = Float32Array.create(triangles * 3 * 2);
    Float32Array uvs1 = Float32Array.create(triangles * 3 * 2);

    int offset = 0;
    int offset2 = 0;

    final Vector2[] _points = {new Vector2(0, 0), new Vector2(width, 0), new Vector2(0, height),

        new Vector2(width, 0), new Vector2(width, height), new Vector2(0, height)};

    Iterator<Vector3> posIter = positions.iterator();
    Iterator<Box2> boxIter = UVs.iterator();

    while (posIter.hasNext() && boxIter.hasNext()) {
      Vector3 position = posIter.next();
      Box2 uv = boxIter.next();

      double uvw = uv.size().getX();
      double uvh = uv.size().getY();
      double uvx = uv.getMin().getX();
      double uvy = uv.getMin().getY();

      final Vector2[] _uvs = {new Vector2(0, uvh), new Vector2(uvw, uvh), new Vector2(0, 0),

          new Vector2(uvw, uvh), new Vector2(uvw, 0), new Vector2(0, 0)};

      int u = 0;

      for (Vector2 p : _points) {
        double x = p.getX() - width * 0.5 + position.getX();
        double y = p.getY() - height + height * 0.5 + position.getY();
        double z = position.getZ();

        vertices.set(offset, x);
        vertices.set(offset + 1, y);
        vertices.set(offset + 2, z);

        normals.set(offset + 2, 1.0);

        uvs.set(offset2, _uvs[u].getX() + uvx);
        uvs.set(offset2 + 1, 1.0 - (_uvs[u].getY() + uvy));

        uvs1.set(offset2, _uvs[u].getX() + uvx);
        uvs1.set(offset2 + 1, 1.0 - (_uvs[u].getY() + uvy));

        offset += 3;
        offset2 += 2;

        u++;
      }
    }

    this.addAttribute("index", new BufferAttribute(indices, 1));
    this.addAttribute("position", new BufferAttribute(vertices, 3));
    this.addAttribute("normal", new BufferAttribute(normals, 3));
    this.addAttribute("uv", new BufferAttribute(uvs, 2));
    this.addAttribute("uv2", new BufferAttribute(uvs1, 2));

    int offsets = triangles / chunkSize;

    for (int i = 0; i < offsets; i++) {
      BufferGeometry.DrawCall drawcall = new BufferGeometry.DrawCall(i * chunkSize * 3, // start
          Math.min(triangles - (i * chunkSize), chunkSize) * 3, // count
          i * chunkSize * 3 // index
      );

      getDrawcalls().add(drawcall);
    }

    computeBoundingSphere();
  }

}
