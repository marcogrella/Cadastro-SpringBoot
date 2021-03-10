package com.br.cadastro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages="com.br.cadastro.model")
@ComponentScan(basePackages={"com.br.*"})
@EnableJpaRepositories(basePackages= {"com.br.cadastro.repository"})
@EnableTransactionManagement
@EnableWebMvc // só para alterar a tela de login // implementou o WebMvcConfigurer
public class CadastroApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(CadastroApplication.class, args);
		
		/* Utilizado somente para gerar uma senha criptografada.
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String resultado = encoder.encode("admin");
		System.out.println(resultado);
		*/
		
	}
	
	// tela de login, quando for interceptada deverá ser encaminhada para a tela de login criada. 
	// a intenção é alterar a tela de login original pela tela customizada. 
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("/login");
		registry.setOrder(Ordered.LOWEST_PRECEDENCE);
	}
			

}
