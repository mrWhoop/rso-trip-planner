package si.fri.rso.samples.locations.api.v1.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.locations.lib.Location;
import si.fri.rso.samples.locations.services.beans.LocationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;


@Log
@ApplicationScoped
@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    private Logger log = Logger.getLogger(LocationResource.class.getName());

    @Inject
    private LocationBean LocationBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get list of all locations.", summary = "Get all locations")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of locations",
                    content = @Content(schema = @Schema(implementation = Location.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getLocation() {

        List<Location> location = LocationBean.getLocationFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(location).build();
    }


    @Operation(description = "Get data for location.", summary = "Get location data")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Location data",
                    content = @Content(
                            schema = @Schema(implementation = Location.class))
            )})
    @GET
    @Path("/{locationId}")
    public Response getLocation(@Parameter(description = "Metadata ID.", required = true)
                                     @PathParam("locationId") Integer locationId) throws IOException, InterruptedException {

        Location location = LocationBean.getLocation(locationId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://open-weather13.p.rapidapi.com/city/london"))
                .header("X-RapidAPI-Key", "47186bcab3msh1a11d665d36372ap1383fcjsn04dec11a4e11")
                .header("X-RapidAPI-Host", "open-weather13.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String locationJSON = ow.writeValueAsString(location);

        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"location\":").append(locationJSON).append(",");
        sb.append(response.body()).append("}");

        return Response.status(Response.Status.OK).entity(sb.toString()).build();
    }

    @Operation(description = "Add location.", summary = "Add location")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Location successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createLocation(@RequestBody(
            description = "DTO object with location data.",
            required = true, content = @Content(
            schema = @Schema(implementation = Location.class))) Location location) {

        if ((location.getTitle() == null || location.getDescription() == null || location.getUri() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            location = LocationBean.createLocation(location);
            return Response.status(Response.Status.CREATED).entity(location).build();
        }

        // return Response.status(Response.Status.CONFLICT).entity(location).build();

    }


    @Operation(description = "Update data for a location.", summary = "Update data")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Location data successfully updated."
            )
    })
    @PUT
    @Path("{locationId}")
    public Response putLocation(@Parameter(description = "Location ID.", required = true)
                                     @PathParam("locationId") Integer imageMetadataId,
                                     @RequestBody(
                                             description = "DTO object with location data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = Location.class)))
                                             Location location){

        location = LocationBean.putLocation(imageMetadataId, location);

        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{
           return Response.status(Response.Status.OK).build();
        }
        // return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @Operation(description = "Delete data for a location.", summary = "Delete location")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Location successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{locationId}")
    public Response deleteLocation(@Parameter(description = "Location ID.", required = true)
                                        @PathParam("locationId") Integer locationId){

        boolean deleted = LocationBean.deleteLocation(locationId);

        if (deleted) {
            return Response.status(Response.Status.OK).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
