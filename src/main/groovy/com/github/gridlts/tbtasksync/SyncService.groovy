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

    GoogleAuthorization googleAuth

    SyncService(GTaskRepo gTaskRepo, GoogleAuthorization googleAuth) {
        this.gTaskRepo = gTaskRepo
        this.googleAuth = googleAuth
    }

    @Transactional
    def void sync(){
        def tasks = TaskEntity.all
        tasks.forEach{
            println(it)
        }

        Credential creds = googleAuth.main()
        def lists = gTaskRepo.getTaskLists(creds.accessToken)
        println(lists)
    }

}
