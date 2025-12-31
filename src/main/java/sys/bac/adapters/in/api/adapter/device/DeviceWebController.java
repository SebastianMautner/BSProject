package sys.bac.adapters.in.api.adapter.device;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.adapters.in.api.models.DevicesApiResult;
import sys.bac.adapters.in.api.models.Link;

@Path("devices")
public class DeviceWebController {
    
    @Inject
    private DeviceServiceAdapter dSA;
    
    @GET
    @Path("{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceById(@PathParam("deviceId") long id) {
        DeviceDTO device = dSA.getDeviceById(id);
        device = addSelfLink(device, "getDeviceWithId" + device.getId());
        
        return Response.ok(device)
        .header("Link", Link.devices.getHeaderLink())
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "updateDevice", "application/json").getHeaderLink())
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "deleteDevice", "application/json").getHeaderLink())
        .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@DefaultValue("") @QueryParam("q") String query,
    @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
    @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) {
        size = Math.min(size, 100);
        
        DevicesApiResult devices = dSA.getDevices(query, offset, size);
        devices.setResult(devices.getResult().stream().map(d -> addSelfLink(d, "getDeviceWithId" + d.getId())).collect(Collectors.toList()));
        Response.ResponseBuilder builder = Response.ok(devices.getResult());
        
        if (devices.next() && devices.prev()) {
            builder = builder
            .header("Link", new Link(Link.devices.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink())
            .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
            
        } else if(devices.next()) {
            builder = builder
            .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
            
        } else if(devices.prev()) {
            builder = builder
            .header("Link", new Link(Link.devices.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink());
        }
        
        return builder
        .header("Link", Link.customers.getHeaderLink())
        .header("Link", Link.orders.getHeaderLink())
        .header("Link", new Link(Link.devices.getHref(), "createDevice", "application/json").getHeaderLink())
        .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDevice(@Valid DeviceDTO device) {
        DeviceDTO result = dSA.createDevice(device);
        
        return Response.status(Response.Status.CREATED)
        .header("Location", new Link(Link.devices.getHref() + "/" + result.getId(), "getDevice", "application/json").getHeaderLink())
        .build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDevice(@Positive @PathParam("id") long id, @Valid DeviceDTO device) {
        dSA.updateDevice(id, device);
        return Response.noContent()
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "getDevice", "application/json").getHeaderLink())
        .build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteDevice(@Positive @PathParam("id") long id) {
        dSA.deleteDevice(id);
        return Response.noContent()
        .header("Link", Link.devices.getHeaderLink())
        .build();
    }
    
    private DeviceDTO addSelfLink(DeviceDTO dto, String rel) {
        dto.setSelf(new Link( "http://localhost:8080/" + Link.devices.getHref() + "/" + dto.getId(), rel, "application/json"));
        return dto;
    }
}

