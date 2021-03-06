//
//   GeoXP Lib, library for efficient geo data manipulation
//
//   Copyright 2020-      SenX S.A.S.
//   Copyright 2019-2020  iroise.net S.A.S.
//   Copyright 1999-2019  Mathias Herberts
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package com.geoxp.geo;

public class GoogleMaps {
  /**
   * API key associated with the domain 'geocoord.com'
   */
  public static final String API_KEY = "ABQIAAAAbMNeMMgWkiVCZlcwtJeF1hTbNRBBRaV0YYpoVQn0p53IiRMTtRSjiwf2NC3kD7wHxtXvyZ8lfNIVNg";

  //
  // @see http://mapwrecker.wordpress.com/category/mercator/
  //
  
  /**
   * Coordinate of the center pixel at different zoom levels.
   * At zoom level 0 the map is on one tile of 256x256 and thus the x,y coordinates of the center are 128,128
   * This goes on up to zoom level 30
   */
  private static final long[] centerPixel = { 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 
                                              131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864,
                                              134217728, 268435456, 536870912, 1073741824, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L };

  /**
   * Number of pixels per degree of longitude (tile span/360) for different zoom levels.
   */
  private static final double[] pixelsPerDegree =  { 0.7111111111111111, 1.4222222222222223, 2.8444444444444446, 5.688888888888889, 11.377777777777778, 22.755555555555556, 45.51111111111111, 91.02222222222223, 182.04444444444445, 364.0888888888889, 728.1777777777778, 1456.3555555555556, 2912.711111111111, 5825.422222222222, 11650.844444444445, 23301.68888888889, 46603.37777777778, 93206.75555555556, 186413.51111111112, 372827.02222222224, 745654.0444444445, 1491308.088888889, 2982616.177777778, 5965232.355555556, 11930464.711111112, 23860929.422222223, 47721858.844444446, 95443717.68888889, 190887435.37777779, 381774870.75555557, 763549741.5111111 };
  
  /**
   * Number of pixels per unit, given a span of 2PI on the tile, for different zoom levels
   * For zoom level 0, 128/PI = 40.74366.....
   */
  private static final double[] pixelsPerUnit =  {40.74366543152521, 81.48733086305042, 162.97466172610083, 325.94932345220167, 651.8986469044033, 1303.7972938088067, 2607.5945876176133, 5215.189175235227, 10430.378350470453, 20860.756700940907, 41721.51340188181, 83443.02680376363, 166886.05360752725, 333772.1072150545, 667544.214430109, 1335088.428860218, 2670176.857720436, 5340353.715440872, 10680707.430881744, 21361414.86176349, 42722829.72352698, 85445659.44705395, 170891318.8941079, 341782637.7882158, 683565275.5764316, 1367130551.1528633, 2734261102.3057265, 5468522204.611453, 10937044409.222906, 21874088818.445812, 43748177636.891624};
  
  /**
   * Convert Lat,Lon expressed in degrees into X,Y coordinates for
   * the given Zoom level.
   * 
   * @param lat Latitude in degrees (South latitude are negative)
   * @param lng Longitude in degrees (West longitudes are negative)
   * @param zoom Zoom level, from 0 to 30
   * @return
   */
  public static long[] LatLon2XY(double lat, double lng, int zoom) {

    long[] coords = { 0L, 0L };
    
    long center = centerPixel[zoom];
  
    long x = (long) Math.round(center + (lng * pixelsPerDegree[zoom]));
    
    double sinlat = Math.sin(lat * Math.PI / 180.0);
    
    // Make sure we do not hit 1.0
    if (sinlat < -0.9999) {
      sinlat = -0.9999;
    } else if (sinlat > 0.9999) {
      sinlat = 0.9999;
    }

    long y = (long) Math.round(center + (0.5 * Math.log((1+sinlat)/(1-sinlat)) * (-pixelsPerUnit[zoom])));
  
    coords[0] = x;
    coords[1] = y;
    
    return coords;
  }
  
  /**
   * Convert X,Y pixel coordinate to degree Lat,Lon at the given zoom level.
   * @param x
   * @param y
   * @param zoom
   * @return
   */
  public static double[] XY2LatLon(long x, long y, int zoom) {
    double[] coords = { 0.0, 0.0 };
   
    long center = centerPixel[zoom];
    
    double lng = (x - center) / pixelsPerDegree[zoom];
    double latu = (y - center) / -pixelsPerUnit[zoom];
    double radlat = 2 * Math.atan(Math.exp(latu)) - Math.PI/2.0;
    double lat = radlat / (Math.PI / 180.0);

    coords[0] = lat;
    coords[1] = lng;
    
    return coords;
  }
}
