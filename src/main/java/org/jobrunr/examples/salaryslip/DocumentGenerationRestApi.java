package org.jobrunr.examples.salaryslip;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/convert/html")
@RegisterRestClient
public interface DocumentGenerationRestApi {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/pdf")
    Response sendMultipartData(@MultipartForm DocumentGenerationRestApiRequest data);

}
