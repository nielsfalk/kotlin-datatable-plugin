package sample

import de.nielsfalk.dataTables.DataTable


fun main() {
    @DataTable("aString","anInt","aBool","aDouble")
    Spock.data<String, Int, Boolean, Double> {

    }.map {
        it.run { "$aString, $anInt, $aBool, $aDouble" }
    }
}