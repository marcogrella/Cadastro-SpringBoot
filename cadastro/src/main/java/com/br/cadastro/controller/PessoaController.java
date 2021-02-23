package com.br.cadastro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.br.cadastro.model.Pessoa;
import com.br.cadastro.repository.PessoaRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	
	
	@RequestMapping(method=RequestMethod.GET, value="/cadastropessoa")
	public String inicio() {
		return "cadastro/cadastropessoa";
		
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/salvarpessoa")
	public String salvar(Pessoa pessoa) {
		
		pessoaRepository.save(pessoa);
		
		return "cadastro/cadastropessoa";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		return modelAndView; 
	}
	
	
	
	
}
