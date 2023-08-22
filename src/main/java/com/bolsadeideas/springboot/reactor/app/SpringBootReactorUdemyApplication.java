package com.bolsadeideas.springboot.reactor.app;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootReactorUdemyApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorUdemyApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorUdemyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ejemploDelayElements();
		//ejemploInterval();
		//ejemploZipWithRangos();
		//ejemploUsuarioComentiosZipWithForma2();
		//ejemploUsuarioComentiosZipWith();
		//ejemploUsuarioComentiosFlatMap();
		//ejemploCollectList();
		//ejemploToString();
		//ejemploFlatMap();
		//ejemploIterable();
	}

	public void ejemploDelayElements(){
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));

		rango.blockLast();

	}
	public void ejemploInterval(){
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso, (ra, re) -> ra)
				.doOnNext(i -> log.info(i.toString()))
				.blockLast();
	}

	public void ejemploZipWithRangos(){
		Flux<Integer> rangos = Flux.range(0,4);

		Flux.just(1,2,3,4)
				.map(i -> (i*2))
				.zipWith(rangos, (uno, dos) -> String.format("primer Flux: %d, segundo Flux: %d", uno, dos))
				.subscribe(texto -> log.info(texto));

	}
	public void ejemploUsuarioComentiosZipWithForma2(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, qué tal!");
			comentarios.addComentarios("Mañana voy a la playa!");
			comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});
		Mono<UsuarioComentarios> usuarioComentariosMono = usuarioMono
				.zipWith(comentariosMono)
						.map(tuple -> {
							Usuario u =tuple.getT1();
							Comentarios c = tuple.getT2();
							return new UsuarioComentarios(u, c);
						});
		usuarioComentariosMono.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}
	public void ejemploUsuarioComentiosZipWith(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, qué tal!");
			comentarios.addComentarios("Mañana voy a la playa!");
			comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});
		Mono<UsuarioComentarios> usuarioComentariosMono = usuarioMono.zipWith(comentariosMono, (usuario, comentario) -> new UsuarioComentarios(usuario, comentario));
		usuarioComentariosMono.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}
	public void ejemploUsuarioComentiosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, qué tal!");
			comentarios.addComentarios("Mañana voy a la playa!");
			comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});

		usuarioMono.flatMap(u -> comentariosMono.map(c -> new UsuarioComentarios(u, c)))
				.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}
	public void ejemploCollectList() throws Exception {
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("Maria", "Fulana"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Juan M", "egano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosList)
				.collectList()
				.subscribe(lista -> {
					lista.forEach(item -> log.info(item.toString()));
				});
	}

	public void ejemploToString() throws Exception {
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres","Guzman"));
		usuariosList.add(new Usuario("Pedro","Fulano"));
		usuariosList.add(new Usuario("Maria","Fulana"));
		usuariosList.add(new Usuario("Diego","Sultano"));
		usuariosList.add(new Usuario("Juan M","egano"));
		usuariosList.add(new Usuario("Bruce","Lee"));
		usuariosList.add(new Usuario("Bruce","Willis"));

		Flux.fromIterable(usuariosList)
				.map(usuario -> (usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase())))
				.flatMap(nombre -> {
					if(nombre.contains("bruce".toUpperCase()))
						return Mono.just(nombre);
					else {
						return Mono.empty();
					}
				})
				.map(nombre -> {
					return nombre.toLowerCase();
				})
				.subscribe(usuario -> log.info(usuario.toString()));


	}
	public void ejemploFlatMap() throws Exception {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("Maria Fulana");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Juan Megano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce"))
						return Mono.just(usuario);
					else {
						return Mono.empty();
					}
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				})
				.subscribe(usuario -> log.info(usuario.toString()));


	}

	public void ejemploIterable() throws Exception {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("Maria Fulana");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Juan Megano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux<String> nombres = Flux.fromIterable(usuariosList);/*Flux.just("Andres Guzman", "Pedro Fulano", "Maria Fulana", "Diego Sultano","Juan Megano", "Bruce Lee", "Bruce Willis")*/

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
				.doOnNext(usuario -> {
					if (usuario == null){
						throw new RuntimeException("El nombre no puede estar vacío");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));

				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(usuario -> log.info(usuario.toString()),
				error -> log.error((error.getMessage())),
				new Runnable() { // Runnable es una interfaz para definir tareas que se ejecutan en hilos (thread)
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable con éxito!");

					}
				});


	}
}


