package jogocartas

import java.util.ArrayList
// A classe Jogador representa um jogador do jogo Yu-Gi-Oh!
class Jogador(nome: String) {
    val nome: String = nome // O nome do jogador
    var vida: Int = 10000 // A vida do jogador
    val tabuleiro: MutableList<Carta> = ArrayList() // O tabuleiro do jogador, onde as cartas são posicionadas
    val mao: MutableList<Carta> = ArrayList() // A mão do jogador, onde as cartas são mantidas

    // Verifica se há espaço no tabuleiro para posicionar um novo monstro
    fun temEspacoNoTabuleiro(): Boolean {
        return tabuleiro.size < 5
    }

    // Método para posicionar um monstro no tabuleiro
    fun posicionarMonstro(cartaId: Int, estado: String): Boolean {
        if (temEspacoNoTabuleiro()) {
            if (cartaId in 1..mao.size) {
                val carta = mao.removeAt(cartaId - 1)
                if (carta.tipo == "monstro") {
                    carta.alterarEstado(estado)
                    tabuleiro.add(carta)
                    return true
                } else {
                    println("Você só pode posicionar monstros no tabuleiro.")
                    mao.add(carta)
                    return false
                }
            } else {
                println("ID de carta inválido.")
                return false
            }
        } else {
            println("Você já tem 5 monstros no tabuleiro. Não pode posicionar mais.")
            return false
        }
    }

    // Método para mostrar a mão do jogador
    fun mostrarMao() {
        println("Mão do jogador $nome:")
        for ((i, carta) in mao.withIndex()) {
            if (carta is Carta) {
                val tipo = if (carta.tipo == "monstro") "Monstro" else "Equipamento"
                println("${i + 1}. ${carta.nome} - Tipo: $tipo, Ataque: ${carta.ataque}, Defesa: ${carta.defesa}")
            }
        }
    }

    // Método para equipar um monstro com uma carta de equipamento
    fun equiparMonstro(monstroId: Int, carta: Carta) {
        if (monstroId in 1..tabuleiro.size) {
            val monstro = tabuleiro[monstroId - 1]
            if (monstro.tipo == "monstro" && carta.tipo == "equipamento") {
                monstro.ataque += carta.ataque
                monstro.defesa += carta.defesa
                mao.remove(carta)
                mostrarTabuleiro()
            } else {
                println("Não é possível equipar esta carta.")
            }
        } else {
            println("ID de monstro inválido.")
        }
    }

    // Método para descartar uma carta da mão
    fun descartarCarta(cartaId: Int) {
        if (cartaId in 1..mao.size) {
            mao.removeAt(cartaId - 1)
        } else {
            println("ID de carta inválido.")
        }
    }

    // Método para alterar o estado de um monstro (ataque/defesa)
    fun alterarEstadoMonstro(monstroId: Int, novoEstado: String) {
        if (monstroId in 1..tabuleiro.size) {
            val monstro = tabuleiro[monstroId - 1]
            monstro.alterarEstado(novoEstado)
        } else {
            println("ID de monstro inválido.")
        }
    }

    // Método para mostrar o tabuleiro do jogador
    fun mostrarTabuleiro() {
        println("Tabuleiro do jogador $nome:")
        for ((i, monstro) in tabuleiro.withIndex()) {
            println("${i + 1}. ${monstro.nome} - Ataque: ${monstro.ataque}, Defesa: ${monstro.defesa}")
        }
    }

    // Método para realizar um ataque contra o oponente
    fun atacarOponente(oponente: Jogador, monstroId: Int, alvoId: Int) {
        if (monstroId in 1..tabuleiro.size && alvoId in 1..oponente.tabuleiro.size) {
            val monstro = tabuleiro[monstroId - 1]
            val alvo = oponente.tabuleiro[alvoId - 1]
            if (monstro.tipo == "monstro" && alvo.tipo == "monstro" && monstro.estado == "ataque") {
                val dano = monstro.calcularDano()
                alvo.receberDano(dano)
                println("$nome utilizou a carta ${monstro.nome} contra o monstro ${alvo.nome} causando $dano de dano.")
            } else {
                println("Você só pode atacar com monstros em estado de ataque e atacar monstros do oponente.")
            }
        } else {
            println("ID de monstro do jogador ou do oponente inválido.")
        }
    }

    // Verifica se o jogador tem pelo menos um monstro no tabuleiro
    fun temMonstro(): Boolean {
        return tabuleiro.isNotEmpty()
    }
}
