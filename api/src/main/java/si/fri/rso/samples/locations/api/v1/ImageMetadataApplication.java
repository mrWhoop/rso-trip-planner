package si.fri.rso.samples.locations.api.v1;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Trip planner location API", version = "v1",
        contact = @Contact(email = "rm1052@student.uni-lj.si"),
        license = @License(name = "dev"), description = "API for managing location data."),
        servers = @Server(url = "http://20.75.151.253:8080/"))
        //servers = @Server(url = "http://localhost:8080/"))
@ApplicationPath("/v1")
public class ImageMetadataApplication extends Application {

}
