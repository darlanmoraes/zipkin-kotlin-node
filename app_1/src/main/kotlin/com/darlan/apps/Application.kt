package com.darlan.apps

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.HashMap
import java.util.concurrent.atomic.AtomicLong

import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG

import com.darlan.apps.App1Configuration.Companion.PRODUCER_TOPIC
import org.springframework.context.annotation.Primary

@Configuration
@SpringBootApplication
class App1Configuration {

	@Value("\${spring.kafka.bootstrap-servers}")
	lateinit var bootstrapAddress: String

	@Bean
	@Primary
	fun producerFactory(): ProducerFactory<String, String> {
		val config = HashMap<String, Any>()
		config[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
		config[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
		config[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
		return DefaultKafkaProducerFactory(config)
	}

	@Bean
	@Primary
	fun kafkaTemplate(): KafkaTemplate<String, String> {
		return KafkaTemplate(producerFactory())
	}

	companion object {
		const val PRODUCER_TOPIC = "TOPIC_1"
	}
}

@RestController
class App1Controller(val kafka: KafkaTemplate<String, String>) {

	val counter = AtomicLong()
	val mapper = jacksonObjectMapper()

	@GetMapping("/message")
	fun message(): App1Message {
		val message = App1Message("id = ${counter.incrementAndGet()}")
		kafka.send(PRODUCER_TOPIC, mapper.writeValueAsString(message))
		return message;
	}

}

data class App1Message(val message: String)

fun main(args: Array<String>) {
	runApplication<App1Configuration>(*args)
}
