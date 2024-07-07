package fail.stderr.usb.common2;

/**
 * @param semiMajorAxis semi-major axis in <b>meters</b>
 * @param eccentricity  eccentricity in <b>dimensionless</b>
 * @param inclination   inclination in <b>radians</b>
 * @param pa            <ul>
 *                      <li>ω</li>
 *                      <li>perigee argument</li>
 *                      <li>argument of periapsis</li>
 *                      </ul>
 *                      <p>
 *                      in <b>radians</b>
 * @param raan          <ul>
 *                      <li>Ω</li>
 *                      <li>right ascension of ascending node</li>
 *                      <li>raan</li>
 *                      <li>longitude of the ascending node</li>
 *                      <li>lan</li>
 *                      </ul>
 *                      <p>
 *                      in <b>radians</b>
 * @param anomaly       <ul>
 *                      <li>mean</li>
 *                      <li>mean anomaly at epoch</li>
 *                      <li>eccentric or true anomaly</li>
 *                      </ul>
 *                      <p>
 *                      in <b>radians</b>
 */
public record KeplerianOrbitParameters(

  double semiMajorAxis,

  double eccentricity,

  double inclination,

  double pa,

  double raan,

  double anomaly

) {
}
