package nmapgui

import kotlin.test.Test
import kotlin.test.assertNotNull

class QuickTestsJVM {

    @Test
    fun basicParse() {
        assertNotNull(parseNmapXml(this.javaClass.getResource("/sample.xml").readText(), strict = true))
    }
}