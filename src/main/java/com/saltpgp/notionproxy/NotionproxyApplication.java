package com.saltpgp.notionproxy;

import com.saltpgp.notionproxy.service.NotionProperty.NotionPropertyFilter;
import com.saltpgp.notionproxy.service.NotionProperty.SelectFilter;
import com.saltpgp.notionproxy.service.NotionServiceFilters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableCaching
//@EnableScheduling
public class NotionproxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotionproxyApplication.class, args);
	}

}