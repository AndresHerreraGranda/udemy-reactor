package com.bolsadeideas.springboot.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorUdemyApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorUdemyApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorUdemyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Flux.just -> crea un objeto con los elementos
		Flux<String> nombres = Flux
				.just("Andres", "Pedro", "Maria", "Diego","Juan")
				.map(elemento -> {
					return elemento.toUpperCase();
				})
				.doOnNext(elemento -> {
					if (elemento.isEmpty()){
						throw new RuntimeException("El nombre no puede estar vacío");
					}
					System.out.println(elemento);
				})
				.map(elemento -> {
					return elemento.toLowerCase();
				});

		nombres.subscribe(log::info,
				error -> log.error((error.getMessage())),
				new Runnable() { // Runnable es una interfaz para definir tareas que se ejecutan en hilos (thread)
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable con éxito!");

					}
				});


	}
}
