package sys.bac.adapters.in.api.adapter.device;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
    
    @Context
    Request request;
    
    
    private CacheControl defaultGetCacheControl() {
        CacheControl cc = new CacheControl();
        cc.setPrivate(true);
        cc.setMaxAge(30);
        cc.setMustRevalidate(true); 
        return cc;
    }
    
    
    private CacheControl noStore() {
        CacheControl cc = new CacheControl();
        cc.setNoStore(true);
        cc.setNoCache(true);
        return cc;
    }
    
    private EntityTag etagOf(Object... parts) {
        String value = Integer.toHexString(Objects.hash(parts));
        return new EntityTag(value, true);
    }
    
    @GET
    @Path("{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceById(@Positive @PathParam("deviceId") long id) {
        DeviceDTO device = dSA.getDeviceById(id);
        device = addSelfLink(device, "getDeviceWithId" + device.getId());
        
        EntityTag etag = etagOf(device);
        
        Response.ResponseBuilder precond = request.evaluatePreconditions(etag);
        if (precond != null) {
            return dashboard(precond)
            .cacheControl(defaultGetCacheControl())
            .tag(etag)
            .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", new Link(Link.devices.getHref() + "/" + id, "updateDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", new Link(Link.devices.getHref() + "/" + id, "deleteDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .build();
        }
        
        return dashboard(Response.ok(device))
        .cacheControl(defaultGetCacheControl())
        .tag(etag)
        .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "updateDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "deleteDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@DefaultValue("") @QueryParam("query") String query,
    @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
    @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
    @PositiveOrZero @DefaultValue("20") @QueryParam("size") int size) {
        size = Math.min(size, 100);
        if (page > 0 && offset == 0) {
            offset = page * size;
        }
        
        DevicesApiResult devices = dSA.getDevices(query, offset, size);
        devices.setResult(devices.getResult().stream().map(d -> addSelfLink(d, "getDeviceWithId" + d.getId())).collect(Collectors.toList()));
        Response.ResponseBuilder builder = Response.ok(devices.getResult());
        addPaginationHeaders(builder, Link.devices.getHref(), query, offset, size, devices.getTotalElements(), devices.next(), devices.prev());

        if(!query.isBlank()) {
            builder.header("Link", new Link(Link.devices.getHref(), "clearQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
        }

        return dashboard(builder)
        .header("Link", new Link(Link.devices.getHref() + "?query={query}", "getNewDeviceQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.devices.getHref(), "createDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDevice(@Valid DeviceDTO device) {
        DeviceDTO result = dSA.createDevice(device);
        
        return Response.status(Response.Status.CREATED)
        .header("Location", new Link(Link.devices.getHref() + "/" + result.getId(), "getDevice", "application/json").getHeaderHref(uriInfo.getBaseUri().toString()))
        .cacheControl(noStore())
        .build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDevice(@Positive @PathParam("id") long id, @Valid DeviceDTO device, @HeaderParam("If-Match") String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            return Response.status(428)
            .cacheControl(noStore())
            .entity("Missing If-Match header. Fetch the resource first (GET) and resend PUT with If-Match: <ETag>.")
            .type(MediaType.TEXT_PLAIN)
            .build();
        }
        DeviceDTO current = dSA.getDeviceById(id);
        current = addSelfLink(current, "getDeviceWithId" + current.getId());
        EntityTag currentEtag = etagOf(current);
        
        Response.ResponseBuilder precond = request.evaluatePreconditions(currentEtag);
        if (precond != null) {
            return precond
            .cacheControl(noStore())
            .tag(currentEtag)
            .header("Link", new Link(Link.devices.getHref() + "/" + id, "getDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .build();
        }
        dSA.updateDevice(id, device);
        
        return Response.noContent()
        .cacheControl(noStore())
        .header("Link", new Link(Link.devices.getHref() + "/" + id, "getDevice", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteDevice(@Positive @PathParam("id") long id) {
        dSA.deleteDevice(id);
        return Response.noContent()
        .cacheControl(noStore())
        .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    private DeviceDTO addSelfLink(DeviceDTO dto, String rel) {
        DeviceDTO copy = new DeviceDTO(dto.getId(), dto.getCustomerId(), dto.getSerialNumber(), dto.getType(), dto.getBrand(), dto.getModel(), dto.getNotes());
        copy.setSelf(new Link(uriInfo.getBaseUri().toString() + "devices" + "/" + dto.getId(), rel, "application/json"));
        return copy;
    }

    private void addPaginationHeaders(Response.ResponseBuilder builder, String resourceHref, String query, int offset, int size, long totalElements, boolean hasNext, boolean hasPrevious) {
        int currentPage = size > 0 ? offset / size : 0;
        long totalPages = size > 0 ? (long) Math.ceil((double) totalElements / size) : 0;
        int lastOffset = totalPages > 0 ? (int) ((totalPages - 1) * size) : 0;

        builder.header("X-Total-Count", totalElements)
        .header("X-Total-Pages", totalPages)
        .header("X-Page-Size", size)
        .header("X-Current-Page", currentPage)
        .header("X-Current-Offset", offset)
        .header("Link", new Link(buildListHref(resourceHref, 0, size, query), "first", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(buildListHref(resourceHref, lastOffset, size, query), "last", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));

        if (hasPrevious) {
            builder.header("Link", new Link(buildListHref(resourceHref, Math.max(offset - size, 0), size, query), "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
        }

        if (hasNext) {
            builder.header("Link", new Link(buildListHref(resourceHref, offset + size, size, query), "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
        }
    }

    private String buildListHref(String resourceHref, int offset, int size, String query) {
        int page = size > 0 ? offset / size : 0;
        String href = resourceHref + "?page=" + page + "&offset=" + offset + "&size=" + size;

        if (query != null && !query.isBlank()) {
            href += "&query=" + encode(query);
        }
        return href;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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

    private Response.ResponseBuilder dashboard(
        Response.ResponseBuilder builder) {
            
            return builder
            .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.home.getHeaderLink(uriInfo.getBaseUri().toString()));
    }
}
