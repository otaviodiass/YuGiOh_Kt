package jogocartas

class Carta(
    val nome: String,
    val descricao: String,
    var defesa: Int,
    var ataque: Int,
    val tipo: String
) {
    var estado: String? = if (tipo == "monstro") "ataque" else null
    val ataqueOriginal: Int = ataque
    val defesaOriginal: Int = defesa

    fun calcularDano(): Int {
        return when (estado) {
            "ataque" -> ataque
            "defesa" -> defesa
            else -> 0
        }
    }

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

    fun receberDano(dano: Int) {
        if (tipo == "monstro" && estado == "ataque") {
            defesa -= dano
            if (defesa < 0) {
                defesa = 0
            }
        }
    }
}
