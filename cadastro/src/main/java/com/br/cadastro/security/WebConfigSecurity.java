package com.br.cadastro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*
 * Essa parte de configuração desativa as configurações padrão e colocou as configurações
 * em memória. Depois foi colocado para verificar usuário e senha no banco de dados.
 * 
 */


@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailService implementacaoUserDetailService;
	
	
	@Override   // configura as solicitações por Http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable()  // desativa as configurações padrão de memória  
		.authorizeRequests() // Permitir restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() // Qualquer usuário acessa o sistema
		.anyRequest().authenticated()
		.and().formLogin().permitAll() // permite qualquer usuário
		.and().logout()  // mapeia URL de Logou e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
		
		
	}
	
	@Override  // cria a autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	
		/*
		// sem criptografia na senha:
		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		.withUser("admin")
		.password("admin")
		.roles("ADMIN");
		*/
		
		// utilizando criptorgrafia, mas com uso do banco de dados:
		
		auth.userDetailsService(implementacaoUserDetailService) // chama o método indUserByLogin automaticamente
		.passwordEncoder(new BCryptPasswordEncoder());
		
		
		
		// utilizando senha criptografada mas com o uso em memória
		/*
		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("admin")
		.password("$2a$10$1tE/eMTNsjKiRsrlghUaYeeVZW5KoQzTxfnl6ufK0DSpGDIOiK4lO")
		.roles("ADMIN");
		*/
		
	}
	
	@Override  // ignora URL específicas (serve para ignorar as pastas do materialize que vamos utilizar para uma outra página de login)
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
		
	}
	
	
}
