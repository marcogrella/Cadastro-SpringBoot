package com.br.cadastro;

// criada somente para testar...

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	
	@RequestMapping("/")
	public String index() {
		return "index";    // redireciona para a página index.html (coloca-se somente index)
	}
	
}
