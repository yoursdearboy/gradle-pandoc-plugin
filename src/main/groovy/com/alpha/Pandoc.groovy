package com.alpha

import com.google.common.base.CaseFormat

class Pandoc {
    File workingDirectory
    String binaryPath = 'pandoc'
    def settings = [:]

    def propertyMissing(String prop, value) {
        set(prop, value)
    }

    def propertyMissing(String prop) {
        get(prop)
    }

    def set(String prop, value) {
        settings[prop] = value
        return this
    }

    def get(String prop) {
        settings[prop]
    }

    private String execString(String input) {
        String execString;
        StringBuilder sb = new StringBuilder()
        sb.append(getBinaryPath())
        settings.each { prop, val ->
            prop = (String) prop;
            String key = prop.startsWith('-') ? prop : propToKey(prop)
            println(val.getClass())
            if (val instanceof Collection) {
                val.each {v -> sb.append(String.format(" ${key} %s", v))}
            } else {
                sb.append(String.format(" ${key} %s", val))
            }
        }
        sb.append(" ${input}")
        execString = sb.toString()
        return execString
    }

    private String execDo(String input) {
        String execString = execString(input)

        println("Executing: " + execString);

        def stdout = new StringBuilder()
        def stderr = new StringBuilder()
        def proc = execString.execute(null, getWorkingDirectory())
        proc.consumeProcessOutput(stdout, stderr)
        proc.waitFor()

        stdout = stdout.toString()
        stderr = stderr.toString()
        if (!stderr.isEmpty()) {
            throw new RuntimeException(stderr)
        }
        return stdout
    }

    public String exec(String input) {
        return execDo(input)
    }

    public void exec(String input, String output) {
        File inputFile = new File(input)
        if(inputFile.isDirectory()) {
            String to = get('to')
            File outputFile = new File(output)
            DirectoryConverter directoryConverter = new DirectoryConverter(inputFile)
            directoryConverter.loop { File nInputFile ->
                String relativePath = inputFile.toURI().relativize(nInputFile.toURI()).getPath();
                String nOutput = PandocExtensionConverter.convert(relativePath, to)
                File nOutputFile = new File(outputFile, nOutput)
                nOutputFile.getParentFile().mkdirs()
                nOutputFile.write(execDo(nInputFile.toString()))
            }
        } else {
            (new File(output)).write(execDo(input))
        }
    }

    private static String propToKey(String prop) {
        prop = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, prop)
        prop = "--${prop}"
        return prop
    }
}
