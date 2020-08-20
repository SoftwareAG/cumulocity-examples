package c8y.example.hellokotlin

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloKotlinController {

    @RequestMapping("hello")
    fun greeting(@RequestParam(value = "who", defaultValue = "admin") who: String): String {
        return "Hello $who in Kotlin world!"
    }
}
