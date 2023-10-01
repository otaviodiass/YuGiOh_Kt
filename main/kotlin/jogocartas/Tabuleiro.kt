package jogocartas

class Tabuleiro(val jogador: String, private val maos: MutableMap<String, MutableList<Carta>>) {
    val monstros = mutableMapOf<String, Carta>() // Mapa para rastrear os monstros no tabuleiro
    val estadoAtaque = mutableMapOf<String, String>() // Mapa para rastrear o estado de cada monstro


    fun posicionarMonstro(nomeMonstro: String, novoEstado: String) {
        if (monstros.size < 5) {
            val monstro = maos[jogador]?.find { it.nome == nomeMonstro } // Encontra o monstro na mão do jogador
            if (monstro != null && monstro.tipo.equals("monstro", true)) { // Verifica se o monstro é válido
                monstros[nomeMonstro] = monstro // Adiciona o monstro ao tabuleiro
                estadoAtaque[nomeMonstro] = novoEstado // Define o estado do monstro
                maos[jogador]?.remove(monstro) // Remove o monstro da mão do jogador
                println("$jogador posicionou o monstro $nomeMonstro no tabuleiro em estado de $novoEstado.")
            } else {
                println("$nomeMonstro não é um monstro válido para ser posicionado.")
            }
        } else {
            println("$jogador já possui 5 monstros no tabuleiro. Remova um para posicionar outro.")
        }
    }


    fun resetarAtaques() {
        estadoAtaque.keys.forEach { monstroNome ->
            estadoAtaque[monstroNome] = "ataque" // Reseta o estado dos monstros para "ataque"
        }
    }


    // Função para alternar o estado de um monstro no tabuleiro
    fun alternarEstado(monstroNome: String, novoEstado: String) {
        val estadoAtual = estadoAtaque[monstroNome]
        if (estadoAtual != null) {
            estadoAtaque[monstroNome] = novoEstado
            println("$jogador alterou o estado do monstro $monstroNome para $novoEstado.")
        } else {
            println("$monstroNome não encontrado no tabuleiro.")
        }
    }
}
