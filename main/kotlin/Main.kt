import jogocartas.JogoDeCartas
import jogocartas.LeitorCartasCSV

fun main() {
    // Crie o leitor de cartas e o jogo
    val leitorCartas = LeitorCartasCSV("C:\\Users\\wesle\\INTELLIJ\\Jogo\\src\\main\\kotlin\\jogocartas\\cartas.csv")
    val jogadores = listOf("Jogador1", "Jogador2") // Adicione mais jogadores, se necessário
    val jogo = JogoDeCartas(jogadores, leitorCartas)

    // Distribua as cartas iniciais
    jogo.distribuirCartas()

    while (!jogo.verificarJogoTerminou()) {
        // Realize uma rodada do jogo
        jogo.realizarRodada()

        // Verifique o limite de cartas na mão após cada rodada
        jogo.verificarLimiteCartasMao()

        // Equipar um monstro com um equipamento
        val jogadorEquipar = "Jogador1"
        val monstroEquipar = "Monstro1"
        val equipamentoEquipar = "Equipamento1"
        jogo.equiparMonstro(jogadorEquipar, monstroEquipar, equipamentoEquipar)

        // Alternar o estado de um monstro em um tabuleiro
        val jogador = "Jogador1"
        val monstroNome = "Monstro1"
        val tabuleiroDoJogador = jogo.obterTabuleiroDoJogador(jogador)

        if (tabuleiroDoJogador != null) {
            tabuleiroDoJogador.alternarEstado(monstroNome, "novoEstado")
        } else {
            println("$jogador não encontrado no jogo.")
        }

        // Jogador compra uma carta
        val jogadorCompraCarta = "Jogador1"
        jogo.comprarCarta(jogadorCompraCarta)

        // Jogador posiciona um monstro no tabuleiro
        val jogadorPosicionaMonstro = "Jogador1"
        val monstroParaPosicionar = "Monstro1"
        jogo.posicionarMonstroNoTabuleiro(jogadorPosicionaMonstro, monstroParaPosicionar)

        // Mostrar as mãos dos jogadores
        val jogadorParaMostrar = "Jogador1"
        jogo.mostrarMaos()

        // Pausa entre as rodadas (opcional)
        Thread.sleep(1000) // Aguarda 1 segundo antes de continuar a próxima rodada
    }

    // Após o término do jogo, verifique o vencedor
    jogo.verificarVencedor()
}
