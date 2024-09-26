package com.saltpgp.notionproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotionproxyControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void endpointShouldReturn() {
//		Arrange
        String expected = "404";

//		Act
        String thing = this.restTemplate.getForObject("http://localhost:" + port + "/notion", String.class);

//		Assert
        assertThat(thing).contains(expected);
    }

}
