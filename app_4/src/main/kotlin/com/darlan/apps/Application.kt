package com.darlan.apps

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate

@Configuration
@SpringBootApplication
class App4Configuration {

	@Bean
	fun getRestTemplate(): RestTemplate {
		return RestTemplate()
	}

}

@RestController
class App4Controller(val restTemplate: RestTemplate) {

	@Value("\${spring.servers.app5.base-url}")
	lateinit var app5Url: String

	@PostMapping("/message")
	fun message(@RequestBody message: App4Message): App4Message {
		val headers = HttpHeaders()
		headers.accept = mutableListOf(MediaType.APPLICATION_JSON)
		val entity: HttpEntity<App4Message> = HttpEntity(message, headers)
		restTemplate.exchange("$app5Url/message",
				HttpMethod.POST, entity, String::class.java)
		return message;
	}

}

data class App4Message(val message: String)

fun main(args: Array<String>) {
	runApplication<App4Configuration>(*args)
}
