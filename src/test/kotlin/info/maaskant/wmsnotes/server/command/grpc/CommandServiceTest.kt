package info.maaskant.wmsnotes.server.command.grpc

import info.maaskant.wmsnotes.model.CreateNoteCommand
import info.maaskant.wmsnotes.server.command.CommandService
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CommandServiceTest {
    @Test
    fun `command not set`() {
        // Given
        val request = with(Command.PostCommandRequest.newBuilder()) {
            noteId = "note-1"
            build()
        }

        // When
        val thrown = catchThrowable { CommandService.toModelCommand(request) }

        // Then
        assertThat(thrown).isInstanceOf(CommandService.BadRequestException::class.java)
    }

    @Test
    fun `note_id not set`() {
        // Given
        val request = with(Command.PostCommandRequest.newBuilder()) {
            createNoteBuilder.title = "Title 1"
            build()
        }

        // When
        val thrown = catchThrowable { CommandService.toModelCommand(request)  }

        // Then
        assertThat(thrown).isInstanceOf(CommandService.BadRequestException::class.java)
    }

    @Test
    fun `create`() {
        // Given
        val request = with(Command.PostCommandRequest.newBuilder()) {
            noteId = "note-1"
            createNoteBuilder.title = "Title 1"
            build()
        }

        // When
        val command = CommandService.toModelCommand(request) as CreateNoteCommand

        // Then
        assertThat(command.noteId).isEqualTo(request.noteId)
        assertThat(command.title).isEqualTo(request.createNote.title)
    }
}