package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.sfcore.logging.LogSF;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;

public class CommandController {
	private ArrayList<Command> Commands = new ArrayList<Command>(10);
	private ArrayList<VirtualMachineConfig> refVirtualMachines;

	MessageDispatcher msgDisp;

	public CommandController(AvalancheServer inAvlSrv, LogSF inLog) {
		// create the message dispatcher
		msgDisp = new MessageDispatcher(inAvlSrv);

		// create the commands
		RevertToSnapshotCommand revert = new RevertToSnapshotCommand(msgDisp, inLog);
		SendCredentialsCommand cred = new SendCredentialsCommand(msgDisp, inLog);
		DeleteVirtualMachineCommand delete = new DeleteVirtualMachineCommand(msgDisp, inLog);
		CreateVirtualMachineCommand create = new CreateVirtualMachineCommand(msgDisp, inLog);
		TakeSnapshotCommand snapshot = new TakeSnapshotCommand(msgDisp, inLog);
		StartVirtualMachineCommand start = new StartVirtualMachineCommand(msgDisp, inLog);
		WaitForToolsCommand wait = new WaitForToolsCommand(msgDisp, inLog);
		CopyFileFromHostToGuestCommand copy = new CopyFileFromHostToGuestCommand(msgDisp, inLog);
		ExecuteInGuestCommand exec = new ExecuteInGuestCommand(msgDisp, inLog);
		StopVirtualMachineCommand stop = new StopVirtualMachineCommand(msgDisp, inLog);

		// concatenate them
		revert.setNextOnSuccess(cred);
		revert.setNextOnFailure(delete);

		cred.setNextOnSuccess(start);
		cred.setNextOnFailure(delete);

		delete.setNextOnSuccess(create);
		delete.setNextOnFailure(create);

		create.setNextOnSuccess(snapshot);
		create.setNextOnFailure(delete);

		snapshot.setNextOnSuccess(start);
		snapshot.setNextOnFailure(delete);

		start.setNextOnSuccess(wait);
		start.setNextOnFailure(revert);

		wait.setNextOnSuccess(copy);
		wait.setNextOnFailure(stop);

		copy.setNextOnSuccess(exec);
		copy.setNextOnFailure(copy);

		exec.setNextOnFailure(copy);

		// sometimes virtual machines are unpingable, restart them
		exec.setNextOnSuccess(stop); // this is an exploit!

		stop.setNextOnSuccess(start);
		stop.setNextOnFailure(delete);


		// add them to the list
		Commands.add(revert);
		Commands.add(cred);
		Commands.add(delete);
		Commands.add(create);
		Commands.add(snapshot);
		Commands.add(start);
		Commands.add(wait);
		Commands.add(copy);
		Commands.add(exec);
		Commands.add(stop);
	}

	public void executeCommands(ArrayList<VirtualMachineConfig> inVirtualMachines) {
		refVirtualMachines = inVirtualMachines;

		for (VirtualMachineConfig cfg : refVirtualMachines) {
			cfg.setCurrentCommand(Commands.get(0));
			cfg.getCurrentCommand().execute(cfg);
		}
	}

	public void handleResponse(XMPPEventExtension inExt) {
		// get the right virtual machine
		for (VirtualMachineConfig virt : refVirtualMachines) {
			if (virt.getAffinity().equals(inExt.getHost()) &&
				virt.getDisplayName().equals(inExt.getPropertyBag().get(VMWareConstants.VMNAME))) {
					// queue the next message
					virt.getCurrentCommand().handlePacket(virt, inExt);

					// send the next message
					msgDisp.sendNext(virt.getAffinity());
				}
		}
	}
}
