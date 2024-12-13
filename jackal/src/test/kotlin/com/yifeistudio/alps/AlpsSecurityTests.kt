package com.yifeistudio.alps

import com.yifeistudio.alps.security.CredentialRequest
import com.yifeistudio.alps.security.PrincipalsReply
import com.yifeistudio.alps.security.SecurityServiceGrpc
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AlpsSecurityTests {

    class FakeAlpsSecurityServer : SecurityServiceGrpc.SecurityServiceImplBase() {

        override fun getAccountPrincipalsByAccessToken(
            req: CredentialRequest,
            resObserver: StreamObserver<PrincipalsReply>
        ) {
            resObserver.onNext(fakePrincipalsReply)
            resObserver.onCompleted()
        }

        override fun getAccountPrincipalsByTicket(
            req: CredentialRequest,
            resObserver: StreamObserver<PrincipalsReply>
        ) {
            resObserver.onNext(fakePrincipalsReply)
            resObserver.onCompleted()
        }
    }

    @Test
    fun getAccountPrincipalsByAccessTokenTest() {
        val result = CredentialRequest.newBuilder().setCredential("fake-access-token").build()
            .let { stub.getAccountPrincipalsByAccessToken(it) }
        assertEquals(result, fakePrincipalsReply)
    }

    @Test
    fun getAccountPrincipalsByTicketTest() {
        val result = CredentialRequest.newBuilder().setCredential("fake-ticket").build()
            .let { stub.getAccountPrincipalsByTicket(it) }
        assertEquals(result, fakePrincipalsReply)
    }

    companion object {

        private val serverName = InProcessServerBuilder.generateName()
        private val server = InProcessServerBuilder.forName(serverName).addService(FakeAlpsSecurityServer()).build().start()
        private val channel = InProcessChannelBuilder.forName(serverName).build()
        private val stub = SecurityServiceGrpc.newBlockingStub(channel)
        private val fakePrincipalsReply = PrincipalsReply.newBuilder()
            .setAvatar("https://fack-url")
            .setUsername("fake name")
            .setNickname("fake nickname")
            .build()

        @JvmStatic
        @AfterAll
        fun cleanup(): Unit {
            server.shutdown()
        }
    }

}