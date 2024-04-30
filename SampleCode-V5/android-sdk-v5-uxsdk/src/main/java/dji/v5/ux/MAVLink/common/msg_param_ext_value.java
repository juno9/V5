/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE PARAM_EXT_VALUE PACKING
package dji.v5.ux.MAVLink.common;
import dji.v5.ux.MAVLink.MAVLinkPacket;
import dji.v5.ux.MAVLink.Messages.MAVLinkMessage;
import dji.v5.ux.MAVLink.Messages.MAVLinkPayload;
        
/**
* Emit the value of a parameter. The inclusion of param_count and param_index in the message allows the recipient to keep track of received parameters and allows them to re-request missing parameters after a loss or timeout.
*/
public class msg_param_ext_value extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_PARAM_EXT_VALUE = 322;
    public static final int MAVLINK_MSG_ID_PARAM_EXT_VALUE_CRC = 243;
    public static final int MAVLINK_MSG_LENGTH = 149;
    private static final long serialVersionUID = MAVLINK_MSG_ID_PARAM_EXT_VALUE;


      
    /**
    * Total number of parameters
    */
    public int param_count;
      
    /**
    * Index of this parameter
    */
    public int param_index;
      
    /**
    * Parameter id, terminated by NULL if the length is less than 16 human-readable chars and WITHOUT null termination (NULL) byte if the length is exactly 16 chars - applications have to provide 16+1 bytes storage if the ID is stored as string
    */
    public byte param_id[] = new byte[16];
      
    /**
    * Parameter value
    */
    public byte param_value[] = new byte[128];
      
    /**
    * Parameter type.
    */
    public short param_type;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_PARAM_EXT_VALUE;
        packet.crc_extra = MAVLINK_MSG_ID_PARAM_EXT_VALUE_CRC;
              
        packet.payload.putUnsignedShort(param_count);
              
        packet.payload.putUnsignedShort(param_index);
              
        
        for (int i = 0; i < param_id.length; i++) {
            packet.payload.putByte(param_id[i]);
        }
                    
              
        
        for (int i = 0; i < param_value.length; i++) {
            packet.payload.putByte(param_value[i]);
        }
                    
              
        packet.payload.putUnsignedByte(param_type);
        
        return packet;
    }

    /**
    * Decode a param_ext_value message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.param_count = payload.getUnsignedShort();
              
        this.param_index = payload.getUnsignedShort();
              
         
        for (int i = 0; i < this.param_id.length; i++) {
            this.param_id[i] = payload.getByte();
        }
                
              
         
        for (int i = 0; i < this.param_value.length; i++) {
            this.param_value[i] = payload.getByte();
        }
                
              
        this.param_type = payload.getUnsignedByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_param_ext_value(){
        msgid = MAVLINK_MSG_ID_PARAM_EXT_VALUE;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_param_ext_value(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_PARAM_EXT_VALUE;
        unpack(mavLinkPacket.payload);
    }

         
    /**
    * Sets the buffer of this message with a string, adds the necessary padding
    */
    public void setParam_Id(String str) {
        int len = Math.min(str.length(), 16);
        for (int i=0; i<len; i++) {
            param_id[i] = (byte) str.charAt(i);
        }

        for (int i=len; i<16; i++) {            // padding for the rest of the buffer
            param_id[i] = 0;
        }
    }

    /**
    * Gets the message, formated as a string
    */
    public String getParam_Id() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            if (param_id[i] != 0)
                buf.append((char) param_id[i]);
            else
                break;
        }
        return buf.toString();

    }
                          
    /**
    * Sets the buffer of this message with a string, adds the necessary padding
    */
    public void setParam_Value(String str) {
        int len = Math.min(str.length(), 128);
        for (int i=0; i<len; i++) {
            param_value[i] = (byte) str.charAt(i);
        }

        for (int i=len; i<128; i++) {            // padding for the rest of the buffer
            param_value[i] = 0;
        }
    }

    /**
    * Gets the message, formated as a string
    */
    public String getParam_Value() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 128; i++) {
            if (param_value[i] != 0)
                buf.append((char) param_value[i]);
            else
                break;
        }
        return buf.toString();

    }
                           
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_PARAM_EXT_VALUE - sysid:"+sysid+" compid:"+compid+" param_count:"+param_count+" param_index:"+param_index+" param_id:"+param_id+" param_value:"+param_value+" param_type:"+param_type+"";
    }
}
        