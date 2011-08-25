/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.ViewerPreferencesParameters;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageLayout;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPrintScaling;

/**
 * Tests for the ViewerPreferences command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ViewerPreferencesConsoleTest extends BaseTaskConsoleTest {

    @Override
    String getTaskName() {
        return "setviewerpreferences";
    }

    @Override
    protected CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults() {
        return new CommandLineTestBuilder(getTaskName());
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains("-h " + getTaskName(), "Usage: sejda-console setviewerpreferences options");
    }

    @Test
    public void testFlagOptions_on() {
        ViewerPreferencesParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("--centerWindow").with("--displayDocTitle").with("--hideMenu").with("--fitWindow")
                .with("--hideWindowUI").with("--hideToolbar").toString());

        assertContainsAll(EnumSet.allOf(PdfBooleanPreference.class), parameters.getActivePreferences());
    }

    @Test
    public void testFlagOptions_off() {
        ViewerPreferencesParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());

        assertTrue(parameters.getActivePreferences().isEmpty());
    }

    @Test
    public void testNonDefaults() {
        ViewerPreferencesParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("--noPrintScaling").with("--direction", "RIGHT_TO_LEFT")
                .with("--duplex", "DUPLEX_FLIP_SHORT_EDGE").with("--nfsMode", "USE_THUMNS")
                .with("--layout", "TWO_PAGE_LEFT").with("--mode", "USE_ATTACHMENTS").toString());

        assertEquals(PdfPrintScaling.NONE, parameters.getPrintScaling());
        assertEquals(PdfDirection.RIGHT_TO_LEFT, parameters.getDirection());
        assertEquals(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE, parameters.getDuplex());
        assertEquals(PdfNonFullScreenPageMode.USE_THUMNS, parameters.getNfsMode());
        assertEquals(PdfPageLayout.TWO_PAGE_LEFT, parameters.getPageLayout());
        assertEquals(PdfPageMode.USE_ATTACHMENTS, parameters.getPageMode());
    }

    @Test
    public void testDefaults() {
        ViewerPreferencesParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());

        assertEquals(PdfPrintScaling.APP_DEFAULT, parameters.getPrintScaling());
        assertEquals(PdfDirection.LEFT_TO_RIGHT, parameters.getDirection());
        assertEquals(PdfDuplex.SIMPLEX, parameters.getDuplex());
        assertEquals(PdfNonFullScreenPageMode.USE_NONE, parameters.getNfsMode());
        assertEquals(PdfPageLayout.SINGLE_PAGE, parameters.getPageLayout());
        assertEquals(PdfPageMode.USE_NONE, parameters.getPageMode());
    }
}