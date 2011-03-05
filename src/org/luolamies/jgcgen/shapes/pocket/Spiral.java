package org.luolamies.jgcgen.shapes.pocket;

import org.luolamies.jgcgen.RenderException;
import org.luolamies.jgcgen.path.Axis;
import org.luolamies.jgcgen.path.Coordinate;
import org.luolamies.jgcgen.path.NumericCoordinate;
import org.luolamies.jgcgen.path.Path;
import org.luolamies.jgcgen.path.PathGenerator;

/**
 * Make a circular pocket by a spiraling path
 *
 */
public class Spiral implements PathGenerator {
	private Double radius;
	private Double tooldia;
	private NumericCoordinate origin = new NumericCoordinate(0.0, 0.0, null);
	/**
	 * Set the pocket radius
	 * @param radius
	 * @return this
	 */
	public Spiral radius(double radius) {
		if(radius<=0)
			throw new IllegalArgumentException("Pocket radius must be greater than zero!");
		this.radius = radius;
		return this;
	}
	
	/**
	 * Set the diamater of the tool.
	 * @param radius
	 * @return this
	 */
	public Spiral tool(double dia) {
		if(dia<=0)
			throw new IllegalArgumentException("Tool diameter must be greater than zero!");
		this.tooldia = dia;
		return this;
	}
	
	/**
	 * Set the origin
	 * @param origin
	 * @return
	 */
	public Spiral origin(String origin) {
		Coordinate o = Coordinate.parse(origin);
		if(!(o instanceof NumericCoordinate))
			throw new IllegalArgumentException("Only numeric coordinates supported for the origin!");
		if(!o.isDefined(Axis.X) || !o.isDefined(Axis.Y))
			throw new IllegalArgumentException("Both X and Y axes must be defined!");
		this.origin = (NumericCoordinate)o;
		return this;
	}
	
	public Path toPath() {
		if(radius==null)
			throw new RenderException("Pocket radius not set!");
		if(tooldia==null)
			throw new RenderException("Tool diameter not set!");
		if(tooldia > radius*2)
			throw new RenderException("Tool diameter is greater than pocket diameter!");
		
		Path path = new Path();
		final double ox = origin.getValue(Axis.X);
		final double oy = origin.getValue(Axis.Y);
		final Double oz = origin.getValue(Axis.Z);
		final double tr = tooldia/2.0;
		
		NumericCoordinate point = new NumericCoordinate(ox, oy, oz);
		path.addSegment(Path.SType.MOVE, point);
		double r=0,rr=0;
		while(r<radius) {
			rr=r;
			r += tr;
			if(r>radius)
				r = radius;
			point = new NumericCoordinate(ox-r, oy, null);
			point.set(Axis.I, -(rr+r)/2);
			path.addSegment(Path.SType.CWARC, point);
			
			rr = r;
			r += tr;
			if(r>radius)
				r = radius;
			point = new NumericCoordinate(ox+r, oy, null);
			point.set(Axis.I, (rr+r)/2);
			path.addSegment(Path.SType.CWARC, point);
		}
		// Finish
		point = new NumericCoordinate(ox-r, oy, null);
		point.set(Axis.I, -r);
		path.addSegment(Path.SType.CWARC, point);
		
		if(rr<radius) {
			point = new NumericCoordinate(ox+r, oy, null);
			point.set(Axis.I, r);
			path.addSegment(Path.SType.CWARC, point);
		}
		
		return path;
	}

}
