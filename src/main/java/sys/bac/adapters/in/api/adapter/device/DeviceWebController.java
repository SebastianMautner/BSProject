package sys.bac.adapters.in.api.adapter.device;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.adapters.in.api.models.DevicesApiResult;
import sys.bac.adapters.in.api.models.Link;

@Path("devices")
public class DeviceWebController {
    
    @Inject
    private DeviceServiceAdapter dSA;

    @Context
    private UriInfo uriInfo;
    private URI uri = uriInfo.getAbsolutePath();
    
    @GET
    @Path("{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceById(@Positive @PathParam("deviceId") long id) {
        DeviceDTO device = dSA.getDeviceById(id);
        device = addSelfLink(device, "getDeviceWithId" + device.getId());
        
        return Response.ok(device)
        .header("Link", Link.devices.getHeaderLink(uri))
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "updateDevice", "application/json").getHeaderLink(uri))
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "deleteDevice", "application/json").getHeaderLink(uri))
        .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@DefaultValue("") @QueryParam("query") String query,
    @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
    @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) {
        size = Math.min(size, 100);
        
        DevicesApiResult devices = dSA.getDevices(query, offset, size);
        devices.setResult(devices.getResult().stream().map(d -> addSelfLink(d, "getDeviceWithId" + d.getId())).collect(Collectors.toList()));
        Response.ResponseBuilder builder = Response.ok(devices.getResult());
        if(query.isBlank()) {
            if (devices.next() && devices.prev()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink(uri))
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uri));
                
            } else if(devices.next()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uri));
                
            } else if(devices.prev()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink(uri));
            }
        }
        else {
            if (devices.next() && devices.prev()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uri))
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uri));
                
            } else if(devices.next()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uri));
                
            } else if(devices.prev()) {
                builder = builder
                .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset - size) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uri));
            }
            builder.header("Link", new Link(Link.devices.getHref(), "clearQuery", "application/json").getHeaderLink(uri));
        }
        return builder
        .header("Link", new Link(Link.devices.getHref() + "?query={query}", "getNewDeviceQuery", "application/json").getHeaderLink(uri))
        .header("Link", Link.customers.getHeaderLink(uri))
        .header("Link", Link.orders.getHeaderLink(uri))
        .header("Link", new Link(Link.devices.getHref(), "createDevice", "application/json").getHeaderLink(uri))
        .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDevice(@Valid DeviceDTO device) {
        DeviceDTO result = dSA.createDevice(device);
        
        return Response.status(Response.Status.CREATED)
        .header("Location", new Link(Link.devices.getHref() + "/" + result.getId(), "getDevice", "application/json").getHeaderLink(uri))
        .build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDevice(@Positive @PathParam("id") long id, @Valid DeviceDTO device) {
        dSA.updateDevice(id, device);
        return Response.noContent()
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "getDevice", "application/json").getHeaderLink(uri))
        .build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteDevice(@Positive @PathParam("id") long id) {
        dSA.deleteDevice(id);
        return Response.noContent()
        .header("Link", Link.devices.getHeaderLink(uri))
        .build();
    }
    
    private DeviceDTO addSelfLink(DeviceDTO dto, String rel) {
        dto.setSelf(new Link(uriInfo.getAbsolutePath().toString() + Link.customers.getHref() + "/" + dto.getId(), rel, "application/json"));
        return dto;
    }

    @DELETE
        public void ErrorDelete() {
                throw new NotAllowedException("No DELETE for path devices/");
        }
        
        @PUT
        public void ErrorUpdate() {
                throw new NotAllowedException("No PUT for path devices/");
        }
        
        @POST
        @Path("{id}")
        public void ErrorPost() {
                throw new NotAllowedException("No POST for path devices/id");
        }
}

