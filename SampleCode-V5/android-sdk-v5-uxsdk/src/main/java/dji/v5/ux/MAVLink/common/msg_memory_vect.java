/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE MEMORY_VECT PACKING
package dji.v5.ux.MAVLink.common;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* Send raw controller memory. The use of this message is discouraged for normal packets, but a quite efficient way for testing new messages and getting experimental debug output.
*/
public class msg_memory_vect extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_MEMORY_VECT = 249;
    public static final int MAVLINK_MSG_ID_MEMORY_VECT_CRC = 204;
    public static final int MAVLINK_MSG_LENGTH = 36;
    private static final long serialVersionUID = MAVLINK_MSG_ID_MEMORY_VECT;


      
    /**
    * Starting address of the debug variables
    */
    public int address;
      
    /**
    * Version code of the type variable. 0=unknown, type ignored and assumed int16_t. 1=as below
    */
    public short ver;
      
    /**
    * Type code of the memory variables. for ver = 1: 0=16 x int16_t, 1=16 x uint16_t, 2=16 x Q15, 3=16 x 1Q14
    */
    public short type;
      
    /**
    * Memory contents at specified address
    */
    public byte value[] = new byte[32];
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_MEMORY_VECT;
        packet.crc_extra = MAVLINK_MSG_ID_MEMORY_VECT_CRC;
              
        packet.payload.putUnsignedShort(address);
              
        packet.payload.putUnsignedByte(ver);
              
        packet.payload.putUnsignedByte(type);
              
        
        for (int i = 0; i < value.length; i++) {
            packet.payload.putByte(value[i]);
        }
                    
        
        return packet;
    }

    /**
    * Decode a memory_vect message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.address = payload.getUnsignedShort();
              
        this.ver = payload.getUnsignedByte();
              
        this.type = payload.getUnsignedByte();
              
         
        for (int i = 0; i < this.value.length; i++) {
            this.value[i] = payload.getByte();
        }
                
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_memory_vect(){
        msgid = MAVLINK_MSG_ID_MEMORY_VECT;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_memory_vect(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_MEMORY_VECT;
        unpack(mavLinkPacket.payload);
    }

            
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_MEMORY_VECT - sysid:"+sysid+" compid:"+compid+" address:"+address+" ver:"+ver+" type:"+type+" value:"+value+"";
    }
}
        