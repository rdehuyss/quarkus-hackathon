package org.jobrunr.examples.salaryslip;

import org.jboss.resteasy.annotations.providers.multipart.PartFilename;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class DocumentGenerationRestApiRequest {

    @FormParam("files")
    @PartFilename("index.html")
    @PartType(MediaType.TEXT_HTML)
    public InputStream file;
}
