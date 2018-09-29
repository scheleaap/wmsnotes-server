package info.maaskant.wmsnotes.server.event

import info.maaskant.wmsnotes.model.EventStore
import info.maaskant.wmsnotes.server.api.GrpcConverters
import info.maaskant.wmsnotes.server.command.grpc.Event
import info.maaskant.wmsnotes.server.command.grpc.EventServiceGrpc
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import info.maaskant.wmsnotes.server.logger

@GRpcService
class EventService(private val eventStore: EventStore) : EventServiceGrpc.EventServiceImplBase() {

    private val logger by logger()

    override fun getEvents(
            request: Event.GetEventsRequest,
            responseObserver: StreamObserver<Event.GetEventsResponse>
    ) {
        eventStore.getEvents().subscribe(
                { responseObserver.onNext(GrpcConverters.toGrpcClass(it)) },
                {
                    logger.warn("Internal error", it)
                    responseObserver.onError(it)
                },
                { responseObserver.onCompleted() }
        )

    }

}