package us.redsols.todo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import us.redsols.todo.model.Todo;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TodoApplication {


	public static void noSleep(){
		while(true){
			try {
				Thread.sleep(60000 * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

		SpringApplication.run(TodoApplication.class, args);
		noSleep();
	}

}
