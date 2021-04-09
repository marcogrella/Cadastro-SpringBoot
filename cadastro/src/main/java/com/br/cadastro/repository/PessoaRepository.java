package com.br.cadastro.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.br.cadastro.model.Pessoa;


@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long>  {
	
	// criado para pesquisar de forma customizada (na página de cadastro enviamos o parâmetro)
	// note que o parâmetro pode ria ser 1 ou mais ficando com and mais outro parâmetro
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1,'%'))")
	List<Pessoa> findPessoaByName(String nome);
	
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1,'%')) and p.sexo = ?2" )
	List<Pessoa> findPessoaByNameAndSex(String nome, String sexo);
	
	@Query("select p from Pessoa p where p.sexo = ?1" )
	List<Pessoa> findPessoaBySex(String sexo);
	
	// esse método foi feito para feita para paginação e utilizado na pesquisa por nome
	default Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable){
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		
		// Estamos configurando a pesquisa par aconsultar por partes do nome no banco de dados. Parecido com o like e identico ao método que busca por nome
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher); // faz a junção do objeto com o valor para consulta no banco de dados. 
		
		Page<Pessoa> pessoas = findAll(example, pageable);
		
		
		
		return pessoas;
	}
	
	
	// esse método foi feito para feita para paginação e utilizado na pesquisa por nome e sexo; 
	// O retorno é parecido com o que é feito na pesquisa normal. 
		default Page<Pessoa> findPessoaByNameAndSexPage(String nome, String sexo, Pageable pageable){
			
			Pessoa pessoa = new Pessoa();
			pessoa.setNome(nome);
			pessoa.setSexo(sexo);
			
			System.out.println("Nome para pesquisa: " + nome);
			System.out.println("Sexo para pesquisa: " + sexo);

			
			
			// Estamos configurando a pesquisa par aconsultar por partes do nome no banco de dados. Parecido com o like e identico ao método que busca por nome
			ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
					.withMatcher("sexo", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			
			Example<Pessoa> example = Example.of(pessoa, exampleMatcher); // faz a junção do objeto com o valor para consulta no banco de dados. 
			
			Page<Pessoa> pessoas = findAll(example, pageable);
			
			System.out.println("Lista Page: ");
			
			for (Pessoa pessoa2 : pessoas) {
				System.out.println(pessoa2.getNome());
			}
			
			return pessoas;
		}
	
	
	
	
	
	
	
	

}
