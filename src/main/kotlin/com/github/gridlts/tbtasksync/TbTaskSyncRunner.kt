package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class TbTaskSyncRunner @Autowired constructor(
    private val syncService: SyncService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        syncService.sync()
    }
}
