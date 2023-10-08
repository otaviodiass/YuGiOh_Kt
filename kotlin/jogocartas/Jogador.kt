package jogocartas

import java.util.ArrayList

class Jogador(nome: String) {
    val nome: String = nome
    var vida: Int = 10000
    val tabuleiro: MutableList<Carta> = ArrayList()
    val mao: MutableList<Carta> = ArrayList()

    fun temEspacoNoTabuleiro(): Boolean {
        return tabuleiro.size < 5
    }

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

    fun mostrarMao() {
        println("Mão do jogador $nome:")
        for ((i, carta) in mao.withIndex()) {
            if (carta is Carta) {
                val tipo = if (carta.tipo == "monstro") "Monstro" else "Equipamento"
                println("${i + 1}. ${carta.nome} - Tipo: $tipo, Ataque: ${carta.ataque}, Defesa: ${carta.defesa}")
            }
        }
    }

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

    fun descartarCarta(cartaId: Int) {
        if (cartaId in 1..mao.size) {
            mao.removeAt(cartaId - 1)
        } else {
            println("ID de carta inválido.")
        }
    }

    fun alterarEstadoMonstro(monstroId: Int, novoEstado: String) {
        if (monstroId in 1..tabuleiro.size) {
            val monstro = tabuleiro[monstroId - 1]
            monstro.alterarEstado(novoEstado)
        } else {
            println("ID de monstro inválido.")
        }
    }

    fun mostrarTabuleiro() {
        println("Tabuleiro do jogador $nome:")
        for ((i, monstro) in tabuleiro.withIndex()) {
            println("${i + 1}. ${monstro.nome} - Ataque: ${monstro.ataque}, Defesa: ${monstro.defesa}")
        }
    }

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

    fun temMonstro(): Boolean {
        return tabuleiro.isNotEmpty()
    }
}
