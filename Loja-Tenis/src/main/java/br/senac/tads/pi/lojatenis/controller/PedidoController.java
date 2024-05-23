package br.senac.tads.pi.lojatenis.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.senac.tads.pi.lojatenis.model.ItemPedido;
import br.senac.tads.pi.lojatenis.model.Pedido;
import br.senac.tads.pi.lojatenis.service.PedidoRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedido")
@Service
public class PedidoController {
    @Autowired
    private PedidoRepository repo;

    @GetMapping({"/MostraPedidos"})
    public String showPedidosList(Model model, HttpSession session) {
        List<Pedido> pedidos = repo.findAll(Sort.by(Sort.Direction.DESC, "dataPedido"));
        model.addAttribute("pedidos", pedidos);
        List<List<ItemPedido>> detalhesProdutos = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            List<ItemPedido> itensPedido = pedido.getItens();
            detalhesProdutos.add(itensPedido);
        }
        model.addAttribute("detalhesProdutos", detalhesProdutos);

        return "pedidos/MostraPedidos";
    }

    @GetMapping("/pedidos")
    public String getPedido() {
        return "pedidos/MostraPedidos";
    }

}