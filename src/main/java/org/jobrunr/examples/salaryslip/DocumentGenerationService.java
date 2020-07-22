package org.jobrunr.examples.salaryslip;

import io.quarkus.qute.Template;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class DocumentGenerationService {

    @Inject
    @RestClient
    DocumentGenerationRestApi service;

    public void generateDocument(Template template, Path pdfOutputPath, Object context) throws IOException {
        Files.createDirectories(pdfOutputPath.getParent().toAbsolutePath());

        final String result = template
                .data("context", context)
                .render();

        try (OutputStream out = Files.newOutputStream(pdfOutputPath)) {
            DocumentGenerationRestApiRequest body = new DocumentGenerationRestApiRequest();
            body.file = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            final Response response = service.sendMultipartData(body);
            final InputStream inputStream = response.readEntity(InputStream.class);
            copyStream(inputStream, out);
            System.out.println(String.format("Generated salary slip %s", pdfOutputPath)); // for demo purposes only
        }

    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
