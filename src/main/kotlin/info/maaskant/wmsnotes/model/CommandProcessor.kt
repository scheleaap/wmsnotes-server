package info.maaskant.wmsnotes.model

import info.maaskant.wmsnotes.server.logger
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CommandProcessor(private val eventStore: EventStore) {

    private val logger by logger()

    init {
//        processCommand(CreateNoteCommand("note-1", "Title 1"))
//        processCommand(CreateNoteCommand("note-2", "Title 2"))
    }

    fun processCommand(command: Command): Event? {
        return Observable
                .just(command)
                .compose(processCommands())
                .blockingSingle()
                .value
    }

    private fun processCommands(): ObservableTransformer<Command, Optional<Event>> {
        return ObservableTransformer { it2 ->
            it2.doOnNext { logger.debug("Received command: $it") }
                    .map(this::executeCommand)
//                    .compose(removeEmptyOptionalItems())
                    .observeOn(Schedulers.io())
                    .doOnNext { storeEventIfPresent(it) }
                    .doOnNext { logEventIfPresent(it) }
        }
    }

    private fun logEventIfPresent(e: Optional<Event>) {
        if (e.value != null) {
            logger.debug("Generated event: $e.value")
        }
    }

    private fun storeEventIfPresent(e: Optional<Event>) {
        if (e.value != null) {
            logger.debug("Storing event: $e.value")
            val throwable: Throwable? = eventStore.storeEvent(e.value).blockingGet()
            if (throwable != null) {
                throw throwable
            }
        }
    }

    private fun executeCommand(c: Command): Optional<Event> {
        return when (c) {
            is CreateNoteCommand -> Optional(createNote(c))
            is DeleteNoteCommand -> Optional(deleteNote(c))
            // else -> Optional.empty()
        }
    }

    private fun createNote(c: CreateNoteCommand): Event {
        return NoteCreatedEvent(eventId = 1, noteId = c.noteId, title = c.title)
    }

    private fun deleteNote(c: DeleteNoteCommand): Event {
        return NoteDeletedEvent(eventId = 1, noteId = c.noteId)
    }
}