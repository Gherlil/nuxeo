/*
 *  (C) Copyright ${year} Nuxeo (http://nuxeo.com/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      ${user}
 */

package org.nuxeo.ecm.automation.core.operations.services.directory;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.directory.api.DirectoryService;

/** Load entries into a {@link org.nuxeo.ecm.directory.Directory} from a CSV File .
 * <p>
 * Depending on duplicate_management policy, duplicate entries a ignored, updated or launch an error.
 * <p>
 *
 * @author <a href="mailto:tcasanova@nuxeo.com">Thierry Casanova</a>
 * @since 11.1
 */
//Fixme (addToStudio ??
@Operation(id = LoadWithCSVDirectory.ID, category = Constants.CAT_SERVICES, label = "Load directory entries from CSV file", description = "Load directory entris from CSV file. Depending on duplicate_management policy, duplicate entries a ignored, updated or launch an error.", addToStudio = true)
public class LoadWithCSVDirectory extends AbstractDirectoryOperation{

    public static final String ID = "Directory.LoadWithCSV";

    @Context
    protected OperationContext ctx;

    @Context
    protected DirectoryService directoryService;

    @Param(name = "directoryName", required = true)
    protected String directoryName;

    @Param(name = "dataFile", required = true) //Fixme : Blob or filename as param ???
    protected Blob dataFile;

    @Param(name = "duplicateManagement", required = true)
    protected String duplicateManagement;


    @OperationMethod
    public void run(){
        validateCanManageDirectories(ctx);
        directoryService.loadCSV(directoryName, dataFile, duplicateManagement);
    }
}
