package edu.illinois.cs.cs125.answerable.jeedrunner

import edu.illinois.cs.cs125.answerable.api.BytecodeClassProvider
import edu.illinois.cs.cs125.answerable.api.BytecodeProvider
import edu.illinois.cs.cs125.answerable.api.EnumerableBytecodeLoader
import edu.illinois.cs.cs125.answerable.api.OutputCapturer
import edu.illinois.cs.cs125.jeed.core.JeedClassLoader
import edu.illinois.cs.cs125.jeed.core.Sandbox
import edu.illinois.cs.cs125.jeed.core.sandbox

import kotlinx.coroutines.*
import kotlin.math.min

val jeedOutputCapturer = object : OutputCapturer {

    private var stdOut: String? = null
    private var stdErr: String? = null

    override fun runCapturingOutput(code: Runnable) {
        val output = Sandbox.redirectOutput { code.run() }
        stdOut = output.first
        stdErr = output.second
    }

    override fun getStandardOut(): String? = stdOut
    override fun getStandardErr(): String? = stdErr

}

fun jeedSandbox(loaderConfig: Sandbox.ClassLoaderConfiguration = Sandbox.ClassLoaderConfiguration(),
                executeConfig: Sandbox.ExecutionArguments = Sandbox.ExecutionArguments(),
                maxTimeout: Long = 10000L): edu.illinois.cs.cs125.answerable.api.Sandbox {
    return object : edu.illinois.cs.cs125.answerable.api.Sandbox {
        private lateinit var sandboxedLoader: Sandbox.SandboxedClassLoader
        override fun transformLoader(loader: EnumerableBytecodeLoader): BytecodeClassProvider {
            val sandboxableLoader = object : Sandbox.SandboxableClassLoader {
                override val bytecodeForClasses: Map<String, ByteArray> = loader.getAllBytecode()
                override val classLoader: ClassLoader = loader.getLoader()
            }
            sandboxedLoader = sandboxableLoader.sandbox(loaderConfig)
            return object : BytecodeClassProvider {
                override fun getLoader() = sandboxedLoader
                override fun getBytecode(clazz: Class<*>): ByteArray {
                    return sandboxedLoader.knownClasses[clazz.name]
                            ?: throw ClassNotFoundException("Jeed did not provide $clazz")
                }
            }
        }
        override fun run(timeout: Long?, callback: Runnable): Boolean {
            val timeoutConfig = Sandbox.ExecutionArguments(
                timeout = min(timeout ?: Long.MAX_VALUE, maxTimeout),
                permissions = executeConfig.permissions,
                maxExtraThreads = executeConfig.maxExtraThreads,
                waitForShutdown = executeConfig.waitForShutdown,
                classLoaderConfiguration = loaderConfig,
                maxOutputLines = Int.MAX_VALUE)
            val job: (Pair<ClassLoader, (() -> Unit) -> Pair<String, String>>) -> Any? = { callback.run() }
            val result = runBlocking {
                Sandbox.execute(sandboxedLoader, timeoutConfig, job)
            }
            return !result.timeout
        }
    }
}

fun answerableBytecodeProvider(loader: JeedClassLoader): BytecodeProvider {
    return object : BytecodeProvider {
        override fun getBytecode(clazz: Class<*>): ByteArray {
            return loader.bytecodeForClass(clazz.name)
        }
    }
}
