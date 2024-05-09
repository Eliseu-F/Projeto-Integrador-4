package br.senac.tads.pi.lojatenis.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import br.senac.tads.pi.lojatenis.model.ClienteDto;
import br.senac.tads.pi.lojatenis.model.EnderecoDto;
import br.senac.tads.pi.lojatenis.model.FormasDePagamento;
import br.senac.tads.pi.lojatenis.model.PagamentoDto;
import br.senac.tads.pi.lojatenis.model.Carrinho;
import br.senac.tads.pi.lojatenis.model.Cliente;
import br.senac.tads.pi.lojatenis.model.Endereco;
import br.senac.tads.pi.lojatenis.model.Produto;
import br.senac.tads.pi.lojatenis.service.ClienteRepository;
import br.senac.tads.pi.lojatenis.service.EnderecoRepository;
import br.senac.tads.pi.lojatenis.service.ProdutoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.Optional;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;

    @GetMapping
    public String mostrarCarrinho(HttpSession session, Model model, HttpServletRequest request) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

        HttpSession session2 = request.getSession();
        Cliente clienteLogado = (Cliente) session2.getAttribute("clienteLogado");

        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

            Endereco enderecoCliente = clienteLogado.getEnderecoPadrao();
            if (enderecoCliente != null) {
                // Adicionar as informações do endereço ao modelo
                model.addAttribute("enderecoCliente", enderecoCliente);
            } else {
                model.addAttribute("enderecoCliente", "Endereço não encontrado");
            }

        } else {
            model.addAttribute("usuarioLogado", false);

        }

        // SE NAO EXISTIR CARRINHO, CRIA UM NOVO
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }

        // Adicione o carrinho ao modelo para exibição na página
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam int produtoId, @RequestParam int quantidade,
            HttpSession session) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // CRIA O CARRINHO NA SESSÃO DO SITE SE NÃO EXISTIR
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }

        // ADICIONA PRODUTO AO CARRINHO
        carrinho.adicionarItem(produto, quantidade);

        // REDIRECIONA PARA PAGINA DO CARRINHO
        return "redirect:/carrinho";
    }

    @PostMapping("/remove")
    public String removeProduto(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        // ADICIONA PRODUTO AO CARRINHO
        carrinho.removerItem(produto);

        if (carrinho.getItens().isEmpty()) {
            // Resetar o valor do frete para zero
            carrinho.setFrete(BigDecimal.ZERO);
        }
        // REDIRECIONA PARA PAGINA DO CARRINHO
        return "redirect:/carrinho";
    }

    @PostMapping("/aumentar")
    public String aumentarQuantidade(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        carrinho.aumentaQuantidade(produto);

        return "redirect:/carrinho";
    }

    @PostMapping("/diminuir")
    public String diminuirQuantidade(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        carrinho.diminuiQuantidade(produto);

        return "redirect:/carrinho";
    }

    @PostMapping("/adicionarFrete")
    public String adicionarFrete(@RequestParam BigDecimal frete, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }
        // Define o valor do frete no carrinho
        carrinho.setFrete(frete);

        return "redirect:/carrinho";
    }

    @GetMapping("/checkout")
    public String finalizarPedido(HttpSession session, Model model, HttpServletRequest request) {

        Cliente clienteLogado = (Cliente) request.getSession().getAttribute("clienteLogado");

        if (clienteLogado != null) {
            // Se estiver logado, redireciona para tela de checkout
            Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
            Endereco enderecoCliente = clienteLogado.getEnderecoPadrao();

            model.addAttribute("carrinho", carrinho);
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());
            model.addAttribute("enderecoCliente", enderecoCliente);
            model.addAttribute("formaDePagamento", carrinho.getFormaDePagamento());


            return "checkout/checkout";
        } else {
            // PAGINA DE LOGIN
            return "redirect:/login";
        }
    }

    @GetMapping("/entrega")
    public String finalizarEntrega(Model model, @RequestParam(required = false) Integer id,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Se não houver um ID fornecido ou o cliente não estiver logado, redirecione
        // para a página de login
        if (id == null || clienteLogado == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEnderecos(cliente.getEnderecos());
        EnderecoDto enderecoDto = new EnderecoDto();
        // Restante do seu código para finalizar a entrega...

        // Se o cliente estiver logado, adicione os atributos ao modelo e retorne a
        // página de entrega
        model.addAttribute("usuarioLogado", true);
        model.addAttribute("clienteId", clienteLogado.getId());
        model.addAttribute("nomeCliente", clienteLogado.getNome());
        model.addAttribute("enderecoDto", enderecoDto);
        model.addAttribute("cliente", cliente);
        model.addAttribute("clienteDto", clienteDto);

        return "checkout/entrega";
    }

    @PostMapping("/escolhe")
    public String defineEnderecoPadrao(@RequestParam int id, HttpSession session) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Atualizar o endereço atual como o padrão no cliente
        clienteLogado.setEnderecoPadrao(endereco);
        clienteRepository.save(clienteLogado);

        return "checkout/pagamento";
    }

    @PostMapping("/escolhe/add")
    public String adicionarEndereco(Model model, @ModelAttribute("enderecoDto") @Valid EnderecoDto enderecoDto,
            BindingResult bindingResult, HttpSession session, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("enderecoDto", enderecoDto);
            return "redirect:" + request.getHeader("Referer");
        }

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        // Cria um novo endereço e configura seus detalhes
        Endereco novoEndereco = new Endereco();
        novoEndereco.setEndereco("ENTREGA");
        novoEndereco.setCep(enderecoDto.getCep());
        novoEndereco.setLogradouro(enderecoDto.getLogradouro());
        novoEndereco.setNumero(enderecoDto.getNumero());
        novoEndereco.setComplemento(enderecoDto.getComplemento());
        novoEndereco.setBairro(enderecoDto.getBairro());
        novoEndereco.setCidade(enderecoDto.getCidade());
        novoEndereco.setUf(enderecoDto.getUf());
        novoEndereco.setCliente(clienteLogado); // Associa o endereço ao cliente logado

        enderecoRepository.save(novoEndereco);

        // Recupera o cliente novamente para atualizar a lista de endereços apos
        // adicionar um novo endereço
        Cliente cliente = clienteRepository.findById(clienteLogado.getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEnderecos(cliente.getEnderecos());

        // Adiciona os atributos do modelo novamente
        model.addAttribute("usuarioLogado", true);
        model.addAttribute("clienteId", clienteLogado.getId());
        model.addAttribute("nomeCliente", clienteLogado.getNome());
        model.addAttribute("enderecoDto", new EnderecoDto()); // Limpa o DTO de endereço
        model.addAttribute("cliente", cliente);
        model.addAttribute("clienteDto", clienteDto);

        return "checkout/entrega";
    }

    @PostMapping("/checkout/processar")
    public String processarPagamento(@ModelAttribute("pagamentoDto") @Valid PagamentoDto pagamentoDto,
            BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, redirecione de volta para a página de pagamento
            return "pagamento";
        }

        // Aqui você pode processar as informações de pagamento, por exemplo, salvar no
        // banco de dados
        // ou realizar validações, etc.

        // Em seguida, você pode adicionar as informações ao carrinho, se necessário
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        FormasDePagamento pagamento = carrinho.getFormaDePagamento();
        if (pagamento == null) {
            pagamento = new FormasDePagamento();
            carrinho.setFormaDePagamento(pagamento);
        }

        // Adicione as informações de pagamento ao objeto FormasDePagamento
        pagamento.setTipo(pagamentoDto.getTipo());
        pagamento.setNumeroCartao(pagamentoDto.getNumeroCartao());
        pagamento.setCodigoVerificador(pagamentoDto.getCodigoVerificador());
        pagamento.setNomeCompleto(pagamentoDto.getNomeCompleto());
        pagamento.setDataVencimento(pagamentoDto.getDataVencimento());
        pagamento.setQuantidadeParcelas(pagamentoDto.getQuantidadeParcelas());

        return "redirect:/carrinho/checkout";
    }

}
