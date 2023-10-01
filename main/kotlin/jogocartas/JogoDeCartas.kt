package jogocartas



class JogoDeCartas(
    private val jogadores: List<String>,        // Lista de jogadores
    private val leitorCartas: LeitorCartasCSV   // Leitor de cartas CSV
) {
    private val maos = HashMap<String, MutableList<Carta>>()                   // Mapa para armazenar as mãos dos jogadores
    private val maosOponentes = HashMap<String, MutableMap<String, MutableList<Carta>>>() // Mapa para armazenar as mãos dos oponentes
    private val tabuleiros = HashMap<String, Tabuleiro>()                      // Mapa para armazenar os tabuleiros dos jogadores
    private val equipamentos = mutableListOf<Carta>()                           // Lista para armazenar a coleção de equipamentos
    private val monstrosAtacaram = HashSet<String>()                            // Conjunto para rastrear monstros que já atacaram
    private val limiteCartasMao = 10                                           // Limite de cartas na mão de cada jogador
    private val equipamentoEquipadoPorRodada = mutableMapOf<String, Boolean>()  // Mapa para rastrear equipamentos equipados por rodada por cada jogador
    private var primeiraRodada = true                                          // Flag para verificar a primeira rodada do jogo
    private var rodada = 0                                                     // Contador do número da rodada atual
    private val pontuacao = mutableMapOf<String, Int>()                        // Mapa para armazenar a pontuação de cada jogador
    private var rodadaInicial = true                                            // Flag para verificar a rodada inicial do jogo


    init {
        for (jogador in jogadores) {
            pontuacao[jogador] = 10000
            equipamentoEquipadoPorRodada[jogador] = false // Inicialmente nenhum equipamento equipado
        }
    }


    fun distribuirCartas() {
        // Itera sobre cada jogador
        for (jogador in jogadores) {
            // Pega 5 cartas aleatórias do leitor de cartas e as transforma em uma lista mutável
            val mao = leitorCartas.pegarCartasAleatorias(5).toMutableList()
            // Armazena a mão do jogador no mapa de mãos, associando-a ao jogador
            maos[jogador] = mao

            // Obtém as mãos dos oponentes (outros jogadores) associando cada uma a 5 cartas aleatórias
            val maosOponentes = jogadores
                .filter { it != jogador }
                .associateWith { leitorCartas.pegarCartasAleatorias(5).toMutableList() }
                .toMutableMap()
            // Armazena as mãos dos oponentes no mapa de mãos dos oponentes, associando-as ao jogador
            this.maosOponentes[jogador] = maosOponentes

            // Inicializa o tabuleiro do jogador com sua mão
            tabuleiros[jogador] = Tabuleiro(jogador, maos)
        }
    }


    fun mostrarMaos() {
        // Itera sobre cada jogador
        for (jogador in jogadores) {
            println("$jogador:") // Imprime o nome do jogador
            maos[jogador]?.forEachIndexed { index, carta ->
                val tipo = if (carta.tipo.equals("monstro", true)) "Monstro" else "Equipamento"
                // Imprime as informações de cada carta na mão do jogador
                println(
                    "$index. Nome: ${carta.nome}, Ataque: ${carta.ataque}, Defesa: ${carta.defesa} - $tipo"
                )
                println("Descrição: ${carta.descricao}\n") // Imprime a descrição da carta
            }
            println() // Imprime uma linha em branco para separar as mãos dos jogadores
        }
    }


    fun equiparMonstro(jogador: String, monstroNome: String, equipamentoNome: String) {
        val tabuleiroJogador = tabuleiros[jogador]

        // Verifica se o tabuleiro do jogador existe e se o monstro com o nome especificado está no tabuleiro
        if (tabuleiroJogador != null && monstroNome in tabuleiroJogador.monstros) {
            val equipamento = equipamentos.firstOrNull { it.nome == equipamentoNome }

            // Verifica se o equipamento com o nome especificado existe
            if (equipamento != null) {
                val monstro = tabuleiroJogador.monstros[monstroNome]!!

                // Verifica se o jogador já equipou um equipamento nesta rodada
                if (equipamentoEquipadoPorRodada[jogador] == false) {
                    // Crie uma cópia modificada do monstro com os valores do equipamento adicionados
                    val monstroModificado = monstro.copy(
                        ataque = monstro.ataque + equipamento.ataque,
                        defesa = monstro.defesa + equipamento.defesa
                    )

                    // Atualize o mapa de monstros com o monstro modificado
                    tabuleiroJogador.monstros[monstroNome] = monstroModificado

                    println("$jogador equipou $monstroNome com $equipamentoNome.")
                    equipamentoEquipadoPorRodada[jogador] = true // Marca que um equipamento foi equipado nesta rodada
                } else {
                    println("$jogador já equipou um equipamento nesta rodada. Apenas um equipamento por rodada é permitido.")
                }
            } else {
                println("$equipamentoNome não encontrado na coleção de equipamentos.")
            }
        } else {
            println("$monstroNome não encontrado no tabuleiro de $jogador.")
        }
    }


    fun realizarAcao(jogador: String, acao: String) {
        // Verifica se é a primeira rodada e se a ação é ataque direto
        if (primeiraRodada && acao == "1") {
            println("Ataques diretos só são permitidos a partir da segunda rodada.")
            return // Retorna sem executar a ação na primeira rodada
        }


        when (acao) {
            "1" -> {
                // Ação 1: Atacar com um monstro
                println("$jogador, escolha um monstro para atacar o jogador oponente:")
                print("Escolha um monstro no seu tabuleiro para atacar: ")
                val monstroAtacante = readLine()
                if (!monstroAtacante.isNullOrBlank()) {
                    if (tabuleiros.containsKey(jogador)) {
                        val tabuleiroJogador = tabuleiros[jogador]!!
                        if (tabuleiroJogador.monstros.containsKey(monstroAtacante)) {
                            val alvoDisponivel = jogadores.filter { it != jogador && pontuacao[it]!! > 0 }
                            if (alvoDisponivel.isNotEmpty()) {
                                if (monstrosAtacaram.contains(monstroAtacante)) {
                                    println("$monstroAtacante já atacou nesta rodada. Não pode atacar novamente.")
                                } else {
                                    println("$jogador, escolha um jogador alvo para atacar:")
                                    for ((index, alvo) in alvoDisponivel.withIndex()) {
                                        println("${index + 1}. $alvo")
                                    }
                                    print("Escolha o jogador alvo (1-${alvoDisponivel.size}): ")
                                    val escolhaAlvo = readLine()
                                    if (escolhaAlvo?.matches("\\d+".toRegex()) == true) {
                                        val indiceAlvo = escolhaAlvo.toInt() - 1
                                        if (indiceAlvo >= 0 && indiceAlvo < alvoDisponivel.size) {
                                            val jogadorAlvo = alvoDisponivel[indiceAlvo]
                                            val danoCausado = realizarAtaque(jogador, jogadorAlvo, monstroAtacante)
                                            println("$jogador causou $danoCausado pontos de dano em $jogadorAlvo.")
                                            if (pontuacao[jogadorAlvo]!! <= 0) {
                                                println("$jogadorAlvo ficou sem pontos de vida.")
                                            }
                                            monstrosAtacaram.add(monstroAtacante) // Marcar o monstro como atacado
                                        } else {
                                            println("Escolha de jogador alvo inválida.")
                                        }
                                    } else {
                                        println("Escolha de jogador alvo inválida.")
                                    }
                                }
                            } else {
                                println("Não há jogadores oponentes disponíveis para atacar.")
                            }
                        } else {
                            println("$monstroAtacante não encontrado no tabuleiro de $jogador.")
                        }
                    } else {
                        println("$jogador não possui um tabuleiro.")
                    }
                } else {
                    println("Nome de monstro inválido.")
                }
            }

            "2" -> {
                // Ação 2: Usar um equipamento em um monstro
                println("$jogador escolheu usar um equipamento em um monstro.")
                println("Escolha um monstro no seu tabuleiro para equipar com um equipamento: ")
                val monstroAlvo = readLine()

                if (!monstroAlvo.isNullOrBlank()) {
                    if (tabuleiros.containsKey(jogador)) {
                        val tabuleiroJogador = tabuleiros[jogador]!!

                        if (tabuleiroJogador.monstros.containsKey(monstroAlvo)) {
                            println("Escolha um equipamento para equipar no monstro:")
                            // Exiba a lista de equipamentos disponíveis para escolha
                            equipamentos.forEachIndexed { index, equipamento ->
                                println("${index + 1}. ${equipamento.nome} - Ataque: ${equipamento.ataque}, Defesa: ${equipamento.defesa}")
                            }
                            print("Escolha um equipamento (1-${equipamentos.size}): ")
                            val escolhaEquipamento = readLine()

                            if (escolhaEquipamento?.matches("\\d+".toRegex()) == true) {
                                val indiceEquipamento = escolhaEquipamento.toInt() - 1

                                if (indiceEquipamento >= 0 && indiceEquipamento < equipamentos.size) {
                                    val equipamentoEscolhido = equipamentos[indiceEquipamento]
                                    // Aqui você deve implementar a lógica para equipar o monstro com o equipamento
                                    equiparMonstro(jogador, monstroAlvo, equipamentoEscolhido.nome)
                                } else {
                                    println("Escolha de equipamento inválida.")
                                }
                            } else {
                                println("Escolha de equipamento inválida.")
                            }
                        } else {
                            println("$monstroAlvo não encontrado no tabuleiro de $jogador.")
                        }
                    } else {
                        println("$jogador não possui um tabuleiro.")
                    }
                } else {
                    println("Nome de monstro inválido.")
                }
            }
            else -> {
                println("Ação inválida. Escolha 1 para atacar com um monstro ou 2 para tomar outra ação.")
            }
        }
    }


    fun realizarRodada() {
        rodada++
        println("Rodada $rodada")

        if (rodadaInicial) {
            // Realize ações específicas da primeira rodada aqui
            rodadaInicial = false // Após a primeira rodada, defina como false
        } else {
            for (jogador in jogadores) {
                if (pontuacao[jogador]!! <= 0) {
                    println("$jogador ficou sem pontos e perdeu o jogo!")
                    pontuacao.remove(jogador)
                }
            }
        }

        if (pontuacao.isEmpty()) {
            verificarVencedor()
            return
        }

        for (tabuleiro in tabuleiros.values) {
            tabuleiro?.resetarAtaques()
        }

        for (jogador in jogadores) {
            val novaCarta = leitorCartas.pegarCartasAleatorias(1)[0]
            maos[jogador]?.add(novaCarta)
        }

        for (jogador in jogadores) {
            while (maos[jogador]?.size ?: 0 > limiteCartasMao) {
                println("$jogador possui mais de $limiteCartasMao cartas na mão.")
                println("Descartando uma carta aleatória.")
                val cartaDescartada = maos[jogador]?.random()
                descartarCarta(jogador, cartaDescartada)
            }
        }

        for (jogador in jogadores) {
            println("Cartas de $jogador:")
            maos[jogador]?.forEachIndexed { index, carta ->
                val tipo = if (carta.tipo.equals("monstro", true)) "Monstro" else "Equipamento"
                println(
                    "${index + 1}. Nome: ${carta.nome}, " +
                            "Ataque: ${carta.ataque}, " +
                            "Defesa: ${carta.defesa}, " +
                            "Tipo: $tipo"
                )
            }

            val escolhaCarta = readLine()
            if (escolhaCarta?.matches("\\d+".toRegex()) == true) {
                val escolha = escolhaCarta.toInt()
                if (escolha in 1..(maos[jogador]?.size ?: 0)) {
                    val cartaEscolhida = maos[jogador]?.get(escolha - 1)
                    println("Ação para ${cartaEscolhida?.nome} (Ataque: ${cartaEscolhida?.ataque}, Defesa: ${cartaEscolhida?.defesa}):")
                    println("1. Posicionar no tabuleiro")
                    println("2. Descartar a carta")
                    print("Escolha uma ação (1/2): ")
                    val acao = readLine()
                    realizarAcao(jogador, acao ?: "")
                } else {
                    println("Número de carta inválido.")
                }
            } else if (escolhaCarta?.equals("D", ignoreCase = true) == true) {
                val cartaDescartada = maos[jogador]?.random()
                maos[jogador]?.remove(cartaDescartada)
                println("$jogador descartou ${cartaDescartada?.nome}.")
            } else {
                println("Escolha inválida. Digite o número da carta que deseja jogar ou 'D' para descartar.")
            }
        }

        if (leitorCartas.cartasEstaoVazias()) {
            verificarVencedor()
        }
    }



    fun realizarAtaque(jogadorAtacante: String, jogadorAlvo: String, monstroAtacante: String): Int {
        val tabuleiroAtacante = tabuleiros[jogadorAtacante]
        val tabuleiroAlvo = tabuleiros[jogadorAlvo]

        if (tabuleiroAtacante != null && tabuleiroAlvo != null) {
            val monstroAtacanteInfo = tabuleiroAtacante.monstros[monstroAtacante]
            val monstroAlvoInfo = tabuleiroAlvo.monstros[monstroAtacante]

            if (monstroAtacanteInfo != null && monstroAlvoInfo != null) {
                if (tabuleiroAtacante.estadoAtaque[monstroAtacante] == "true") {
                    val diferencaAtaque = monstroAtacanteInfo.ataque - monstroAlvoInfo.ataque

                    if (diferencaAtaque > 0) {
                        println("$jogadorAtacante atacou $jogadorAlvo com ${monstroAtacanteInfo.nome} e causou $diferencaAtaque de dano.")
                        // Reduza os pontos do jogador alvo com base no dano causado
                        pontuacao[jogadorAlvo] = maxOf(0, pontuacao[jogadorAlvo]!! - diferencaAtaque)

                        if (pontuacao[jogadorAlvo]!! <= 0) {
                            println("$jogadorAlvo ficou sem pontos de vida.")
                        }
                        verificarFimDoJogo() // Verificar o fim do jogo após cada ataque
                        return diferencaAtaque
                    } else {
                        println("$jogadorAtacante atacou $jogadorAlvo com ${monstroAtacanteInfo.nome} mas não causou dano.")
                    }
                } else {
                    println("${monstroAtacanteInfo.nome} está em estado de defesa e não pode atacar nesta rodada.")
                }
            } else {
                println("$jogadorAtacante ou $jogadorAlvo não encontrado no tabuleiro.")
            }
        } else {
            println("$jogadorAtacante ou $jogadorAlvo não encontrado nos tabuleiros do jogo.")
        }

        return 0
    }

    fun verificarFimDoJogo() {
        val jogadoresEliminados = pontuacao.filterValues { it <= 0 }.keys.toList()
        for (jogadorEliminado in jogadoresEliminados) {
            println("$jogadorEliminado ficou sem pontos e foi eliminado do jogo.")
            // Remova o jogador eliminado do mapa de pontuações
            pontuacao.remove(jogadorEliminado)
            // Verifique se o jogo terminou
            if (pontuacao.isEmpty()) {
                println("O jogo terminou.")
                verificarVencedor()
                return
            }
        }
    }

    fun posicionarMonstroNoTabuleiro(jogador: String, monstroNome: String) {
        // Obtém a mão do jogador
        val maoJogador = maos[jogador]

        // Verifica se a mão do jogador não é nula e contém o monstro com o nome especificado
        if (maoJogador != null && maoJogador.any { it.nome == monstroNome }) {
            // Obtém o tabuleiro do jogador
            val tabuleiroJogador = tabuleiros[jogador]

            // Verifica se o jogador ainda pode posicionar monstros no tabuleiro
            if ((tabuleiroJogador?.monstros?.size ?: 0) < 5) {
                // Remove o monstro da mão do jogador
                maoJogador.removeAll { it.nome == monstroNome }

                // Posiciona o monstro no tabuleiro com um estado inicial
                tabuleiroJogador?.posicionarMonstro(monstroNome, "estado_do_monstro")

                // Informa que o jogador posicionou o monstro no tabuleiro
                println("$jogador posicionou o monstro $monstroNome no tabuleiro.")
            } else {
                // Informa que o jogador já possui 5 monstros no tabuleiro
                println("$jogador, você já possui 5 monstros no tabuleiro. Remova um para posicionar outro.")
            }
        } else {
            // Informa que o jogador ou o monstro especificado não foram encontrados
            println("$jogador ou $monstroNome não encontrado no jogo.")
        }
    }


    fun descartarCarta(jogador: String, cartaDescartada: Carta?) {
        // Verifica se o jogador está na lista de jogadores e se a carta a ser descartada está na mão do jogador
        if ((jogador in maos) && (cartaDescartada in (maos[jogador] ?: emptyList()))) {
            // Remove a carta da mão do jogador
            maos[jogador]?.remove(cartaDescartada)

            // Informa que o jogador descartou a carta com o nome correspondente
            println("$jogador descartou a carta ${cartaDescartada?.nome}.")
        }
    }


    fun verificarLimiteCartasMao() {
        // Itera sobre a lista de jogadores
        for (jogador in jogadores) {
            // Enquanto o número de cartas na mão do jogador for maior que o limite permitido
            while ((maos[jogador]?.size ?: 0) > limiteCartasMao) {
                // Informa que o jogador possui mais cartas do que o limite permitido
                println("$jogador possui mais de $limiteCartasMao cartas na mão.")

                // Informa que uma carta aleatória está sendo descartada
                println("Descartando uma carta aleatória.")

                // Seleciona uma carta aleatória da mão do jogador
                val cartaDescartada = maos[jogador]?.random()

                // Chama a função para descartar a carta
                descartarCarta(jogador, cartaDescartada)
            }
        }
    }


    fun verificarVencedor() {
        // Encontra o maior número de pontos entre os jogadores
        val maxPontos = pontuacao.values.maxOrNull()

        // Filtra os jogadores que têm a pontuação máxima
        val vencedores = pontuacao.filterValues { it == maxPontos }.keys.toList()

        // Verifica se há apenas um vencedor
        if (vencedores.size == 1) {
            // Exibe o jogador vencedor e sua pontuação
            println("O vencedor é ${vencedores[0]} com $maxPontos pontos!")
        } else {
            // Se houver empate, informa que o jogo terminou em empate
            println("O jogo terminou em empate.")
        }
    }

    fun comprarCarta(jogador: String) {
        val maoJogador = maos[jogador]
        if (maoJogador != null) {
            if (maoJogador.size < limiteCartasMao) {
                // Adquire uma nova carta aleatória
                val novaCarta = leitorCartas.pegarCartasAleatorias(1)[0]

                // Adiciona a nova carta à mão do jogador
                maoJogador.add(novaCarta)

                // Exibe a mensagem de que o jogador comprou uma carta
                println("$jogador comprou a carta ${novaCarta.nome}.")
            } else {
                // Informa que o jogador possui 10 cartas na mão e precisa descartar uma
                println("$jogador possui 10 cartas na mão. Descartando uma carta aleatória.")

                // Seleciona uma carta aleatória da mão do jogador para descartar
                val cartaDescartada = maoJogador.random()

                // Remove a carta descartada da mão do jogador
                maoJogador.remove(cartaDescartada)

                // Exibe a mensagem de que o jogador descartou uma carta
                println("$jogador descartou a carta ${cartaDescartada.nome}.")
            }

            // Verifica se o jogador ultrapassou o limite de cartas na mão após comprar
            verificarLimiteCartasMao()
        }
    }

    fun verificarJogoTerminou(): Boolean {
        // Verifica se o jogo terminou, ou seja, se não há mais jogadores com pontuação
        return pontuacao.isEmpty()
    }

    fun obterTabuleiroDoJogador(jogador: String): Tabuleiro? {
        // Retorna o tabuleiro associado ao jogador (ou nulo se não existir)
        return tabuleiros[jogador]
    }
}
