package com.br.cadastro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.br.cadastro.model.Pessoa;


@Repository
@Transactional
public interface PessoaRepository extends CrudRepository <Pessoa, Long> {
	
	// criado para pesquisar de forma customizada (na página de cadastro enviamos o parâmetro)
	// note que o parâmetro pode ria ser 1 ou mais ficando com and mais outro parâmetro
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1,'%'))")
	List<Pessoa> findPessoaByName(String nome);
	
	@Query("select p from Pessoa p where lower(p.nome) like lower(concat('%', ?1,'%')) and p.sexo = ?2" )
	List<Pessoa> findPessoaByNameAndSex(String nome, String sexo);
	
	@Query("select p from Pessoa p where p.sexo = ?1" )
	List<Pessoa> findPessoaBySex(String sexo);
	

}
