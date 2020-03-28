package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.domain.TaskEntity
import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.google.api.client.auth.oauth2.Credential
import grails.gorm.transactions.Transactional
import org.springframework.stereotype.Component

@Component
class SyncService {

    GTaskRepo gTaskRepo;

    SyncService(GTaskRepo gTaskRepo) {
        this.gTaskRepo = gTaskRepo
    }

    @Transactional
    def void sync(){
        def tasks = TaskEntity.all
        tasks.forEach{
            println(it)
        }
        def googleAuth = new GoogleAuthorization();
        Credential creds = googleAuth.main()
        def lists = gTaskRepo.getTaskLists(creds.accessToken)
        println(lists)
    }

}
