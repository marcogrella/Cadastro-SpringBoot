package com.br.cadastro.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.br.cadastro.model.Pessoa;


@Repository
@Transactional
public interface PessoaRepository extends CrudRepository <Pessoa, Long> {

}
