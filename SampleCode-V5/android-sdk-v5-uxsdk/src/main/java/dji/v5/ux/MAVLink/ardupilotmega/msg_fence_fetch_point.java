/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE FENCE_FETCH_POINT PACKING
package dji.v5.ux.MAVLink.ardupilotmega;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* Request a current fence point from MAV.
*/
public class msg_fence_fetch_point extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_FENCE_FETCH_POINT = 161;
    public static final int MAVLINK_MSG_ID_FENCE_FETCH_POINT_CRC = 68;
    public static final int MAVLINK_MSG_LENGTH = 3;
    private static final long serialVersionUID = MAVLINK_MSG_ID_FENCE_FETCH_POINT;


      
    /**
    * System ID.
    */
    public short target_system;
      
    /**
    * Component ID.
    */
    public short target_component;
      
    /**
    * Point index (first point is 1, 0 is for return point).
    */
    public short idx;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_FENCE_FETCH_POINT;
        packet.crc_extra = MAVLINK_MSG_ID_FENCE_FETCH_POINT_CRC;
              
        packet.payload.putUnsignedByte(target_system);
              
        packet.payload.putUnsignedByte(target_component);
              
        packet.payload.putUnsignedByte(idx);
        
        return packet;
    }

    /**
    * Decode a fence_fetch_point message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.target_system = payload.getUnsignedByte();
              
        this.target_component = payload.getUnsignedByte();
              
        this.idx = payload.getUnsignedByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_fence_fetch_point(){
        msgid = MAVLINK_MSG_ID_FENCE_FETCH_POINT;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_fence_fetch_point(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_FENCE_FETCH_POINT;
        unpack(mavLinkPacket.payload);
    }

          
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_FENCE_FETCH_POINT - sysid:"+sysid+" compid:"+compid+" target_system:"+target_system+" target_component:"+target_component+" idx:"+idx+"";
    }
}
        