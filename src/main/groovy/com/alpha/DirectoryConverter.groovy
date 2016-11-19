package com.alpha

class DirectoryConverter {
    File input

    DirectoryConverter(input) {
        this.input = input
    }

    public void loop(Closure closure) {
        input.listFiles().each{ file ->
            if (file.isDirectory()) {
                DirectoryConverter directoryConverter = new DirectoryConverter(file)
                directoryConverter.loop(closure)
            } else {
                closure(file)
            }
        }
    }
}
