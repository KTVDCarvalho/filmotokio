/*
 * © 2026 Kiniame Tarquinio Vieira Dias de Carvalho
 * Projeto: FILMOTOKIO
 */

package filmotokio;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class FilmotokioApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmotokioApplication.class, args);
	}
}


