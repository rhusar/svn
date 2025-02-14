/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.rpm.local;

import org.smartfrog.services.rpm.manager.RpmErrors;
import org.smartfrog.services.rpm.local.LocalRPMTool;
import org.smartfrog.services.rpm.local.RPMUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * abstract local RPM Tool implementation.
 */
public abstract class AbstractLocalRPMToolImpl extends PrimImpl implements LocalRPMTool, RpmErrors {
    private String rpmPackage;
    private String options;
    private RPMUtils rpmUtils;

    public AbstractLocalRPMToolImpl() throws RemoteException {
    }


    /**
     * At deploy time we resolve the package name and the options, and create a new rpmutils instance
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        rpmPackage = sfResolve(ATTR_RPM_PACKAGE, "", true);
        options = sfResolve(ATTR_OPTIONS, "", true);
        rpmUtils = new RPMUtils(sfLog());
    }

    protected String getRpmPackage() {
        return rpmPackage;
    }

    protected String getOptions() {
        return options;
    }

    protected RPMUtils getRpmUtils() {
        return rpmUtils;
    }

    protected void upgradePackage(String packageOptions) throws SmartFrogException {
        try {
            getRpmUtils().UpgradePackage(getRpmPackage(), packageOptions);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_UPGRADE + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_UPGRADE +
                    getRpmPackage(), e);
        }
    }

    protected void installPackage(String packageOptions) throws SmartFrogException {
        try {
            getRpmUtils().InstallPackage(getRpmPackage(), packageOptions);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_INSTALL + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_INSTALL +
                    getRpmPackage(), e);
        }
    }

    protected void uninstallPackage(String packageOptions) throws SmartFrogException {
        try {
            getRpmUtils().UninstallPackage(getRpmPackage(), packageOptions);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_UNINSTALL + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_UNINSTALL +
                    getRpmPackage(), e);
        }
    }
}
