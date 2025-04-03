package de.nielsfalk.kotlin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty


abstract class DataTableScannerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension =
            project.extensions.create("dataTableScanner", DataTableScannerExtension::class.java)

        val taskProvider =
            project.tasks.register("scanDataTables", ScanDataTablesTask::class.java) {
                sourceDirs.set(extension.sourceDirs)
                outputDir.set(project.layout.buildDirectory.dir("generated/dataTable"))
            }

        project.afterEvaluate {
            project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
                //commonMain main
                //commonTest test


                sourceSets.getByName("main").java.srcDir(taskProvider.get().outputDir)
            }
        }
    }
}

abstract class DataTableScannerExtension {
   //todo aProperty testSourcesOnly default false

    //todo a property add to source set default true

    //todo replace sourceDirs with searching for kotlin in sourcesets commonMain main or if testSourcesOnly in commonTest test
    abstract val sourceDirs: ListProperty<org.gradle.api.file.Directory>
}