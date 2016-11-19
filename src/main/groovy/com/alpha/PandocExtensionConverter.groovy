package com.alpha

class PandocExtensionConverter {
    private static Map<String, String> FORMAT_EXTENSION = [
        'markdown': 'md',
        'html': 'html'
    ]

    public static convert(String path, String to) {
        String toExt = FORMAT_EXTENSION[to]
        path = path.substring(0, path.lastIndexOf('.'))
        path = path + ".${toExt}"
        return path
    }
}
