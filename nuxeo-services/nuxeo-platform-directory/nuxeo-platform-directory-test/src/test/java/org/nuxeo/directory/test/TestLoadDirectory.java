/*
 * (C) Copyright 2017-2019 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Casanova Thierry
 *
 */

package org.nuxeo.directory.test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.ecm.directory.BaseDirectoryDescriptor.DuplicatePolicy.ERROR_ON_DUPLCATE;
import static org.nuxeo.ecm.directory.BaseDirectoryDescriptor.DuplicatePolicy.IGNORE_DUPLICATE;
import static org.nuxeo.ecm.directory.BaseDirectoryDescriptor.DuplicatePolicy.UPDATE_DUPLLICATE;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

/**
 * @since 11.1
 */
@RunWith(FeaturesRunner.class)
@Features({ DirectoryFeature.class })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.directory.tests:csv-loaded-directory-contrib.xml")
public class TestLoadDirectory {

    protected static final String CSV_LOAD_DIRECTORY = "csvLoadedDirectory";

    protected static final String CSV_FILE = "test-append-directory.csv";

    @Inject
    protected DirectoryService directoryService;

    @Inject
    protected TransactionalFeature txFeature;

    @Test
    public void testUpdateDirectoryFromCsv() throws IOException {
        try (Session session = directoryService.open(CSV_LOAD_DIRECTORY)) {
            DocumentModel docEurope = session.getEntry("1");
            assertEquals("Europe", docEurope.getPropertyValue("label"));

            Blob blob = Blobs.createBlob(FileUtils.getResourceFileFromContext(CSV_FILE), "text/csv", UTF_8.name(), CSV_FILE);
            directoryService.loadCSV(CSV_LOAD_DIRECTORY, blob, UPDATE_DUPLLICATE.toString());

            // check that document initially loaded is still present
            assertNotNull("entry with id '2' should not be null", session.getEntry("2"));
            // check a new directory entry has been created
            DocumentModel newDoc = session.getEntry("8");
            assertEquals("Italy", newDoc.getPropertyValue("label"));
            // check one directory entry has been updated
            docEurope = session.getEntry("1");
            assertEquals("European Union", docEurope.getPropertyValue("label"));
        }
    }

    @Test(expected = DirectoryException.class)
    public void testIgnoreThenErrorOnDuplicate() throws IOException {
        try (Session session = directoryService.open(CSV_LOAD_DIRECTORY)) {
            Blob blob = Blobs.createBlob(FileUtils.getResourceFileFromContext(CSV_FILE), "text/csv", UTF_8.name(), CSV_FILE);
            directoryService.loadCSV(CSV_LOAD_DIRECTORY, blob, IGNORE_DUPLICATE.toString());
            // verify that new entry was create
            assertEquals("Italy", session.getEntry("8").getPropertyValue("label"));
            // verify existing entry ws not update
            assertEquals("Europe", session.getEntry("1").getPropertyValue("label"));

            // should through Exception:
            directoryService.loadCSV(CSV_LOAD_DIRECTORY, blob, ERROR_ON_DUPLCATE.toString());
        }
    }
}
