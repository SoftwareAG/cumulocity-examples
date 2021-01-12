package c8y.example.hellokotlin.setup

import com.cumulocity.microservice.security.service.SecurityUserDetails
import io.mockk.every
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class SecurityMocksInitializer {

    companion object {
        val ROLES = listOf("ROLE_HELLO_KOTLIN_ADMIN")
        const val TENANT = "t1000"
        const val USER = "johannes"
        const val PASSWORD = "123456"
    }

    fun init(service: UserDetailsService) {
        every { service.loadUserByUsername(USER) } returns SecurityUserDetails.activeUser(TENANT, USER, PASSWORD, ROLES)
        every { service.loadUserByUsername("$TENANT/$USER") } returns SecurityUserDetails.activeUser(TENANT, USER, PASSWORD, ROLES)
    }
}
