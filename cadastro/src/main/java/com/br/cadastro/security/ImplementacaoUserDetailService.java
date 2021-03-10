package com.br.cadastro.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.br.cadastro.model.Usuario;
import com.br.cadastro.repository.UsuarioRepository;

@Service
@Transactional
public class ImplementacaoUserDetailService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.isEnabled(), true, true, true, usuario.getAuthorities()); 
		
				// getAuthorities vem do atributo que foi criado no banco, relacionado a tabela role. 
	}

}
