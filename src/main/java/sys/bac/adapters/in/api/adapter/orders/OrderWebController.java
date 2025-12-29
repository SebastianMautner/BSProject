package sys.bac.adapters.in.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sys.bac.adapters.in.api.adapter.OrderRepresentationAdapter;
import sys.bac.adapters.in.api.exceptions.ApiProblem;
import sys.bac.adapters.in.api.models.OrderCreateModel;
import sys.bac.application.port.in.OrderDispatcherPort;
import sys.bac.application.port.in.commands.CreateOrderCommand;
import sys.bac.application.port.in.commands.UpdateStatusCommand;
import sys.bac.application.port.in.commands.FinalizeOrderCommand;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderWebController {

    @Inject
    OrderDispatcherPort dispatcher;

    @GET
    @Path("/orders")
    public Response getOrders(
            @QueryParam("deviceId") Long deviceId,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @Context UriInfo uriInfo
    ) {
        var result = dispatcher.searchOrders(deviceId, status, page, size);
        if (!result.isOk()) return Response.status(result.httpStatus()).entity(new ApiProblem(result.code(), result.message())).build();

        var body = OrderRepresentationAdapter.toListModel(result.value(), page, size, result.hasNext(), uriInfo);
        return Response.ok(body).build();
    }

    @GET
    @Path("/orders/{id}")
    public Response getOrderById(@PathParam("id") long id, @Context UriInfo uriInfo) {
        var result = dispatcher.getOrder(id);
        if (!result.isOk()) return Response.status(result.httpStatus()).entity(new ApiProblem(result.code(), result.message())).build();

        return Response.ok(OrderRepresentationAdapter.toModel(result.value(), uriInfo)).build();
    }

    @POST
    @Path("/orders")
    public Response createOrder(OrderCreateModel req, @Context UriInfo uriInfo) {
        var cmd = new CreateOrderCommand(req.customerId(), req.deviceId(), req.problemDescription(), req.costEstimate());
        var result = dispatcher.createOrder(cmd);
        if (!result.isOk()) return Response.status(result.httpStatus()).entity(new ApiProblem(result.code(), result.message())).build();

        var created = result.value();
        return Response.status(Response.Status.CREATED)
                .entity(OrderRepresentationAdapter.toModel(created, uriInfo))
                .build();
    }

    @PUT
    @Path("/orders/{id}/status")
    public Response updateStatus(@PathParam("id") long id, String newStatus, @Context UriInfo uriInfo) {
        var cmd = new UpdateStatusCommand(id, newStatus);
        var result = dispatcher.updateStatus(cmd);
        if (!result.isOk()) return Response.status(result.httpStatus()).entity(new ApiProblem(result.code(), result.message())).build();

        return Response.ok(OrderRepresentationAdapter.toModel(result.value(), uriInfo)).build();
    }

    @POST
    @Path("/orders/{id}/finalize")
    public Response finalizeOrder(@PathParam("id") long id, String finalCost, @Context UriInfo uriInfo) {
        var cmd = new FinalizeOrderCommand(id, finalCost);
        var result = dispatcher.finalizeOrder(cmd);
        if (!result.isOk()) return Response.status(result.httpStatus()).entity(new ApiProblem(result.code(), result.message())).build();

        return Response.ok(OrderRepresentationAdapter.toModel(result.value(), uriInfo)).build();
    }
}

