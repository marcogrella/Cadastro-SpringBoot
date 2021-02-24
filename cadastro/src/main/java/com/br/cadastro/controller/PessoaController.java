package com.br.cadastro.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.br.cadastro.model.Pessoa;
import com.br.cadastro.model.Telefone;
import com.br.cadastro.repository.PessoaRepository;
import com.br.cadastro.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@RequestMapping(method=RequestMethod.GET, value="/cadastropessoa")
	public ModelAndView inicio() {
		
		// quando iniciar a tela tem que passar um objeto vazio se não vai dar erro. 
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
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
	
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		
		return modelAndView;
		
	}
	
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView remover(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		return modelAndView;
		
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
		
	}
	
	
	// Método para adicionar Telefone:
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get(); // consulta a pessoa com base no id
		telefone.setPessoa(pessoa); // seta o telefone com base nos dados da pessoa (
		telefoneRepository.save(telefone); // salva o telefone para o repositório
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); // retorna para a mesma tela.
		
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid)); // seleciona os telefones com base nos valores do id de usuário.
		modelAndView.addObject("pessoaobj", pessoa); // joga o objeto usuário na tela.
		
		return modelAndView;
		
	}
	
	
	
	
	
	
}
