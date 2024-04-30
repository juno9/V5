/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE DISTANCE_SENSOR PACKING
package dji.v5.ux.MAVLink.common;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* 
*/
public class msg_distance_sensor extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_DISTANCE_SENSOR = 132;
    public static final int MAVLINK_MSG_ID_DISTANCE_SENSOR_CRC = 85;
    public static final int MAVLINK_MSG_LENGTH = 14;
    private static final long serialVersionUID = MAVLINK_MSG_ID_DISTANCE_SENSOR;


      
    /**
    * Timestamp (time since system boot).
    */
    public long time_boot_ms;
      
    /**
    * Minimum distance the sensor can measure
    */
    public int min_distance;
      
    /**
    * Maximum distance the sensor can measure
    */
    public int max_distance;
      
    /**
    * Current distance reading
    */
    public int current_distance;
      
    /**
    * Type of distance sensor.
    */
    public short type;
      
    /**
    * Onboard ID of the sensor
    */
    public short id;
      
    /**
    * Direction the sensor faces. downward-facing: ROTATION_PITCH_270, upward-facing: ROTATION_PITCH_90, backward-facing: ROTATION_PITCH_180, forward-facing: ROTATION_NONE, left-facing: ROTATION_YAW_90, right-facing: ROTATION_YAW_270
    */
    public short orientation;
      
    /**
    * Measurement covariance, 0 for unknown / invalid readings
    */
    public short covariance;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_DISTANCE_SENSOR;
        packet.crc_extra = MAVLINK_MSG_ID_DISTANCE_SENSOR_CRC;
              
        packet.payload.putUnsignedInt(time_boot_ms);
              
        packet.payload.putUnsignedShort(min_distance);
              
        packet.payload.putUnsignedShort(max_distance);
              
        packet.payload.putUnsignedShort(current_distance);
              
        packet.payload.putUnsignedByte(type);
              
        packet.payload.putUnsignedByte(id);
              
        packet.payload.putUnsignedByte(orientation);
              
        packet.payload.putUnsignedByte(covariance);
        
        return packet;
    }

    /**
    * Decode a distance_sensor message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.time_boot_ms = payload.getUnsignedInt();
              
        this.min_distance = payload.getUnsignedShort();
              
        this.max_distance = payload.getUnsignedShort();
              
        this.current_distance = payload.getUnsignedShort();
              
        this.type = payload.getUnsignedByte();
              
        this.id = payload.getUnsignedByte();
              
        this.orientation = payload.getUnsignedByte();
              
        this.covariance = payload.getUnsignedByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_distance_sensor(){
        msgid = MAVLINK_MSG_ID_DISTANCE_SENSOR;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_distance_sensor(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_DISTANCE_SENSOR;
        unpack(mavLinkPacket.payload);
    }

                    
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_DISTANCE_SENSOR - sysid:"+sysid+" compid:"+compid+" time_boot_ms:"+time_boot_ms+" min_distance:"+min_distance+" max_distance:"+max_distance+" current_distance:"+current_distance+" type:"+type+" id:"+id+" orientation:"+orientation+" covariance:"+covariance+"";
    }
}
        