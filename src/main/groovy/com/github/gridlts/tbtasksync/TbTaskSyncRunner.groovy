package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
public class TbTaskSyncRunner implements CommandLineRunner {

    @Autowired
    SyncService syncService;


    @Override
    void run(String... args) {
        syncService.sync()
    }
}