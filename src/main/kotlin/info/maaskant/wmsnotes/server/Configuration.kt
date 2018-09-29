package info.maaskant.wmsnotes.server

import com.esotericsoftware.kryo.Kryo
import info.maaskant.wmsnotes.model.EventStore
import info.maaskant.wmsnotes.model.CommandProcessor
import info.maaskant.wmsnotes.model.serialization.EventSerializer
import info.maaskant.wmsnotes.model.serialization.KryoEventSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean
    fun eventSerializer(): EventSerializer = KryoEventSerializer()

    @Bean
    fun eventStore(eventSerializer: EventSerializer): EventStore = EventStore(eventSerializer)

    @Bean
    fun model(eventStore: EventStore): CommandProcessor = CommandProcessor(eventStore)

}