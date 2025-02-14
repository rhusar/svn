package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.vast.architecture.VirtualMachineConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MessageDispatcher {
	private HashMap<String, DispatcherThread> dispatcherThreads = new LinkedHashMap<String, DispatcherThread>(10);
	private AvalancheServer refAvl = null;

	public MessageDispatcher(AvalancheServer refAvl) {
		this.refAvl = refAvl;
	}

	public synchronized void sendMessage(VirtualMachineConfig inCfg, Command inCmd) {
		if (dispatcherThreads.containsKey(inCfg.getAffinity())) {
			// queue message
			DispatcherThread dt = dispatcherThreads.get(inCfg.getAffinity());
			dt.queueMessage(inCfg, inCmd);
		} else {
			// create new dispatcher thread
			DispatcherThread dt = new DispatcherThread(refAvl, inCfg.getAffinity());
			dt.queueMessage(inCfg, inCmd);
			dt.start();

			// add it to the hashmap
			dispatcherThreads.put(inCfg.getAffinity(), dt);
		}
	}

	public AvalancheServer getRefAvl() {
		return refAvl;
	}

	public void sendNext(String inHost) {
		dispatcherThreads.get(inHost).sendNext();
	}

	public void setRefAvl(AvalancheServer refAvl) {
		this.refAvl = refAvl;
	}
}
