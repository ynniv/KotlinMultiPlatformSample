package nmapgui

import kotlin.browser.*
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import kotlin.js.Date
import kotlin.js.json

@Suppress("unused")
@JsName("testParsing")
fun testParsing(hostTag: String) {
    val parsedTag = Json.parse(SearchResponse.serializer(), hostTag)
    document.getElementById("js-response")?.textContent = parsedTag.data.first().address?.first()?.addr
}

@Suppress("unused")
@JsName("onSearchSubmit")
fun onSearchSubmit(e: Event) {
    e.preventDefault()
    val form = e.currentTarget as HTMLFormElement
    val addressElement = form["address"] as HTMLInputElement
    val pattern = addressElement.value
    val jsonData = json("address" to pattern)

    window.fetch("/search", object : RequestInit {
        override var credentials: dynamic = "same-origin"
        override var body: dynamic = JSON.stringify(jsonData)
        override var headers: dynamic = json().apply {
            this["Content-Type"] = "application/json"
        }
        override var method: dynamic = "POST"
    }).then { it.text() }.then { text ->
        val response = Json.parse(SearchResponse.serializer(), text)
        displayMessage(response.message)
        displayHostEntries(response.data)
    }
}

@Suppress("unused")
@JsName("onUploadSubmit")
fun onUploadSubmit(e: Event)  {
    e.preventDefault()
    val form = e.currentTarget as HTMLFormElement
    val fileUpload = form["fileupload"] as HTMLInputElement
    val label = fileUpload.value.replace("\\/g".toRegex(), "/").replace(".*/".toRegex(), "");
    val file = fileUpload.files?.item(0)

    if (file != null) {
        val postData = FormData()
        postData.append("name", label)
        postData.append("data", file)
        window.fetch("/upload", object : RequestInit {
            override var credentials: dynamic = "same-origin"
            override var body: dynamic = postData
            override var method: dynamic = "POST"
        }).then { it.text() }.then { text ->
            val response = Json.parse(SearchResponse.serializer(), text)
            displayMessage(response.message)
            displayHostEntries(response.data)
        }
    }
}

fun displayHostEntries(hostEntries: List<HostTag>) {
    val newMarkup = "<div class=\"host-entry\">" + hostEntries.map(::formatHostEntry).joinToString("") + "</div>"
    document.getElementById("content")?.innerHTML = newMarkup
}

fun formatHostEntry(entry: HostTag) : String {
    val addr = entry.address?.first()?.addr
    val hostname = entry.hostnames?.first()?.hostname?.first()?.name
    val location = if (hostname != null) "$hostname ($addr)" else addr
    val lines = mutableListOf("<div>Nmap scan report for $location<br/>")
    val status = entry.status
    if (status != null) lines.add("Host is ${status.state}, received ${status.reason}</div>")
    val date = entry.starttime?.toLong()?.times(1000)?.let { Date(it) }
    if (date != null) {
        val time = date.toLocaleDateString() + " " + date.toLocaleTimeString()
        val duration = entry.endtime?.toLong()?.minus(entry.starttime.toLong())
        lines.add("<div>Scanned at $time for ${duration}s</div>")
    }
    lines.add("<table>")
    lines.add("<tr><td>PORT</td><td>STATE</td><td>SERVICE</td><td>REASON</td></tr>")
    entry.ports?.first()?.port?.map { port ->
        lines.add("<tr><td>${port.portid}/${port.protocol}</td>"
                + "<td>${port.state?.state}</td>"
                + "<td>${port.service?.name}</td>"
                + "<td>${port.state?.reason}</td></tr>")
    }
    lines.add("</table><br/>")

    return lines.joinToString("\n")
}

fun displayMessage(msg: String) {
    document.getElementById("messagebox")?.textContent = msg
}

fun renderPage() {

}