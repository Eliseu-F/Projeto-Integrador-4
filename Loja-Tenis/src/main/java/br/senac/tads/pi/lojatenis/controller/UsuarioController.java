package br.senac.tads.pi.lojatenis.controller;

import br.senac.tads.pi.lojatenis.model.Usuario;
import br.senac.tads.pi.lojatenis.model.UsuarioDto;
import br.senac.tads.pi.lojatenis.service.UsuarioRepository;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @GetMapping({"", "/"})
    public String showUsuariosList(Model model) {
        List<Usuario> usuarios = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("usuarios", usuarios);
        return "usuarios/index";
    }

    @GetMapping("/create")
    public String showCriaUsuario(Model model) {
        UsuarioDto usuarioDto = new UsuarioDto();
        model.addAttribute("usuarioDto", usuarioDto);
        return "usuarios/CriaUsuario";
    }

    @PostMapping("/create")
    public String criarUsuario(@ModelAttribute("usuarioDto") @Valid UsuarioDto usuarioDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "usuarios/CriaUsuario";
        }

        if (repo.existsByEmail((usuarioDto.getEmail()))) {
            bindingResult.rejectValue("email", "error.usuarioDto", "Este email já está em uso");
            return "usuarios/CriaUsuario";
        }

        // Mapear UsuarioDto para a entidade Usuario
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setCpf(usuarioDto.getCpf());
        usuario.setSenha(usuarioDto.getSenha());
        usuario.setGrupo(usuarioDto.getGrupo());
        usuario.setStatus(usuarioDto.getStatus());
        // Configurar atributos de usuarioDto para usuario

        // Salvar usuario no repositório
        repo.save(usuario);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/usuarios";
    }

    @GetMapping("/edit")
    public String MostraEdicao(Model model, @RequestParam int id) {

        try {
            Usuario usuario = repo.findById(id).get();
            model.addAttribute("usuario", usuario);

            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setId(usuario.getId());
            usuarioDto.setNome(usuario.getNome());
            usuarioDto.setEmail(usuario.getEmail());
            usuarioDto.setCpf(usuario.getCpf());
            usuarioDto.setSenha(usuario.getSenha());
            usuarioDto.setGrupo(usuario.getGrupo());
            usuarioDto.setStatus(usuario.getStatus());

            model.addAttribute("usuarioDto", usuarioDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/usuarios";
        }
        return "usuarios/EditarUsuario";

    }

    @PostMapping("/edit")
    public String editarUsuario(Model model, @RequestParam int id, @Valid @ModelAttribute UsuarioDto usuarioDto, BindingResult bindingResult) {

        try {
            Usuario usuario = repo.findById(id).get();
            model.addAttribute("usuario", usuario);

            if (bindingResult.hasErrors()) {
                // Se houver erros de validação, retorne para o formulário de registro
                return "usuarios/EditarUsuario";
            }

            // Configurar atributos de usuarioDto para usuario
            usuario.setNome(usuarioDto.getNome());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setCpf(usuarioDto.getCpf());
            usuario.setSenha(usuarioDto.getSenha());
            usuario.setGrupo(usuarioDto.getGrupo());

            // Salvar usuario no repositório
            repo.save(usuario);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());

        }
            // Redirecionar para a lista de usuários após a criação bem-sucedida
            return "redirect:/usuarios";
    }
}
