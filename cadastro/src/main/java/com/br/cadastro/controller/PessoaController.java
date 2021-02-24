package com.br.cadastro.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public ModelAndView inicio() {
		
		// quando iniciar a tela tem que passar um objeto vazio se não vai dar erro. 
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		return modelAndView;
		
	}
	
	// O ** antes da requisição serve para ignorar na url. Por exemplo, quando for editar vai 
	// bagunçar a URL e o ** faz com que seja interceptado o get salvarpessoa.
	
	@RequestMapping(method=RequestMethod.POST, value="**/salvarpessoa")   
	public ModelAndView salvar(Pessoa pessoa) {
		
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
		return modelAndView;
		

	}
	
	
	// OBS: o GetMapping e o RequestMapping são notações com mesma finalidade, só muda o fato de GetMapping ser mais novo.
	
	// utilizado para listar o cadastro de usuários
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
		return modelAndView; 
	}
	
	
	/*
	 * Recebe a url, intercepta o valor do id ao clicar no botão editar.
	 * Faz a busca no banco de dados com o valor passado e atribui os dados no pessoaobj
	 * Lá no cadastro a gente precisa dizer que queremos trabalhar com object e carregar esse objeto
	 * fica então th:object="${pessoaobj}"
	 */
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", pessoa.get());
		
		return modelAndView;
		
	}
	
	
	
	
	
}
