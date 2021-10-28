// BV Ue2 WS2021/22 Vorgabe
//
// Copyright (C) 2021 by Klaus Jung
// All rights reserved.
// Date: 2021-07-22
 		   		     	

package bv_ws2122;


public class GeometricTransform {
 		   		     	
	public enum InterpolationType { 
		NEAREST("Nearest Neighbour"), 
		BILINEAR("Bilinear");
		
		private final String name;       
	    private InterpolationType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch(interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;	
		}
		
	}
 		   		     	
	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
 		   		     	
		// TODO: implement the geometric transformation using nearest neighbour image rendering
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant
		
		//Winkel umrechnen in Bogenmaß
		double phi = Math.toRadians(angle);
		
		for(int xd = 0; xd < dst.width; xd++) {
			for(int yd = 0; yd < dst.height; yd++) {
				
				//dst Bild verschieben
				double xd1 = xd - (dst.width-1) /2;
				double yd1 = yd - (dst.height-1) /2;
				
				//Perspektivdaten berechnen
				double ys = yd1 / (Math.cos(phi) - perspectiveDistortion * Math.sin(phi) * yd1);
				double xs = xd1 * (perspectiveDistortion * Math.sin(phi) * ys +1);
				
				//Nearest Neighbor & src Bild zurückverschieben
				int ys1 = (int) Math.round(ys+ (src.height-1) /2);
				int xs1 = (int) Math.round(xs+ (src.width-1) /2);
				
				//src Bild in dst Bild einsetzen
				int pos = xd + yd * dst.width;
				
				//Randbehandlung
				if(xs1 < 0 || ys1 <0 || xs1 >= src.width || ys1 >= src.height){
					dst.argb[pos] = 0xffffffff;
				} else {
					
					
					int argb = src.argb[xs1 + ys1 * src.width];
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb & 0xff; 
					dst.argb[pos] = (0xff<<24) | (r << 16) | (g << 8) | b;
					
				}
			}
		}
	}


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
 		   		     	
		// TODO: implement the geometric transformation using bilinear interpolation
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant
		//Winkel umrechnen in Bogenmaß
				double phi = Math.toRadians(angle);
				
				for(int xd = 0; xd < dst.width; xd++) {
					for(int yd = 0; yd < dst.height; yd++) {
						
						//dst Bild verschieben
						double xd1 = xd - (dst.width) /2;
						double yd1 = yd - (dst.height) /2;
						
						//Perspektivdaten berechnen
						double ys = yd1 / (Math.cos(phi) - perspectiveDistortion * Math.sin(phi) * yd1);
						double xs = xd1 * (perspectiveDistortion * Math.sin(phi) * ys +1);
						
						//src Bild zurückverschieben
						double ys1 = ys+ (src.height) /2;
						double xs1 = xs+ (src.width) /2;
						//Bilineare Interpolation
						int ysINT = (int) ys1;
						int xsINT = (int) xs1;
						
						double v = ys1 - ysINT;
						double h = xs1 - xsINT;
						
						//neighbouring values
						int rA = 255; int rB = 255; int rC = 255; int rD = 255;
						int gA = 255; int gB = 255; int gC = 255; int gD = 255;
						int bA = 255; int bB = 255; int bC = 255; int bD = 255;
						
						//difference between coordinates of src and dst (dst is bigger than src)
						int smallestSrcCoordinateX = dst.width - src.width; 
						int smallestSrcCoordinateY = dst.height - src.height;
						
						if(!(xsINT < smallestSrcCoordinateX || xsINT >= src.width + smallestSrcCoordinateX) && !(ysINT < smallestSrcCoordinateY || ysINT >= src.height + smallestSrcCoordinateY)) {
							//if (xsINT - smallestSrcCoordinateX >= 0 && ysINT - smallestSrcCoordinateY >= 0) {
							if(!(xsINT < 0 || xsINT >= src.width || ysINT < 0 || ysINT >= src.height)) {
								int argbA = src.argb[pos(xsINT - smallestSrcCoordinateX, ysINT - smallestSrcCoordinateY, src)];
								rA = (argbA >> 16) & 0xff;
								gA = (argbA >> 8) & 0xff;
								bA = argbA & 0xff;
							}
							//if (xsINT - smallestSrcCoordinateX + 1 < src.width && ysINT - smallestSrcCoordinateY >= 0) {
							if(!(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height)) {
								int argbB = src.argb[pos(xsINT - smallestSrcCoordinateX + 1, ysINT - smallestSrcCoordinateY, src)];
								rB = (argbB >> 16) & 0xff;
								gB = (argbB >> 8) & 0xff;
								bB = argbB & 0xff;
							}
							//if (xsINT - smallestSrcCoordinateX >= 0 && ysINT - smallestSrcCoordinateY + 1 < src.height) { 
							if(!(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height)) {
								int argbC = src.argb[pos(xsINT - smallestSrcCoordinateX, ysINT - smallestSrcCoordinateY + 1, src)];
								rC = (argbC >> 16) & 0xff;
								gC = (argbC >> 8) & 0xff;
								bC = argbC & 0xff;
							}
							//if (xsINT - smallestSrcCoordinateX + 1 < src.width && ysINT - smallestSrcCoordinateY + 1 < src.height) {
							if(!(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height)) {
								int argbD = src.argb[pos(xsINT - smallestSrcCoordinateX + 1, ysINT - smallestSrcCoordinateY + 1, src)];
								rD = (argbD >> 16) & 0xff;
								gD = (argbD >> 8) & 0xff;
								bD = argbD & 0xff;
							}
						}
						// ^ verschiebt bild, schneidet es ab, innerhalb d. bildes bilinear okay, rand nicht
						int rn = (int) Math.round(rA * (1-h) * (1-v) + rB * h * (1-v) + rC * (1-h) * v + rD * h * v);
						int gn = (int) Math.round(gA * (1-h) * (1-v) + gB * h * (1-v) + gC * (1-h) * v + gD * h * v);
						int bn = (int) Math.round(bA * (1-h) * (1-v) + bB * h * (1-v) + bC * (1-h) * v + bD * h * v);
						
						//just noticed, that this makes a bilinear version of the original and only then puts the  angle on the sides.....
						
/*						//Punkt A
						if(xsINT < 0 || xsINT >= src.width || ysINT < 0 || ysINT >= src.height) {
							rn = (int) Math.round(255 * (1-h) * (1-v));
							gn = (int) Math.round(255 * (1-h) * (1-v));
							bn = (int) Math.round(255 * (1-h) * (1-v));
						} else {
							int argb = src.argb[xsINT + ysINT * src.width];
							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb & 0xff;
							
							rn = (int) Math.round(r * (1-h) * (1-v));
							gn = (int) Math.round(g * (1-h) * (1-v));
							bn = (int) Math.round(b * (1-h) * (1-v));
						}
						//Punkt B
						if(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height) {
							rn = (int) Math.round(rn + 255 * h * (1-v));
							gn = (int) Math.round(gn + 255 * h * (1-v));
							bn = (int) Math.round(bn + 255 * h * (1-v));
						} else {
							int argbB = src.argb[xsINT+1 + ysINT * src.width];
							int rB = (argbB >> 16) & 0xff;
							int gB = (argbB >>  8) & 0xff;
							int bB =  argbB & 0xff;
						
							rn = (int) Math.round(rn + rB * h * (1-v));
							gn = (int) Math.round(gn + gB * h * (1-v));
							bn = (int) Math.round(bn + bB * h * (1-v));
						}
						//Punkt C
						if(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height) {
							rn = (int) Math.round(rn + 255 * (1-h) * v);
							gn = (int) Math.round(gn + 255 * (1-h) * v);
							bn = (int) Math.round(bn + 255 * (1-h) * v);
						} else {
							int argbC = src.argb[xsINT + (ysINT+1) * src.width];
							int rC = (argbC >> 16) & 0xff;
							int gC = (argbC >>  8) & 0xff;
							int bC =  argbC & 0xff;
							
							rn = (int) Math.round(rn + rC * (1-h) * v);
							gn = (int) Math.round(gn + gC * (1-h) * v);
							bn = (int) Math.round(bn + bC * (1-h) * v);
						}
						
						//Punkt D
						if(xsINT < 0 || xsINT+1 >= src.width || ysINT < 0 || ysINT +1 >= src.height) {
							rn = (int) Math.round(rn + 255 * h * v);
							gn = (int) Math.round(gn + 255 * h * v);
							bn = (int) Math.round(bn + 255 * h * v);
						} else {
							int argbD = src.argb[xsINT+1 + (ysINT+1) * src.width];
							int rD = (argbD >> 16) & 0xff;
							int gD = (argbD >>  8) & 0xff;
							int bD =  argbD & 0xff;
							
							rn = (int) Math.round(rn + rD * h * v);
							gn = (int) Math.round(gn + gD * h * v);
							bn = (int) Math.round(bn + bD * h * v);
							
						}
*/						
						//Plan: alles in eine formel
						
//						int xsINT = (int) xs1;
//						int ysINT = (int) ys1;
//						
//						double h = xs1 - xsINT;
//						double v = ys1 - ysINT;
//						
						
						//src Bild in dst Bild einsetzen
						int pos = xd + yd * dst.width;
						
						if(xs1 < 0 || ys1 <0 || xs1 >= src.width || ys1 >= src.height){
							dst.argb[pos] = 0xffffffff;
						} else {
							rn = Math.min(255, Math.max(0, rn));
							gn = Math.min(255, Math.max(0, gn));
							bn = Math.min(255, Math.max(0, bn));
							dst.argb[pos] = (0xff<<24) | (rn << 16) | (gn << 8) | bn;
						}
//						dst.argb[pos] = (0xff<<24) | (rn << 16) | (gn << 8) | bn;
					}
				}
 	}

	private int pos(int x, int y, RasterImage image) {
		int pos = x + y * image.width;
		return pos;
	}
 		   		     	
}
 		   		     	



