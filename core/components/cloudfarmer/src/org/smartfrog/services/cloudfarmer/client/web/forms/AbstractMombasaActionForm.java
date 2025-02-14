/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.client.web.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.AttributeNames;

public abstract class AbstractMombasaActionForm extends ActionForm implements AttributeNames {
    protected final Log log = LogFactory.getLog("org.smartfrog.services.cloudfarmer.client.web.action." + getActionName());


    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the action name
     */
    protected String getActionName() {
        return "AbstractMombasaActionForm";
    }

    public boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
    }

    public boolean isEmptyFile(FormFile file) {
        return file == null || file.getFileSize() <= 0;
    }

    /**
     * Add an error
     * @param errors errors in, can be null
     * @param name new error name
     * @param key key to use
     * @return the updated/created error list
     */
    protected ActionErrors addError(ActionErrors errors, String name, String key) {
        if (errors == null) {
            errors = new ActionErrors();
        }
        errors.add(name, new ActionMessage(key));
        return errors;
    }
}
