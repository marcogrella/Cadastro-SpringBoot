package com.br.cadastro.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.br.cadastro.model.Pessoa;
import com.br.cadastro.model.Telefone;
import com.br.cadastro.repository.PessoaRepository;
import com.br.cadastro.repository.ProfissaoRepository;
import com.br.cadastro.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil; 
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@RequestMapping(method=RequestMethod.GET, value="/cadastropessoa")
	public ModelAndView inicio() {
		
		// quando iniciar a tela tem que passar um objeto vazio se não vai dar erro. 
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
		
	}
	
	
	// criado com o intuito de realizar a paginação. Quando requisitar a paginação por parte do thymeleaf
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoasPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView modelAndView, @RequestParam ("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pagePessoa);
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		modelAndView.setViewName("cadastro/cadastropessoa");
		
		return modelAndView;
		
		
	}
	
	
	
	// O ** antes da requisição serve para ignorar na url. Por exemplo, quando for editar vai 
	// bagunçar a URL e o ** faz com que seja interceptado o get salvarpessoa.
	// BindingResult é a mensagem que deve ser enviada (configurada na classe modelo e enviada para a tela).
	
	@RequestMapping(method=RequestMethod.POST, value="**/salvarpessoa", consumes = {"multipart/form-data"})   
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		//System.out.println(pessoa.getId());
		 
		// Como estamos utilizando validações fazemos o uso do if:
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			modelAndView.addObject("pessoaobj", pessoa); // devolve pra tela os dados que foram preenchidos (com msg de erros)
			
			// faz um for para encontrar os erros e apresentar na tela:
			List<String> msg = new ArrayList<String>(); // lista com os erros em vazio
			
			// ObjectError é uma lista do 
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // getDafaultMessage vem das notações feitas na classe Model.
			}
			
			modelAndView.addObject("msg", msg); // joga as mensagens devolta na tela referentes aos erros encontrados.
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView; // returna para a tela e não executa a debaixo.
		}
		
		
		// condição para verificar se há arquivo para upload:
		// a exceção foi colocada na declaração do método. 
		if(file.getSize() > 0) { 
				pessoa.setCurriculo(file.getBytes()); // cadastra um novo currículo
				pessoa.setTipoFileCurriculo(file.getContentType());
				pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		} else {
			if(pessoa.getId() != null && pessoa.getId() > 0) { // para editar um pessoa que já tem currículo
				byte[] curriculoTemp = pessoaRepository.findById(pessoa.getId()).get().getCurriculo(); // 
				String tipoFileCurriculoTemp = pessoaRepository.findById(pessoa.getId()).get().getTipoFileCurriculo();
				String nomeFileCurriculoTemp = pessoaRepository.findById(pessoa.getId()).get().getNomeFileCurriculo();

				pessoa.setCurriculo(curriculoTemp); // depois que encontra no banco, mantem o mesmo currículo.
				pessoa.setTipoFileCurriculo(tipoFileCurriculoTemp);
				pessoa.setNomeFileCurriculo(nomeFileCurriculoTemp);
				
			}
		}
		
		
		// caso não houver erros segue o fluxo:
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa()); // insere um novo objeto vazio
		
		String msg = "Usuário cadastrado com sucesso!"; // mensagem de sucesso 
		modelAndView.addObject("msg", msg); // insere um novo objeto vazio
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
		

	}
	
	
	// OBS: o GetMapping e o RequestMapping são notações com mesma finalidade, só muda o fato de GetMapping ser mais novo.
	
	// utilizado para listar o cadastro de usuários
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
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
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
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
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		
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
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo,
									@PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
							// O spring vai jogar automaticamente o objeteo Pageable como 5 e ordenado pelo nome
		
		Page<Pessoa> pessoas = null;
		
		//System.out.println(nomepesquisa);
		//System.out.println(pesquisasexo);
		
		if(pesquisasexo!=null && !pesquisasexo.isEmpty()) {
			System.out.println("Chamo o método pesquisa por nome e sexo com paginação");
			pessoas = pessoaRepository.findPessoaByNameAndSexPage(nomepesquisa, pesquisasexo, pageable);
		} else {
			System.out.println("Chamo o método pesquisa por nome com paginação");
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}
		
		
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		return modelAndView;
		
	}
	
	// Utilizado para fazer relatório dos usuários. É o mesmo mapeamento que o decima (pesquisar pessoa). Só que está alterado 
	// para que quando seja clicado ao invez de enviar um post fará o envio de um get (alterado por intermédio de um javascript)
	// Esse método não recarrega a página, pra não perder o valor que está setado na lista da tela.
	@GetMapping("**/pesquisarpessoa")
	public void imprimePDF(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		
		//System.out.println("Get");
		//System.out.println(nomepesquisa);
		//System.out.println(pesquisasexo);
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesquisasexo!=null && !pesquisasexo.isEmpty() && nomepesquisa!=null && !nomepesquisa.isEmpty()) {
			System.out.println("Sexo e nome enviados Enviado");
			System.out.println("Chamei método nome Name and Sex");
			pessoas = pessoaRepository.findPessoaByNameAndSex(nomepesquisa, pesquisasexo);
		
			// verifico se tem nome
		} else if(nomepesquisa!=null && !nomepesquisa.isEmpty()) {
			System.out.println("Só tem nome");
			System.out.println("Chamei método findByName");
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
			
			// verifico se tem sexo
		} else if(pesquisasexo!=null && !pesquisasexo.isEmpty()) {
			System.out.println("Só tem sexo");
			System.out.println("Chamei método findBySex");
			pessoas = pessoaRepository.findPessoaBySex(pesquisasexo);
		}
		
		// não tem nome nem sexo
		else {
			System.out.println("Não tem nem nome nem sexo: ");
			System.out.println("Chamo o método findAll()");
			Iterable<Pessoa> listaIterator = pessoaRepository.findAll();
			for (Pessoa pessoa : listaIterator) {
				pessoas.add(pessoa);
		}
			
		}
		
		
		// fazer o relatório utilizando o reportUtil
		
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		// tamanho da resposta para o navegador
		response.setContentLength(pdf.length);
		
		// define para a resposta o tipo de arquivo.
		response.setContentType("application/octet-stream");
		
		// define o cabeçalho da resposta:
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		// finalizar a resposta para o navegador:
		response.getOutputStream().write(pdf);
		
		
	}
	
	
	
	
	// método de download (obs: é sempre void quando envolve download)
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response){
		
		
		// Consulta o objeto da pessoa no bd
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if (pessoa.getCurriculo() != null) {
			
				
			// tamanho da resposta e tipo de arquivo para download. OBS: o tipo do arquivo pode ser generico: application/octet-stream
			response.setContentLength(pessoa.getCurriculo().length);
			response.setContentType(pessoa.getTipoFileCurriculo()); // genérico response.setContentType("application/octet-stream");
			
			// define o cabeçalho da resposta: 
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			// finalizando a resposta e passando o arquivo:
			try {
				response.getOutputStream().write(pessoa.getCurriculo());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
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
