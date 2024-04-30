/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

package dji.v5.ux.MAVLink.enums;

/** 
* Enumeration for low battery states.
*/
public class MAV_BATTERY_CHARGE_STATE {
   public static final int MAV_BATTERY_CHARGE_STATE_UNDEFINED = 0; /* Low battery state is not provided | */
   public static final int MAV_BATTERY_CHARGE_STATE_OK = 1; /* Battery is not in low state. Normal operation. | */
   public static final int MAV_BATTERY_CHARGE_STATE_LOW = 2; /* Battery state is low, warn and monitor close. | */
   public static final int MAV_BATTERY_CHARGE_STATE_CRITICAL = 3; /* Battery state is critical, return or abort immediately. | */
   public static final int MAV_BATTERY_CHARGE_STATE_EMERGENCY = 4; /* Battery state is too low for ordinary abort sequence. Perform fastest possible emergency stop to prevent damage. | */
   public static final int MAV_BATTERY_CHARGE_STATE_FAILED = 5; /* Battery failed, damage unavoidable. | */
   public static final int MAV_BATTERY_CHARGE_STATE_UNHEALTHY = 6; /* Battery is diagnosed to be defective or an error occurred, usage is discouraged / prohibited. | */
   public static final int MAV_BATTERY_CHARGE_STATE_ENUM_END = 7; /*  | */
}
            