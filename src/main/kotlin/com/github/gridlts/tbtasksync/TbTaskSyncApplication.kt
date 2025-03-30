package com.github.gridlts.tbtasksync

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TbTaskSyncApplication

fun main(args: Array<String>) {
    runApplication<TbTaskSyncApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
