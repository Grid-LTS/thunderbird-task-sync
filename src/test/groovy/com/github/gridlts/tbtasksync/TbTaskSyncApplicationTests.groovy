package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class TbTaskSyncApplicationTests {

	@MockBean
	GTaskRepo gTaskRepo

	@MockBean
	GoogleAuthorization googleAuthorization

	@Test
	void contextLoads() {
	}

}
