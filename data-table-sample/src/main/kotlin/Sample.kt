package sample

import de.nielsfalk.dataTables.DataTable


fun main() {
    @DataTable("aString", "anInt", "aBool", "aDouble")
    Spock.data<String   ,   Int  , Boolean, Double   > {
               "df"     ǀ   1    ǀ true   ǀ 2.0
    }
}