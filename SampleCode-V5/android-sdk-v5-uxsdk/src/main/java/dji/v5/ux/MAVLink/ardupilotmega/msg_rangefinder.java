/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE RANGEFINDER PACKING
package dji.v5.ux.MAVLink.ardupilotmega;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* Rangefinder reporting.
*/
public class msg_rangefinder extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_RANGEFINDER = 173;
    public static final int MAVLINK_MSG_ID_RANGEFINDER_CRC = 83;
    public static final int MAVLINK_MSG_LENGTH = 8;
    private static final long serialVersionUID = MAVLINK_MSG_ID_RANGEFINDER;


      
    /**
    * Distance.
    */
    public float distance;
      
    /**
    * Raw voltage if available, zero otherwise.
    */
    public float voltage;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_RANGEFINDER;
        packet.crc_extra = MAVLINK_MSG_ID_RANGEFINDER_CRC;
              
        packet.payload.putFloat(distance);
              
        packet.payload.putFloat(voltage);
        
        return packet;
    }

    /**
    * Decode a rangefinder message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.distance = payload.getFloat();
              
        this.voltage = payload.getFloat();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_rangefinder(){
        msgid = MAVLINK_MSG_ID_RANGEFINDER;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_rangefinder(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_RANGEFINDER;
        unpack(mavLinkPacket.payload);
    }

        
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_RANGEFINDER - sysid:"+sysid+" compid:"+compid+" distance:"+distance+" voltage:"+voltage+"";
    }
}
        