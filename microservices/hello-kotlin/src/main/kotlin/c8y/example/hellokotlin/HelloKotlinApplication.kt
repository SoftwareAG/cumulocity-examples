package c8y.example.hellokotlin

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication
import org.springframework.boot.runApplication

@MicroserviceApplication
class HelloKotlinApplication

fun main(args: Array<String>) {
    runApplication<HelloKotlinApplication>(*args)
}


