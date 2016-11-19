package com.alpha

class PandocPluginExtension {
    String input
    String output
    def settings = [:]

    def propertyMissing(String prop, value) {
        settings[prop] = value
    }

    def propertyMissing(String prop) {
        settings[prop]
    }
}
