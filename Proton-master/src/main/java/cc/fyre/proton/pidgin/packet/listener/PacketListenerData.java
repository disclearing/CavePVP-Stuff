package cc.fyre.proton.pidgin.packet.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.pidgin.packet.Packet;

import java.lang.reflect.Method;

/**
 * A wrapper class that holds all the information needed to
 * identify and execute a message function.
 *
 */
@AllArgsConstructor
@Getter
public class PacketListenerData {

	private Object instance;
	private Method method;
	private Class packetClass;

	public boolean matches(Packet packet) {
		return this.packetClass == packet.getClass();
	}

}
