package com.darlan.apps

import com.darlan.apps.App3Configuration.Companion.CONSUMER_TOPIC
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*


@Configuration
@SpringBootApplication
class App3Configuration {

	@Value("\${spring.kafka.bootstrap-servers}")
	lateinit var bootstrapAddress: String

	@Bean
	fun consumerFactory(): ConsumerFactory<String?, String?> {
		val config: MutableMap<String, Any> = HashMap()
		config[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
		config[GROUP_ID_CONFIG] = "app_3"
		config[KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
		config[VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
		return DefaultKafkaConsumerFactory(config)
	}

	@Bean
	fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>? {
		val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
		factory.consumerFactory = consumerFactory()
		return factory
	}

	@Bean
	fun getRestTemplate(): RestTemplate {
		return RestTemplate()
	}

	companion object {
		const val CONSUMER_TOPIC = "TOPIC_2"
	}
}

@Component
class App3Controller(val restTemplate: RestTemplate) {

	@Value("\${spring.servers.app4.base-url}")
	lateinit var app4Url: String

	val mapper = jacksonObjectMapper()

	@KafkaListener(topics = [ CONSUMER_TOPIC ], containerFactory = "kafkaListenerContainerFactory")
	fun listen(json: String) {
		val message = mapper.readValue(json, App3Message::class.java)
		val headers = HttpHeaders()
		headers.accept = mutableListOf(MediaType.APPLICATION_JSON)
		val entity: HttpEntity<App3Message> = HttpEntity(message, headers)
		restTemplate.exchange("$app4Url/message",
				HttpMethod.POST, entity, String::class.java)
	}

}

data class App3Message(val message: String)

fun main(args: Array<String>) {
	runApplication<App3Configuration>(*args)
}
