package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import exceptions.FilmeSemEstoqueExceptions;
import exceptions.LocadoraException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testeLocacao() throws Exception {
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        //açao
        Locacao locacao = service.alugarFilme(usuario, filme);

        //verificaçao
        error.checkThat(locacao.getValor(), is(5.0));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), obterDataComDiferencaDias(0)), is(true));
    }

    //Elegante (boa quando garante que a exceção é lançada apenas pelo motivo testado, ñ preciso da mensagem)
    @Test(expected = FilmeSemEstoqueExceptions.class)
    public void testLocacao_FilmeSemEstoque() throws Exception {
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 0, 5.0);

        //açao
        service.alugarFilme(usuario, filme);
    }

    //Robusta (com try/catch)
    @Test
    public void testLocacao_usuarioVazio() throws FilmeSemEstoqueExceptions {
        LocacaoService service = new LocacaoService();
        Filme filme = new Filme("Filme 2", 2, 4.0);
        try {
            service.alugarFilme(null, filme);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    //Nova (usa a Rule)
    @Test
    public void testLocacao_filmeVazio() throws FilmeSemEstoqueExceptions, LocadoraException {
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        service.alugarFilme(usuario, null);
    }
}

