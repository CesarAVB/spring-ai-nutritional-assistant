package br.com.sistema.nutritional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
		System.out.println("🚀 Nutritional Plan Assistant rodando na porta 8083");
        System.out.println("📚 Swagger: http://localhost:8083/swagger-ui.html");
	}

}
