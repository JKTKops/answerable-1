package edu.illinois.cs.cs125.answerablewebdemo


import edu.illinois.cs.cs125.answerable.TestGenerator
import edu.illinois.cs.cs125.answerable.api.Solution
import edu.illinois.cs.cs125.answerable.api.bytecodeProvider
import edu.illinois.cs.cs125.jeed.core.*
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.lang.IllegalStateException
import kotlin.random.Random

fun main() {

    val server = embeddedServer(Netty, 8080 /*System.getenv("ANSWERABLE_DEMO_PORT")?.toInt()*/
        ?: throw IllegalStateException("ANSWERABLE_DEMO_PORT not provided.")) {

        install(ContentNegotiation) {
            jackson()
        }
        routing {
            options("/") {
                call.response.header("Access-Control-Allow-Origin", "*") // don't deploy this
                call.response.header("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token")
                call.response.status(HttpStatusCode.OK)
            }

            post("/") {
                call.response.header("Access-Control-Allow-Origin", "*") // don't deploy this
                val received = call.receive<AnswerableDemoPost>()
                println(received)
                call.response.status(HttpStatusCode.OK)
            }
        }
    }

    server.start(wait = true)

}

data class AnswerableDemoPost(
    val referenceString: String,
    val submissionString: String,
    val commonString: String,
    val solutionName: String
)

private fun getOutput() {
    val referenceSource = Source(mapOf(
        "Reference" to """
import edu.illinois.cs.cs125.answerable.api.*;

public class Test {
    @Solution
    public static int sum(int a, int b) {
        return a + b;
    }
}
        """.trim()
    ))
    val submissionSource = Source(mapOf(
        "Submission" to """
public class Test {
    public static int sum(int first, int second) {
        return first - second;
    }
}
        """.trim()
    ))

    val refCL = try {
        referenceSource.compile(CompilationArguments(parentClassLoader = Solution::class.java.classLoader))
    } catch (e: CompilationFailed) {
        e.errors.forEach { println("${it.location}: ${it.message}\n")}
        throw e
    }.classLoader
    val subCL = try {
        submissionSource.compile()
    } catch (e: CompilationFailed) {
        e.errors.forEach { println("${it.location}: ${it.message}\n")}
        throw e
    }.classLoader

    println(
        TestGenerator(refCL.loadClass("Test"), bytecodeProvider = refCL.bytecodeProvider)
            .loadSubmission(subCL.loadClass("Test"), bytecodeProvider = subCL.bytecodeProvider)
            .runTests(Random.nextLong())
            .toJson()
    )
}

val JeedClassLoader.bytecodeProvider
    get() = bytecodeProvider { this.bytecodeForClass(it.name) }