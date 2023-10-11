package jogocartas

import java.io.File


// A classe LeitorCartasCSV é responsável por ler as cartas do jogo a partir de um arquivo CSV
class LeitorCartasCSV(caminhoArquivo: String) {
    private val cartas: MutableList<Carta> = mutableListOf()

    // Inicializa o leitor lendo as cartas do arquivo CSV
    init {
        lerCartas(caminhoArquivo)
    }

    // Método para ler as cartas do arquivo CSV
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

    // Método para pegar cartas aleatórias da lista de cartas
    fun pegarCartasAleatorias(quantidade: Int): List<Carta> {
        val cartasEmbaralhadas = cartas.shuffled().take(quantidade)
        return cartasEmbaralhadas
    }

    // Verifica se a lista de cartas está vazia
    fun cartasEstaoVazias(): Boolean {
        return cartas.isEmpty()
    }
}
