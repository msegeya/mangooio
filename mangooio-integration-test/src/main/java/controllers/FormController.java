package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Form;
    
public class FormController {
    private static final Logger LOG = LogManager.getLogger(FormController.class);
    private static final int MIN_SIZE = 11;
    private static final int MAX_SIZE = 12;

    @Routing(method = "POST", url = "/form")
    public Response form() {
        return Response.withOk();
    }
    
    @Routing(method = "POST", url = "/singlefile")
    public Response singlefile(Form form) {
        String content = "";
        Optional<File> formFile = form.getFile();
        if (formFile.isPresent()) {
            File file = formFile.get();
            try {
                content = Files.readFirstLine(file, Charset.defaultCharset());
            } catch (IOException e) {
                LOG.error("Failed to read single file", e);
            }
        }
        
        return Response.withOk().andTextBody(content);
    }
    
    @SuppressWarnings("all")
    @Routing(method = "POST", url = "/multifile")
    public Response multifile(Form form) {
        String content = "";
        List<File> files = form.getFiles();
        for (File file : files) {
            try {
                content = content + Files.readFirstLine(file, Charset.defaultCharset());
            } catch (IOException e) {
                LOG.error("Failed to one of multiple files", e);
            }
        }
        
        return Response.withOk().andTextBody(content + files.size());
    }

    @Routing(method = "POST", url = "/validateform")
    public Response validateform(Form form) {
        form.expectValue("name");
        form.expectEmail("email");
        form.expectExactMatch("password", "passwordconfirm"); //NOSONAR
        form.expectMatch("email2", "email2confirm");
        form.expectIpv4("ipv4");
        form.expectIpv6("ipv6");
        form.expectRegex("regex", Pattern.compile("[a-z]"));
        form.expectMax("phone", MAX_SIZE);
        form.min("fax", MIN_SIZE);

        if (form.isValid()) {
            return Response.withOk().andTextBody("Fancy that!");
        }

        return Response.withOk();
    }
    
    @Routing(method = "GET", url = "/flashify")
    public Response flashify() {
        return Response.withOk();
    }
    
    @Routing(method = "POST", url = "/submit")
    public Response submit(Form form) {
        form.keep();
        
        return Response.withRedirect("/flashify");
    }
}