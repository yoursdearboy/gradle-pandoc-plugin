package com.alpha

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Paths

class PandocPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('pandoc', PandocPluginExtension)
        project.task('pandoc') << {
            Pandoc pandoc = new Pandoc()
            pandoc.setWorkingDirectory(project.getProjectDir())
            project.pandoc.settings.each {prop, value ->
                pandoc.set(prop, value)
            }
            File templatesDir = new File(project.getProjectDir(), 'templates')
            if (templatesDir.isDirectory() && pandoc.template != null) {
                pandoc.template = new File(templatesDir, pandoc.template).toString()
            }

            String input = project.pandoc.input
            String output = project.pandoc.output
            if (output == null) {
                File inputFile = new File(input)
                output = inputFile.isDirectory() ? inputFile.toString() : inputFile.getParent().toString()
                output = new File(project.getBuildDir(), output).toString()
            }

//            println(pandoc.exec(input))
            pandoc.exec(input, output)
        }
    }
}
