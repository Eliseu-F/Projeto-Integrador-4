package br.senac.tads.pi.lojatenis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = "br.senac.tads.pi.lojatenis")
public class LojaTenisApplication {

	public static void main(String[] args) {
			SpringApplication.run(LojaTenisApplication.class, args);
	}

}
