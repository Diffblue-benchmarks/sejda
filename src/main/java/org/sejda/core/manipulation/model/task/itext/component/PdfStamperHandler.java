/*
 * Created on 31/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.task.itext.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfStream;

/**
 * Component responsible for handling operations related to a {@link PdfStamper} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfStamperHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PdfStamperHandler.class);

    private PdfStamper stamper = null;
    private FileOutputStream ouputStream = null;

    /**
     * Creates a new instance initializing the inner {@link PdfStamper} instance.
     * 
     * @param reader
     *            input reader
     * @param ouputFile
     *            {@link File} to stamp on
     * @param version
     *            version for the created stamper, if null the version number is taken from the input {@link PdfReader}
     * @throws TaskException
     *             in case of error
     */
    public PdfStamperHandler(PdfReader reader, File ouputFile, PdfVersion version) throws TaskException {
        try {
            ouputStream = new FileOutputStream(ouputFile);
            if (version != null) {
                stamper = new PdfStamper(reader, ouputStream, version.getVersionAsCharacter());
            } else {
                stamper = new PdfStamper(reader, ouputStream, reader.getPdfVersion());
            }
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfStamper.", e);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the PdfStamper.", e);
        }
    }

    /**
     * Enables compression if compress is true
     * 
     * @param compress
     */
    public void setCompressionOnStamper(boolean compress) {
        if (compress) {
            stamper.setFullCompression();
            stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
        }
    }

    /**
     * Closes the stamper suppressing the exception.
     * 
     */
    public void closePdfStamper() {
        try {
            stamper.close();
        } catch (DocumentException e) {
            LOG.error("Error closing the PdfStamper.", e);
        } catch (IOException e) {
            LOG.error("Error closing the PdfStamper.", e);
        }
        IOUtils.closeQuietly(ouputStream);
    }

    /**
     * Adds the creator to the metadata taken from the reader and it sets it to the {@link PdfStamper}
     * 
     * @param reader
     */
    public void setCreatorOnStamper(PdfReader reader) {
        HashMap<String, String> meta = reader.getInfo();
        setMetadataOnStamper(meta);
    }

    /**
     * Adds the creator to the metadata in input and it sets it to the {@link PdfStamper}
     * 
     * @param meta
     */
    public void setMetadataOnStamper(HashMap<String, String> meta) {
        meta.put(PdfMetadataKey.CREATOR.getKey(), Sejda.CREATOR);
        stamper.setMoreInfo(meta);
    }

    /**
     * Sets the encryption for this document delegating encryption to the stamper.
     * 
     * @see PdfStamper#setEncryption(int, String, String, int)
     * @param encryptionType
     * @param userPassword
     * @param ownerPassword
     * @param permissions
     * @throws TaskException
     *             wraps the {@link DocumentException} that can be thrown by the stamper
     */
    public void setEncryptionOnStamper(int encryptionType, String userPassword, String ownerPassword, int permissions)
            throws TaskException {
        try {
            stamper.setEncryption(encryptionType, userPassword, ownerPassword, permissions);
        } catch (DocumentException e) {
            throw new TaskException("An error occured while setting encryption on the document", e);
        }
    }

    /**
     * Sets the viewer preferences on the stamper
     * 
     * @see PdfStamper#setViewerPreferences(int)
     * @param preferences
     */
    public void setViewerPreferencesOnStamper(int preferences) {
        stamper.setViewerPreferences(preferences);
    }

    /**
     * adds the viewer preference to the stamper
     * 
     * @see PdfStamper#addViewerPreference(PdfName, PdfObject)
     * @param key
     * @param value
     */
    public void addViewerPreferenceOnStamper(PdfName key, PdfObject value) {
        stamper.addViewerPreference(key, value);
    }

    /**
     * 
     * @return the inner {@link PdfStamper} instance
     */
    public PdfStamper getStamper() {
        return stamper;
    }

}
