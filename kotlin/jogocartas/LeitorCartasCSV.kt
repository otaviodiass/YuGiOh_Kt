package jogocartas

import java.io.File


class LeitorCartasCSV(caminhoArquivo: String) {
    private val cartas: MutableList<Carta> = mutableListOf()

    init {
        lerCartas(caminhoArquivo)
    }

    private fun lerCartas(caminhoArquivo: String) {
        val linhas = File(caminhoArquivo).readLines()
        for (linha in linhas) {
            val dados = linha.split(";")
            if (dados.size >= 5) {
                val nome = dados[0]
                val descricao = dados[1]
                val ataque = dados[2].toInt()
                val defesa = dados[3].toInt()
                val tipo = dados[4]
                val carta = Carta(nome, descricao, ataque, defesa, tipo)
                cartas.add(carta)
            }
        }
    }

    fun pegarCartasAleatorias(quantidade: Int): List<Carta> {
        val cartasEmbaralhadas = cartas.shuffled().take(quantidade)
        return cartasEmbaralhadas
    }

    fun cartasEstaoVazias(): Boolean {
        return cartas.isEmpty()
    }
}
