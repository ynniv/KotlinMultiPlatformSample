package nmapgui

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.*
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.content.*
import io.ktor.jackson.jackson
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object HostEntry : Table() {
    @ContextualSerialization
    val id = varchar("id", length=60).nullable().primaryKey()
    @ContextualSerialization
    val starttime = varchar("starttime", 10).nullable() // Column<String>
    @ContextualSerialization
    val address = varchar("address", length = 40).nullable() // Column<String>
    @ContextualSerialization
    val data = text("data").nullable()
}

fun uploadXml(xml: String) : SearchResponse {
    val doc = parseNmapXml(xml)
    var count = 0

    if (doc?.host != null) {
        transaction {
            doc.host.forEach { hostTag ->
                val addr = hostTag.address?.first()?.addr ?: ""
                val start = hostTag.starttime!!
                val result = HostEntry.insertIgnore {
                    it[id] = "$addr@$start"
                    it[starttime] = start
                    it[address] = addr
                    it[data] = Json.stringify(HostTag.serializer(), hostTag)
                }
                result.resultedValues?.let { count += it.size }
            }
        }

        if (count > 0) {
            return SearchResponse("imported $count host entries", count, doc.host)
        }
    }
    return SearchResponse("imported no host entries", count, listOf())
}

data class SearchParameters(val address: String? = null)
suspend fun doSearch(parameters: SearchParameters) : SearchResponse? = dbQuery {
    if (parameters.address != null) {
        val results = HostEntry.select { HostEntry.address.like(parameters.address) }
            .mapNotNull { it[HostEntry.data]?.let { Json.parse(HostTag.serializer(), it) } }
            .toList()
        val count = results.count()
        SearchResponse("found $count host entries", count, results)
    } else null
}

fun main() {

    setupDb()
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation) {
            jackson {
            }
        }

        val currentDir = File(".").absoluteFile
        environment.log.info("Current directory: $currentDir")

        val webDir = listOf(
            "web",
            "../src/jsMain/web",
            "src/jsMain/web"
        ).map {
            File(currentDir, it)
        }.firstOrNull { it.isDirectory }?.absoluteFile ?: error("Can't find 'web' folder for this sample")

        environment.log.info("Web directory: $webDir")

        routing {
            file("/", webDir.path + "/index.html")

            static("/static") {
                files(webDir)
            }

            @UseExperimental(ImplicitReflectionSerializer::class)
            post("/search") {
                call.respondText { doSearch(call.receive())?.let { Json.stringify(it) } ?: "" }
            }

            // https://ktor.io/servers/uploads.html
            @UseExperimental(ImplicitReflectionSerializer::class)
            post("/upload") {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            part.streamProvider().use { its ->
                                val xml = String(its.readBytes(), Charset.forName("UTF-8"))
                                call.respondText {uploadXml(xml)?.let { Json.stringify(it) } ?: ""}
                            }
                        }
                    }
                    part.dispose()
                }
            }

        }
    }.start(wait = true)
}

///// Utilities and boilerplate /////

suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }

fun setupDb() {
    Database.connect("jdbc:sqlite:nmapgui.db", driver = "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

    transaction {
        SchemaUtils.create (HostEntry)
        println("loaded ${HostEntry.selectAll().count()} HostEntries")
    }
}

fun parseNmapXml(xml: String, strict: Boolean = false) : NmapRunTag? {
    val module = JacksonXmlModule()
    module.setDefaultUseWrapper(false);
    val xmlMapper = XmlMapper(module);
    xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, strict)
    return xmlMapper.readValue<NmapRunTag>(xml)
}

// https://ktor.io/servers/uploads.html
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = ioCoroutineDispatcher
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}

