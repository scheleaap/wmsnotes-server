package info.maaskant.wmsnotes.server.command

import info.maaskant.wmsnotes.model.*
import info.maaskant.wmsnotes.server.command.grpc.Command
import info.maaskant.wmsnotes.server.command.grpc.CommandServiceGrpc
import info.maaskant.wmsnotes.server.logger
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class CommandService(private val commandProcessor: CommandProcessor) : CommandServiceGrpc.CommandServiceImplBase() {

    override fun postCommand(
            request: Command.PostCommandRequest,
            responseObserver: StreamObserver<Command.PostCommandResponse>
    ) {
        try {
            val command = toModelCommand(request)
            val event = commandProcessor.processCommand(command)
            val response = Command.PostCommandResponse.newBuilder()
                    .setStatus(when (event) {
                        null -> Command.PostCommandResponse.Status.INTERNAL_ERROR // TODO
                        else -> Command.PostCommandResponse.Status.SUCCESS
                    })
                    .build()
            responseObserver.onNext(response)
        } catch (e: Throwable) {
            responseObserver.onNext(toErrorResponse(e))
        }
        responseObserver.onCompleted()
    }

    companion object {

        private val logger by logger()

        private fun toErrorResponse(e: Throwable): Command.PostCommandResponse {
            val responseBuilder = Command.PostCommandResponse.newBuilder()
            if (e is BadRequestException) {
                logger.info("Bad request: ${e.message}")
                responseBuilder.setStatus(Command.PostCommandResponse.Status.BAD_REQUEST).setErrorDescription(e.message)
            } else {
                logger.warn("Internal error", e)
                responseBuilder.setStatus(Command.PostCommandResponse.Status.INTERNAL_ERROR).setErrorDescription("Internal errror")
            }
            return responseBuilder.build()
        }

        fun toModelCommand(request: Command.PostCommandRequest): info.maaskant.wmsnotes.model.Command {
            if (request.noteId.isEmpty()) throw BadRequestException("Field 'note_id' must not be empty")
            return when (request.commandCase!!) {

                Command.PostCommandRequest.CommandCase.CREATE_NOTE -> CreateNoteCommand(
                        noteId = request.noteId,
                        title = request.createNote.title
                )
                Command.PostCommandRequest.CommandCase.DELETE_NOTE -> DeleteNoteCommand(
                        noteId = request.noteId
                )
                Command.PostCommandRequest.CommandCase.COMMAND_NOT_SET -> throw BadRequestException("Field 'command' not set")
            }
        }
    }

    class BadRequestException(description: String) : Exception(description)
}