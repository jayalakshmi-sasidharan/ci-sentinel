package com.cisentinel.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.cisentinel.backend.service.GitHubService;

@SpringBootTest
class BackendApplicationTests {

	@MockBean
	private GitHubService gitHubService; // This fakes the service so it doesn't call the real GitHub API
	@Test
	void contextLoads() {
	}

}
