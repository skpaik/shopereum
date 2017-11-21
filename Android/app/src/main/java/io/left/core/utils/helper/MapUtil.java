/*
*  ****************************************************************************
*  * Created by : Md. Imran Hossain on 24-Oct-17 at 10:30 PM.
*  * Email : imran.iot@w3engineers.com
*  *
*  * Last edited by : Md. Imran Hossain on 24-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

package io.left.core.utils.helper;


public final class MapUtil {

    private final static double ONE_EIGHTY_DEGREE = 180.0;
    private final static double SIXTY_DEGREE = 60.0;
    private final static double METER_CONSTANT = 1000.0;
    private final static double MILE_CONSTANT = 1.1515;
    private final static double KILO_METER_CONSTANT = 1.609344;

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function Calculate the distance between two geo point            :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

    public static double getDistance(double myLat, double myLng, double friendsLat, double friendsLng) {

        double theta = myLng - friendsLng;
        double dist = Math.sin(deg2rad(myLat)) * Math.sin(deg2rad(friendsLat)) + Math.cos(deg2rad(myLat)) * Math.cos(deg2rad(friendsLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344 * 1000;
        if (dist > 99.0) {
            dist = dist / 1000.0;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / ONE_EIGHTY_DEGREE);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * ONE_EIGHTY_DEGREE / Math.PI);
    }

    /*0.000621371*/


}
