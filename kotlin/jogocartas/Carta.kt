package jogocartas

// A classe Carta representa uma carta do jogo Yu-Gi-Oh!
class Carta(
    val nome: String, // O nome da carta
    val descricao: String, // A descrição da carta
    var defesa: Int, // A pontuação de defesa da carta
    var ataque: Int, // A pontuação de ataque da carta
    val tipo: String // O tipo da carta, que pode ser "monstro" ou "equipamento"
) {
    var estado: String? = if (tipo == "monstro") "ataque" else null // O estado da carta, que pode ser "ataque" ou "defesa"
    val ataqueOriginal: Int = ataque // A pontuação de ataque original da carta
    val defesaOriginal: Int = defesa // A pontuação de defesa original da carta

    // Método para calcular o dano com base no estado da carta
    fun calcularDano(): Int {
        return when (estado) {
            "ataque" -> ataque
            "defesa" -> defesa
            else -> 0
        }
    }

    // Método para alterar o estado da carta
    fun alterarEstado(novoEstado: String) {
        if (tipo == "monstro") {
            when (novoEstado) {
                "ataque" -> {
                    ataque = ataqueOriginal
                    defesa = defesaOriginal
                }
                "defesa" -> defesa = defesaOriginal
            }
            estado = novoEstado
        }
    }

    // Método para receber dano quando a carta está em estado de ataque
    fun receberDano(dano: Int) {
        if (tipo == "monstro" && estado == "ataque") {
            defesa -= dano
            if (defesa < 0) {
                defesa = 0
            }
        }
    }
}
