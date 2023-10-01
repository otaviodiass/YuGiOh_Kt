package jogocartas

import java.io.BufferedReader
import java.io.FileReader



// Classe para ler e fornecer cartas a partir de um arquivo CSV
class LeitorCartasCSV(caminhoArquivo: String) {
    // Lista de cartas lidas do arquivo CSV
    private val cartas = mutableListOf<Carta>()

    init {
        // Inicializador: lê as cartas do arquivo CSV ao criar uma instância da classe
        lerCartas(caminhoArquivo)
    }

    // Função privada para ler as cartas do arquivo CSV
    private fun lerCartas(caminhoArquivo: String) {
        // Utiliza um BufferedReader para ler o arquivo linha por linha
        BufferedReader(FileReader(caminhoArquivo)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // Divide a linha em campos usando ';' como delimitador
                val dados = line!!.split(";".toRegex()).toTypedArray()
                if (dados.size >= 5) {
                    // Extrai informações dos campos e cria uma instância de Carta
                    val nome = dados[0]
                    val descricao = dados[1]
                    val ataque = dados[2].toInt()
                    val defesa = dados[3].toInt()
                    val tipo = dados[4]
                    val carta = Carta(nome, descricao, ataque, defesa, tipo)
                    cartas.add(carta) // Adiciona a carta à lista de cartas
                }
            }
        }
    }

    // Método público para pegar uma lista de cartas aleatórias de acordo com a quantidade especificada
    fun pegarCartasAleatorias(quantidade: Int): List<Carta> {
        val cartasEmbaralhadas = cartas.shuffled() // Embaralha as cartas
        return cartasEmbaralhadas.subList(0, quantidade) // Retorna as primeiras cartas embaralhadas
    }

    // Método público para verificar se o conjunto de cartas está vazio
    fun cartasEstaoVazias(): Boolean {
        return cartas.isEmpty()
    }
}
