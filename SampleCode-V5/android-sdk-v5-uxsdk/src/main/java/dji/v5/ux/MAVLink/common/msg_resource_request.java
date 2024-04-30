/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE RESOURCE_REQUEST PACKING
package dji.v5.ux.MAVLink.common;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* The autopilot is requesting a resource (file, binary, other type of data)
*/
public class msg_resource_request extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_RESOURCE_REQUEST = 142;
    public static final int MAVLINK_MSG_ID_RESOURCE_REQUEST_CRC = 72;
    public static final int MAVLINK_MSG_LENGTH = 243;
    private static final long serialVersionUID = MAVLINK_MSG_ID_RESOURCE_REQUEST;


      
    /**
    * Request ID. This ID should be re-used when sending back URI contents
    */
    public short request_id;
      
    /**
    * The type of requested URI. 0 = a file via URL. 1 = a UAVCAN binary
    */
    public short uri_type;
      
    /**
    * The requested unique resource identifier (URI). It is not necessarily a straight domain name (depends on the URI type enum)
    */
    public short uri[] = new short[120];
      
    /**
    * The way the autopilot wants to receive the URI. 0 = MAVLink FTP. 1 = binary stream.
    */
    public short transfer_type;
      
    /**
    * The storage path the autopilot wants the URI to be stored in. Will only be valid if the transfer_type has a storage associated (e.g. MAVLink FTP).
    */
    public short storage[] = new short[120];
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_RESOURCE_REQUEST;
        packet.crc_extra = MAVLINK_MSG_ID_RESOURCE_REQUEST_CRC;
              
        packet.payload.putUnsignedByte(request_id);
              
        packet.payload.putUnsignedByte(uri_type);
              
        
        for (int i = 0; i < uri.length; i++) {
            packet.payload.putUnsignedByte(uri[i]);
        }
                    
              
        packet.payload.putUnsignedByte(transfer_type);
              
        
        for (int i = 0; i < storage.length; i++) {
            packet.payload.putUnsignedByte(storage[i]);
        }
                    
        
        return packet;
    }

    /**
    * Decode a resource_request message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.request_id = payload.getUnsignedByte();
              
        this.uri_type = payload.getUnsignedByte();
              
         
        for (int i = 0; i < this.uri.length; i++) {
            this.uri[i] = payload.getUnsignedByte();
        }
                
              
        this.transfer_type = payload.getUnsignedByte();
              
         
        for (int i = 0; i < this.storage.length; i++) {
            this.storage[i] = payload.getUnsignedByte();
        }
                
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_resource_request(){
        msgid = MAVLINK_MSG_ID_RESOURCE_REQUEST;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_resource_request(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_RESOURCE_REQUEST;
        unpack(mavLinkPacket.payload);
    }

              
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_RESOURCE_REQUEST - sysid:"+sysid+" compid:"+compid+" request_id:"+request_id+" uri_type:"+uri_type+" uri:"+uri+" transfer_type:"+transfer_type+" storage:"+storage+"";
    }
}
        