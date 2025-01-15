package com.madinaappstudio.viruscheck.models

data class StatsModel(
    var totalScan: Int = 0,
    var clean: Int = 0,
    var malicious: Int = 0,
    var suspicious: Int = 0,
    var file: Int = 0,
    var url: Int = 0,
    var apk: Int = 0,
    var exe: Int = 0,
    var document: Int = 0,
    var archive: Int = 0,
    var other: Int = 0
)
