package com.br.cadastro.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
	// BindingResult é a mensagem que deve ser enviada (configurada na classe modelo e enviada para a tela).
	
	@RequestMapping(method=RequestMethod.POST, value="**/salvarpessoa")   
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		//System.out.println(pessoa.getId());
		 
		// Como estamos utilizando validações fazemos o uso do if:
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIterable);
			modelAndView.addObject("pessoaobj", pessoa); // devolve pra tela os dados que foram preenchidos (com msg de erros)
			
			// faz um for para encontrar os erros e apresentar na tela:
			List<String> msg = new ArrayList<String>(); // lista com os erros em vazio
			
			// ObjectError é uma lista do 
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // getDafaultMessage vem das notações feitas na classe Model.
			}
			
			modelAndView.addObject("msg", msg); // joga as mensagens devolta na tela referentes aos erros encontrados.
			return modelAndView; // returna para a tela e não executa a debaixo.
		}
		
		// caso não houver erros segue o fluxo:
		
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIterable);
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
		
		String msg = "Usuário cadastrado com sucesso!"; // mensagem de sucesso 
		modelAndView.addObject("msg", msg); // insere um novo objeto vazio
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
	
	
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa(); // atribui a um objeto pessoa os dados com base no idtelefone
		
		telefoneRepository.deleteById(idtelefone); // apaga o telefone com base no id informado
		 
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); // retorna para a mesma tela.
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId())); // seleciona os telefones com base nos valores do id de usuário.
		modelAndView.addObject("pessoaobj", pessoa); // joga o objeto usuário na tela.
		return modelAndView;
		
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo) {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		
		if(pesquisasexo!=null && !pesquisasexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameAndSex(nomepesquisa, pesquisasexo);
		} else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		
		
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
		
	}
	
	
	// Método para adicionar Telefone:
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get(); // consulta a pessoa com base no id
		
		// validação manual
		if((telefone != null && telefone.getNumero() != null   
				&& telefone.getNumero().isEmpty()) || telefone.getNumero() == null) {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); // retorna para a mesma tela.
			modelAndView.addObject("pessoaobj", pessoa); 
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			// cria uma lista em vazio que será adicionado na tela (variável msg)
			List<String> msg = new ArrayList<String>();
			msg.add("O número deve ser informado");
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}
		
		telefone.setPessoa(pessoa); // seta o telefone com base nos dados da pessoa (
		telefoneRepository.save(telefone); // salva o telefone para o repositório
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); // retorna para a mesma tela.
		
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid)); // seleciona os telefones com base nos valores do id de usuário.
		modelAndView.addObject("pessoaobj", pessoa); // joga o objeto usuário na tela.
		
		return modelAndView;
		
	}
	
	
	
	
	
	
}
