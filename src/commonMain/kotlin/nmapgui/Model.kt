package nmapgui

import kotlinx.serialization.Serializable

fun ifString(x: Any?) = if (x is String) x else null

@Serializable
data class SearchResponse(val message: String, val count: Int, val data: List<HostTag>)

@Serializable
data class NmapRunTag(
    val scanner          : String? = null,
    val args             : String? = null,
    val start            : String? = null,
    val startstr         : String? = null,
    val version          : String? = null,
    val profile_name     : String? = null,
    val xmloutputversion : String? = null,

    val scaninfo         : ScanInfoTag? = null,
    val verbose          : VerboseTag? = null,
    val debugging        : DebuggingTag? = null,
    //val target           : List<TargetTag>? = null,
    val taskbegin        : List<TaskBeginTag>? = null,
    //val taskprogress     : List<TaskProgressTag>? = null,
    val taskend          : List<TaskEndTag>? = null,
    //val prescript        : List<PrescriptTag>? = null,
    //val postscript       : List<PostscriptTag>? = null,
    val host             : List<HostTag>? = null,
    //val output           : List<OutputTag>? = null,
    val runstats         : RunStatsTag? = null
)
//data class TargetTag(
//    val status: String? = null,
//    val reason: String? = null
//)
@Serializable
data class TaskBeginTag(
    val task: String? = null,
    val time : String? = null,
    val extrainfo : String? = null
)
//data class TaskProgressTag(
//    val task: String? = null,
//    val time : String? = null,
//    val percent : String? = null,
//    val remaining : String? = null,
//    val etc: String? = null
//)
@Serializable
data class TaskEndTag(
    val task: String? = null,
    val time : String? = null,
    val extrainfo : String? = null
)
//data class PrescriptTag(
//    val script: List<ScriptTag>? = null
//)
//data class PostscriptTag(
//    val script: List<ScriptTag>? = null
//)
//data class HostScriptTag(
//    val script: List<ScriptTag>? = null
//)
@Serializable
data class HostTag(
    val starttime:     String? = null,
    val endtime:       String? = null,
    //val comment:       String? = null,
    val status:        HostStatusTag? = null,
    val address:       List<HostAddressTag>? = null,
    val hostnames:     List<HostNamesTag>? = null,
    //val smurf:         List<SmurfTag>? = null,
    val ports:         List<PortsTag>? = null,
    //val os:            List<OsTag>? = null,
    //val distance:      List<DistanceTag>? = null,
    //val uptime:        List<UptimeTag>? = null,
    //val tcpsequence:   List<TcpSequenceTag>? = null,
    //val ipidsequence:  List<IpIdSequenceTag>? = null,
    //val tcptssequence: List<TcpTsSequenceTag>? = null,
    //val hostscript:    List<HostScriptTag>? = null,
    //val trace:         List<TraceTag>? = null,
    val times:         List<TimesTag>? = null
)
@Serializable
data class HostAddressTag(
    val addr: String? = null,
    val addrtype: String? = null
    //val vendor: String? = null
)
@Serializable
data class HostNamesTag(
    val hostname: List<HostNameTag>? = null
)
@Serializable
data class HostNameTag(
    val name: String? = null,
    val type: String? = null
)
//data class SmurfTag(
//    val responses: String? = null
//)
@Serializable
data class PortsTag(
    //val extraports: List<ExtraPortsTag>? = null,
    val port: List<PortTag>? = null
)
//data class ExtraPortsTag(
//    val extrareasons: List<ExtraReasonsTag>? = null,
//    val state: String? = null,
//    val count: String? = null
//)
//data class ExtraReasonsTag(
//    val reason: String? = null,
//    val count: String? = null
//)
@Serializable
data class PortTag(
    val state: StateTag? = null,
    //val owner: OwnerTag? = null,
    val service: ServiceTag? = null,
    //val script: List<ScriptTag>? = null,
    val protocol: String? = null,
    val portid: String? = null
)
@Serializable
data class StateTag(
    val state: String? = null,
    val reason: String? = null,
    val reason_ttl: String? = null,
    val reason_ip: String? = null
)
//data class OwnerTag(
//    val name: String? = null
//)
@Serializable
data class ServiceTag(
    //val cpe: String? = null,
    val name: String? = null,
    val conf: String? = null,
    val method: String? = null
    //val version: String? = null,
    //val product: String? = null,
    //val extrainfo: String? = null,
    //val tunnel: String? = null,
    //val proto: String? = null,
    //val rpcnum: String? = null,
    //val lowver: String? = null,
    //val highver: String? = null,
    //val hostname: String? = null,
    //val ostype: String? = null,
    //val devicetype: String? = null,
    //val servicefp: String? = null
)
//data class OsTag(
//    val portused: List<PortUsedTag>? = null,
//    val osmatch: List<OsMatchTag>? = null,
//    val osfingerprint: List<OsFingerprintTag>? = null
//)
//data class PortUsedTag(
//    val state: String? = null,
//    val proto: String? = null,
//    val portid: String? = null
//)
//data class OsMatchTag(
//    val osclass: List<OsClassTag>? = null,
//    val name: String? = null,
//    val accuracy: String? = null,
//    val line: String? = null
//)
//data class OsFingerprintTag(
//    val fingerprint: String? = null
//)
//data class OsClassTag(
//    val cpe: String? = null,
//    val vendor: String? = null,
//    val osgen: String? = null,
//    val type: String? = null,
//    val accuracy: String? = null,
//    val osfamily: String? = null
//)
//data class DistanceTag(
//    val value: String? = null
//)
//data class UptimeTag(
//    val second: String? = null,
//    val lastboot: String? = null
//)
//data class TcpSequenceTag(
//    val index: String? = null,
//    val difficulty: String? = null,
//    val values: String? = null
//)
//data class IpIdSequenceTag(
//    val Class: String? = null,
//    val values: String? = null
//)
//data class TcpTsSequenceTag(
//    val Class: String? = null,
//    val values: String? = null
//)
//data class TraceTag(
//    val trace: List<HopTag>? = null,
//    val proto: String? = null,
//    val port: String? = null
//)
//data class HopTag(
//    val ttl: String? = null,
//    val rtt: String? = null,
//    val ipaddr: String? = null,
//    val host: String? = null
//)
@Serializable
data class TimesTag(
    val srtt: String? = null,
    val rttvar: String? = null,
    val to: String? = null
)
//data class OutputTag(
//    val type: String? = null
//)
@Serializable
data class RunStatsTag(
    val finished: FinishedTag? = null,
    val hosts : RunStatsHostsTag? = null
)
@Serializable
data class FinishedTag(
    val time : String? = null,
    val timestr: String? = null,
    val elapsed: String? = null,
    val summary: String? = null,
    val exit: String? = null,
    val errormsg: String? = null
)
@Serializable
data class RunStatsHostsTag(
    val up: String? = null,
    val down: String? = null,
    val total: String? = null
)
//data class ScriptTag(
//    val id : String? = null,
//    val output : String? = null
//)
@Serializable
data class HostStatusTag(
    val state: String? = null,
    val reason: String? = null,
    val reason_ttl: String? = null
)
@Serializable
data class ScanInfoTag(
    val type : String? = null,
    val scanflags : String? = null,
    val protocol : String? = null,
    val numservices : Int? = null,
    val services : String? = null
)
@Serializable
data class VerboseTag(
    val level : Int? = null
)
@Serializable
data class DebuggingTag(
    val level : Int? = null
)
