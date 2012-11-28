package edu.cmu;
/**
 * 
 * @author Sungho Cho
 *
 */
public class LocationUtils {

	public static double distanceBetween(double srcLat, double srcLng, double dstLat, double dstLng) {
		double earthRadius = 3963.1676;	// Miles
		double dLat = Math.toRadians(dstLat - srcLat);
		double dLng = Math.toRadians(dstLng - srcLng);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * 
				Math.cos(Math.toRadians(srcLat)) * Math.cos(Math.toRadians(dstLat));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadius * c;
	}

	public static boolean isOverlapping(double srcLat, double srcLng, double srcRad, double dstLat, double dstLng, double dstRad) {
		// Two circles overlap each other only if the distance between  
		// center of circles less than sum of radius of two circles
		double distance = distanceBetween(srcLat, srcLng, dstLat, dstLng);
		return distance <= srcRad + dstRad;
	}
}