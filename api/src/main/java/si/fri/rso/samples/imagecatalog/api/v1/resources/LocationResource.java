package si.fri.rso.samples.imagecatalog.api.v1.resources;

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

    @Operation(description = "Get all image metadata.", summary = "Get all metadata")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of image metadata",
                    content = @Content(schema = @Schema(implementation = Location.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getImageMetadata() {

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
    public Response getImageMetadata(@Parameter(description = "Metadata ID.", required = true)
                                     @PathParam("locationId") Integer locationId) {

        Location location = LocationBean.getLocation(locationId);

        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(location).build();
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
        }

        return Response.status(Response.Status.CONFLICT).entity(location).build();

    }


    @Operation(description = "Update metadata for an image.", summary = "Update metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully updated."
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

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @Operation(description = "Delete metadata for an image.", summary = "Delete metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{locationId}")
    public Response deleteImageMetadata(@Parameter(description = "Location ID.", required = true)
                                        @PathParam("locationId") Integer locationId){

        boolean deleted = LocationBean.deleteLocation(locationId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }





}
