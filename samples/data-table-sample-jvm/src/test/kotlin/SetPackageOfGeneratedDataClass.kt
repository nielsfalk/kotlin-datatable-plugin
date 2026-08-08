import OriginalName
import de.nielsfalk.dataTables.Data
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import setpackage.via.import.SetPackageViaImport
import OriginalName as SetPackageViaImportAs

class SetPackageOfGeneratedDataClass : FreeSpec({
    "set package via fully qualified name" {
        val list = @Data("foo" , "bar") setpackage.via.fully.QulifiedName{
                         1     ǀ 2
        }

        list.first().shouldBeInstanceOf<setpackage.via.fully.QulifiedName<Int, Int>>()
    }

    "re use data class via import" {
        val list = @Data("name" , "expectedLength") Spock{
                         1      ǀ 2
        }

        list.first().shouldBeInstanceOf<Spock<Int, Int>>()
    }

    "set package via import" {
        val list = @Data("foo" , "bar") SetPackageViaImport{
                         1     ǀ 2
        }

        list.first().shouldBeInstanceOf<SetPackageViaImport<Int, Int>>()
    }

    "set package via import as" {
        val list = @Data("foo" , "bar") SetPackageViaImportAs{
                         1     ǀ 2
        }

        list.first().shouldBeInstanceOf<OriginalName<Int, Int>>()
    }
})
