package info.maaskant.wmsnotes.server

import org.davidmoten.rx.jdbc.Database

//    private val database = Database.from("jdbc:sqlite::memory:", 5)
val database = Database.from("jdbc:sqlite:database.db", 5)

