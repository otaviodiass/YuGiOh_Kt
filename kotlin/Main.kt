import jogocartas.Jogador
import jogocartas.LeitorCartasCSV
import kotlin.math.abs

fun main() {
    val jogo = JogoYuGiOh()
    jogo.iniciarJogo()
}

// Classe principal do programa que inicia e controla o jogo
class JogoYuGiOh {
    private val jogador1 = Jogador("Jogador 1")
    private val jogador2 = Jogador("Jogador 2")
    private val maxRodadas = 10
    private var turno = 1
    private val leitorCartas = LeitorCartasCSV("C:COLOCAR CAMINHO DO DIRETÓRIO DO CARTAS.CSV")

    // Método para iniciar o jogo
    fun iniciarJogo() {
        for (rodada in 1..maxRodadas) {
            println("\nRodada $rodada")

            for (jogador in listOf(jogador1, jogador2)) {
                repeat(5) {
                    if (jogador.mao.size < 5) {
                        jogador.mao.add(leitorCartas.pegarCartasAleatorias(1)[0])
                    } else if (jogador.mao.size > 10) {
                        println("${jogador.nome} tem 10 cartas na mão. Descartando uma carta.")
                        jogador.mao.removeAt(0)
                    }
                }
            }

            val jogadorAtual = if (turno == 1) jogador1 else jogador2
            val oponente = if (turno == 1) jogador2 else jogador1

            println("\nTurno do ${jogadorAtual.nome}")
            jogadorAtual.mostrarMao()

            print("Escolha uma ação:\n" +
                    "a) Posicionar um novo monstro no tabuleiro;\n" +
                    "b) Equipar um monstro com uma carta de equipamento;\n" +
                    "c) Descartar uma carta da mão;\n" +
                    "d) Realizar um ataque contra o oponente;\n" +
                    "e) Alterar o estado de um monstro (ataque/defesa);\n" +
                    "s) Sair.\n" +
                    "Escolha a letra da ação: ")
            val acao = readLine()?.toLowerCase() ?: ""
            when (acao) {
                "a" -> {
                    jogadorAtual.mostrarMao()
                    print("Escolha o ID da carta na mão para posicionar como monstro: ")
                    val cartaId = readLine()?.toInt() ?: 0
                    if (cartaId in 1..jogadorAtual.mao.size) {
                        if (jogadorAtual.temEspacoNoTabuleiro()) {
                            print("Escolha o estado do monstro (ataque/defesa): ")
                            val estado = readLine()?.toLowerCase() ?: ""
                            if (estado in listOf("ataque", "defesa")) {
                                if (jogadorAtual.posicionarMonstro(cartaId, estado)) {
                                    print("Você tem certeza da opção escolhida (S/N): ")
                                    val confirmacao = readLine()?.toLowerCase() ?: ""
                                    if (confirmacao == "s") {
                                        jogadorAtual.tabuleiro.last().alterarEstado(estado)
                                    } else {
                                        println("Posicionamento de monstro cancelado.")
                                    }
                                } else {
                                    println("Posicionamento de monstro falhou.")
                                }
                            } else {
                                println("Estado inválido. Escolha entre 'ataque' e 'defesa'.")
                            }
                        } else {
                            println("Você já tem 5 monstros no tabuleiro. Não pode posicionar mais.")
                        }
                    } else {
                        println("ID de carta inválido.")
                    }

                    jogadorAtual.mostrarMao()
                }
                "b" -> {
                    jogadorAtual.mostrarTabuleiro()
                    if (jogadorAtual.temMonstro()) {
                        print("Escolha o ID do monstro no tabuleiro: ")
                        val monstroId = readLine()?.toInt() ?: 0
                        if (monstroId in 1..jogadorAtual.tabuleiro.size) {
                            jogadorAtual.mostrarMao()
                            print("Escolha o ID da carta na mão para equipar: ")
                            val cartaId = readLine()?.toInt() ?: 0
                            if (cartaId in 1..jogadorAtual.mao.size) {
                                jogadorAtual.equiparMonstro(monstroId, jogadorAtual.mao[cartaId - 1])
                            } else {
                                println("Carta inválida.")
                            }
                        } else {
                            println("Monstro inválido.")
                        }
                    } else {
                        println("Não há monstros no tabuleiro para equipar.")
                    }
                }
                "c" -> {
                    jogadorAtual.mostrarMao()
                    print("Escolha o ID da carta na mão para descartar: ")
                    val cartaId = readLine()?.toInt() ?: 0
                    if (cartaId in 1..jogadorAtual.mao.size) {
                        jogadorAtual.descartarCarta(cartaId)
                    } else {
                        println("Carta inválida.")
                    }
                }
                "d" -> {
                    jogadorAtual.mostrarTabuleiro()
                    if (jogadorAtual.temMonstro()) {
                        print("Escolha o ID do monstro no tabuleiro para atacar: ")
                        val monstroId = readLine()?.toInt() ?: 0
                        val alvoJogador = if (jogadorAtual == jogador1) jogador2 else jogador1
                        alvoJogador.mostrarTabuleiro()
                        print("Escolha o ID do monstro do oponente para atacar: ")
                        val alvoId = readLine()?.toInt() ?: 0
                        if (monstroId in 1..jogadorAtual.tabuleiro.size && alvoId in 1..alvoJogador.tabuleiro.size) {
                            jogadorAtual.atacarOponente(oponente, monstroId, alvoId)
                            val alvo = alvoJogador.tabuleiro[alvoId - 1]
                            val dano = jogadorAtual.tabuleiro[monstroId - 1].calcularDano()
                            if (alvo.defesa < 0) {
                                println("${alvo.nome} foi descartado!")
                                alvoJogador.tabuleiro.removeAt(alvoId - 1)
                            }
                            if (alvo != null) {
                                alvo.defesa -= dano
                                if (alvo.defesa < 0) {
                                    oponente.vida -= abs(alvo.defesa)
                                    alvo.defesa = 0
                                }
                            }
                            if (alvo != null) {
                                alvo.defesa -= dano
                                if (alvo.defesa <= 0) {
                                    alvoJogador.tabuleiro.removeAt(alvoId - 1)
                                }
                            }
                            println("\n${alvo.nome} foi descartado!\n")
                            jogador1.mostrarTabuleiro()
                            jogador2.mostrarTabuleiro()
                            println("\nVida atual de ${jogadorAtual.nome}: ${jogadorAtual.vida}")
                            println("Vida atual de ${oponente.nome}: ${oponente.vida}\n")
                        } else {
                            println("Monstro inválido.")
                        }
                    } else {
                        println("Não há monstros no tabuleiro para atacar.")
                    }
                }
                "e" -> {
                    jogadorAtual.mostrarTabuleiro()
                    if (jogadorAtual.temMonstro()) {
                        print("Escolha o ID do monstro no tabuleiro para alterar o estado: ")
                        val monstroId = readLine()?.toInt() ?: 0
                        print("Escolha o novo estado (ataque/defesa): ")
                        val novoEstado = readLine()?.toLowerCase() ?: ""
                        jogadorAtual.alterarEstadoMonstro(monstroId, novoEstado)
                        val monstro = jogadorAtual.tabuleiro[monstroId - 1]
                        println("O estado de ${monstro.nome} foi alterado para $novoEstado.")
                    } else {
                        println("Não há monstros no tabuleiro para alterar o estado.")
                    }
                }
                "s" -> break
            }

            if (jogador1.vida == 0 || jogador2.vida == 0) break

            turno = 3 - turno
        }

        when {
            jogador1.vida == jogador2.vida -> println("Empate!")
            jogador1.vida > jogador2.vida -> println("${jogador1.nome} venceu!")
            else -> println("${jogador2.nome} venceu!")
        }
    }
}
