# Checkpoint 5 — Bug Hunt PetFiap

> Copie este arquivo para a raiz do seu repositório com o nome **README.md**
> e preencha todas as seções.

## Identificação

**Grupo:** 404Squad

| Integrante | RM | Turma |
|---|---|---|
| Claus Moreira | 565503 | 2CCPX |
| Julia Lopes | 566557 | 2CCPX |
| Guilherme Martins | 566570 | 2CCPX |
| Gabriel Rodrigues | 566475 | 2CCPX |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 11 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas |

---

## Parte 1 — Bugs encontrados

> Uma linha por bug, na ordem em que você os encontrou. Use a numeração dos seus
> commits (`fix: bug01 ...`). Preencha TODAS as colunas — metade da nota está aqui.

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | `GeradorProtocoloTest.deveManterUmaUnicaInstancia` e `deveGerarProtocolosSequenciais` falhavam. `assertSame` acusava objetos distintos e os protocolos retornavam sempre o valor 1. | `GeradorProtocolo.java` (linha ~18): o método `getInstancia()` criava `new GeradorProtocolo()` direto no `return`, sem atribuir à variável estática `instancia`. | Atribuição da nova instância ao campo estático `instancia = new GeradorProtocolo();` antes de retorná-la no método `getInstancia()`. | Padrão de Criação Singleton, ciclo de vida de objetos e atributos estáticos (`static`) (Aula 14). |
| bug02 | `AtendimentoBuilderTest.deveMontarAtendimentoCompleto` falhava com `AssertionFailedError: expected: <Rex> but was: <null>`. | `AtendimentoBuilder.java` (linha ~24): no método `comPet`, a instrução `petNome = petNome;` causava sombreamento (shadowing) do parâmetro sobre o atributo da classe. | Utilização explícita da referência `this`: `this.petNome = petNome;`, atribuindo o parâmetro recebido ao atributo de instância correspondente. | Escopo de identificadores, sombreamento de atributos (Shadowing) e palavra-chave `this` (Aulas 4 e 7). |
| bug03 | `AtendimentoFactoryTest.devePreencherOsDadosDoPetNaConsulta` falhava com `expected: <Mimi> but was: <null>`. Os atributos do pet não eram gravados na consulta. | `ConsultaVeterinaria.java` (linha ~17): o construtor sobrecarregado invocava `super();` sem passar os argumentos para a classe base `Atendimento`. | Chamada ao construtor parametrizado da superclasse: `super(protocolo, petNome, petPorte, tutorNome, dataHora);`. | Herança, Construtores em subclasses e uso de `super(...)` (Aulas 5 e 6). |
| bug04 | `AtendimentoFactoryTest.deveCriarTosaQuandoTipoForTosa` falhava no `assertInstanceOf(Tosa.class, atendimento)` pois a factory devolvia um objeto da classe `Banho`. | `AtendimentoFactory.java` (linha ~17): no switch de tipos de atendimento, o case `"TOSA"` instanciava erroneamente `new Banho(...)`. | Correção do switch-case para instanciar a subclasse correta: `case "TOSA" -> new Tosa(p, n, po, tu, d);`. | Padrão de Criação Factory Method / Simple Factory e Polimorfismo (Aulas 6 e 14). |
| bug05 | `AgendaServiceTest.deveLancarExcecaoQuandoAtendimentoNaoExiste` falhava porque nenhuma exceção era lançada; o método retornava `null`. | `AgendaService.java` (linha ~37): `buscarPorId()` continha um bloco `try/catch (Exception e)` que capturava a exceção de negócio do `orElseThrow()` e retornava `null`. | Remoção do bloco `try/catch` desnecessário, permitindo que a `AtendimentoNaoEncontradoException` seja propagada para o chamador. | Tratamento de Exceções, antipadrão "Exception Swallowing" e uso de `Optional` (Aula 8). |
| bug06 | `AgendaServiceTest.deveRecusarAgendamentoComHorarioJaOcupado` falhava, permitindo agendamento duplicado no mesmo horário sem lançar `HorarioOcupadoException`. | `AgendaService.java` (linha ~26): validação comparava `a.getPetNome() == novo.getPetNome()` e `a.getDataHora() == novo.getDataHora()`, comparando referências de memória. | Substituição do operador `==` pelo método `.equals()` para comparar o conteúdo dos objetos: `equals(novo.getPetNome())` e `equals(novo.getDataHora())`. | Comparação de objetos em Java: Identidade de Referência (`==`) vs Igualdade de Conteúdo (`.equals()`) (Aula 7). |
| bug07 | `AtendimentoBuilderTest.deveRecusarMontagemSemNomeDoPet` e `deveRecusarMontagemSemPorte` falhavam pois objetos inválidos eram gerados sem lançar `IllegalArgumentException`. | `AtendimentoBuilder.java` (linha ~40): método `construir()` não validava os dados obrigatórios, assumindo incorretamente que o controller faria a validação. | Implementação de validação defensiva em `construir()` verificando `petNome` e `petPorte` com `isBlank()` e lançando `IllegalArgumentException` se inválidos. | Padrão Builder, Validação de Invariantes e princípio de "o objeto só nasce válido" (Aula 14). |
| bug08 | Teste novo `BanhoTest.deveCobrarPrecoPorPorteQuandoForBanho` falhava: porte PEQUENO cobrava R$ 100 e GRANDE cobrava R$ 60 (valores invertidos). | `Banho.java` (linha ~28): lógica de retorno em `calcularPreco()` estava invertida em relação à tabela de preços definida no contrato. | Ajuste da condicional para retornar `60.0` para porte `PEQUENO`, `80.0` para `MEDIO` e `100.0` para `GRANDE`. | Polimorfismo, Sobrescrita de métodos e implementação de Regras de Negócio/Contrato (Aulas 6 e 15). |
| bug09 | Teste novo `TosaTest.deveDurar60Minutos` falhava com `expected: <60> but was: <30>` ao chamar `getDuracaoMinutos()`. | `Tosa.java` (linha ~40): o método foi declarado como `getDuracaoMinutos(String porte)` com parâmetro extra, gerando sobrecarga (overload) em vez de sobrescrita (override). | Remoção do parâmetro extra e adição da anotação `@Override public int getDuracaoMinutos() { return 60; }`. | Sobrescrita (Override) vs Sobrecarga (Overload) e uso da anotação `@Override` (Aula 7). |
| bug10 | Teste novo `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoJaConcluido` falhava, permitindo cancelar atendimento com status `"CONCLUIDO"` ou `"CANCELADO"`. | `Atendimento.java` (linha ~63): o método `cancelar()` atribuía `"CANCELADO"` diretamente sem checar se o atendimento estava no estado `"AGENDADO"`. | Inclusão de verificação de máquina de estados em `cancelar()`: se `!"AGENDADO".equals(status)`, lança `StatusInvalidoException`. | Encapsulamento, Máquina de Estados e Integridade de Domínio (Aulas 4 e 8). |
| bug11 | Teste novo `AgendaServiceTest.deveRecusarAgendamentoQuandoDataHoraEstaNoPassado` falhava, permitindo agendamentos em datas retroativas e consultando o banco. | `AgendaService.java` (linha ~22): método `agendar()` não validava se a data/hora solicitada já havia passado antes de processar o agendamento. | Adição de validação fail-fast no início de `agendar()`: se `novo.getDataHora().isBefore(LocalDateTime.now())`, lança `IllegalArgumentException`. | Validação de Pré-condições / Fail-Fast e manipulação da API `java.time.LocalDateTime` (Aulas 7 e 15). |
| bug12 | Ausência de thread-safety no `GeradorProtocolo` (risco de condição de corrida gerando protocolos repetidos em concorrência real). | `GeradorProtocolo.java` (linhas ~17 e ~24): método `getInstancia()` e operação `contador++` não eram atômicos nem sincronizados, apesar do comentário alegar thread-safety. | Diagnóstico documentado: necessidade de sincronização (`synchronized`) ou uso de `AtomicInteger` para garantir segurança concorrente entre múltiplas requisições. | Concorrência, Thread-Safety e Atomicidade em padrões de projeto (Aula 14). |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.java` (método `criar`) | Nomes Significativos e Reveladores de Intenção (Clean Code Cap. 2). Uso de parâmetros de apenas uma letra (`p`, `t`, `n`, `po`, `tu`, `d`) que dificultavam a leitura e entendimento do código. | Identificado para padronização com nomes descritivos e autoexplicativos: `protocolo`, `tipo`, `petNome`, `petPorte`, `tutorNome` e `dataHora`. |
| clean02 | `AtendimentoController.java` (linhas ~106 a ~113) | Dead Code (Código Morto) e Princípio YAGNI ("You Aren't Gonna Need It"). Método privado `calcularDescontoFidelidade` nunca utilizado e comentários de regras futuras especulativas. | Remoção de código morto e comentários sobre funcionalidades não aprovadas, mantendo o controller conciso e aderente ao escopo atual. |
| clean03 | `AgendaService.java` (linha ~34) e `GeradorProtocolo.java` (linha ~14) | Separação de Responsabilidades e Boas Práticas de Logging. Presença de `System.out.println` no meio da lógica de negócio e do singleton para emissão de recibos/logs no console. | Substituição de impressões no console por logging estruturado via Logger/SLF4J, evitando poluição da saída padrão em ambiente produtivo. |
| clean04 | `Banho.java`, `Tosa.java` e `ConsultaVeterinaria.java` | Números Mágicos (Magic Numbers) espalhados no código. Valores fixos de preços (`60.0`, `80.0`, `100.0`, `150.0`), durações (`45`, `60`, `30`) e pontos de fidelidade soltos nos métodos. | Extração dos literais mágicos para constantes estáticas imutáveis (`public static final`), facilitando a manutenção e alteração de tabelas de preços. |
| clean05 | `Tosa.java` (método `getDuracaoMinutos`) | Utilização da anotação `@Override` para integridade de polimorfismo. A ausência da anotação escondeu um erro semântico grave de sobrecarga não intencional. | Aplicação explícita e obrigatória de `@Override` em todos os métodos que redefinem comportamento da superclasse, ativando a checagem estática do compilador. |
| clean06 | `AtendimentoBuilder.java` (comentário anterior a `construir`) | Comentários Desonestos / Compensatórios (Clean Code Cap. 4). Comentário afirmava que a validação era papel do controller, tentando justificar a ausência de integridade no builder. | Substituição do comentário enganoso pelo comentário verdadeiro: `// Valida os campos obrigatorios: o atendimento so nasce valido.`, alinhando código e documentação. |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

> Uma linha por teste novo (`test: ...`). "Regra coberta" é o comportamento do
> contrato (seção 3 do enunciado) que o teste protege. Em "Resultado", diga se o
> teste ficou vermelho ao ser escrito (revelou bug — qual?) ou verde de cara
> (regra já estava correta).

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `BanhoTest.deveCobrarPrecoPorPorteQuandoForBanho` | Tabela de preços do banho conforme o porte do pet: PEQUENO (R$ 60,00), MEDIO (R$ 80,00) e GRANDE (R$ 100,00). | Vermelho (revelou o `bug08`, pois os valores para pequeno e grande estavam invertidos na classe `Banho`). |
| teste02 | `TosaTest.deveDurar60Minutos` | Duração da tosa fixada em 60 minutos, sobrescrevendo a duração padrão de 30 minutos definida na superclasse `Atendimento`. | Vermelho (revelou o `bug09`, pois `Tosa` possuía overload com parâmetro em vez de sobrescrever `getDuracaoMinutos()`). |
| teste03 | `ConsultaVeterinariaTest.deveCobrarPrecoFixoQuandoForConsultaIndependenteDoPorte` | Preço fixo de R$ 150,00 para consulta veterinária, independentemente de o animal ser de porte PEQUENO, MEDIO ou GRANDE. | Verde de cara (regra já estava implementada corretamente no método `calcularPreco()` da classe `ConsultaVeterinaria`). |
| teste04 | `AgendaServiceTest.deveRecusarAgendamentoQuandoDataHoraEstaNoPassado` | Bloqueio de agendamento retroativo (no passado) lançando `IllegalArgumentException`, sem realizar consultas ou persistência no repositório. | Vermelho (revelou o `bug11`, pois `AgendaService.agendar()` não validava se a data/hora já havia passado). |
| teste05 | `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoJaConcluido` | Impossibilidade de cancelar atendimento com status `"CONCLUIDO"`, lançando `StatusInvalidoException` e sem persistir alteração no banco. | Vermelho (revelou o `bug10`, pois o método `cancelar()` alterava o status incondicionalmente sem checar se estava `"AGENDADO"`). |
| teste06 | `AgendaServiceTest.deveRecusarConclusaoDeAtendimentoCancelado` | Impossibilidade de concluir atendimento previamente cancelado, lançando `StatusInvalidoException` e sem persistir alteração no banco. | Verde de cara (a regra já estava protegida em `Atendimento.concluir()` através da checagem `if (!"AGENDADO".equals(status))`). |

---

## Parte 4 — Perguntas de reflexão

> Responda com suas palavras, 5 a 10 linhas cada, **usando o código real do
> projeto como exemplo**. Respostas genéricas de tutorial não pontuam.

### 1. A suíte como contrato (Aula 15)
A suíte de testes unitários funcionou como uma especificação executável rigorosa do sistema. Quando o projeto chegou com 9 testes quebrando, as mensagens de erro nos guiaram cirurgicamente até as causas raízes. Por exemplo, a falha `expected: <Rex> but was: <null>` em `AtendimentoBuilderTest.deveMontarAtendimentoCompleto` apontou com precisão cirúrgica que o atributo `petNome` não estava sendo gravado, revelando o sombreamento `petNome = petNome` dentro de `comPet()`. Da mesma forma, o `assertSame` falhando em `GeradorProtocoloTest` comprovou que o Singleton gerava novas instâncias a cada chamada. Testar isso manualmente via `curl` seria imensamente mais lento e impreciso: exigiria subir o servidor web, configurar o banco de dados Oracle, efetuar requisições HTTP manuais e decodificar respostas JSON que muitas vezes mascarariam o erro com códigos genéricos 400 ou 500 sem apontar a linha do código defeituoso. A suíte unitária roda em frações de segundo, valida as regras em isolamento e oferece feedback determinístico imediato.

### 2. Mock e injeção de dependência (Aulas 13 a 15)
Em ambiente de produção, o contêiner de Inversão de Controle (IoC) do Spring Framework gerencia o ciclo de vida dos componentes. Ao inicializar o `AgendaService` (marcado com `@Service`), o Spring detecta a anotação `@Autowired private AtendimentoRepository repository;` e injeta automaticamente uma implementação dinâmica gerada pelo Spring Data JPA, a qual se conecta fisicamente ao banco de dados Oracle através de pools de conexão e drivers JDBC. Já no teste `AgendaServiceTest`, quem comanda o processo não é o Spring, mas sim a extensão do Mockito (`@ExtendWith(MockitoExtension.class)`). O `@Mock` cria em tempo de execução um objeto simulado (dublê) de `AtendimentoRepository` em memória, enquanto o `@InjectMocks` instancia o `AgendaService` e injeta esse mock em seu atributo privado por reflexão. Graças a isso, o teste roda de forma puramente unitária: não sobe o contexto pesado do Spring Boot nem depende de rede ou credenciais de banco, executando cenários simulados via `when(...).thenReturn(...)` em poucos milissegundos.

### 3. `==` vs `.equals()` (Aula 7)
No método `agendar()` de `AgendaService`, o código original verificava duplicidade com `a.getPetNome() == novo.getPetNome() && a.getDataHora() == novo.getDataHora()`. O operador `==` compara a identidade de referência, isto é, se duas variáveis apontam exatamente para o mesmo endereço de memória na Heap. No teste `deveRecusarAgendamentoComHorarioJaOcupado`, o objeto `novo` foi instanciado separadamente a partir de um parse (`LocalDateTime.parse(...)`), ocupando um endereço de memória diferente daquele do atendimento já agendado. Por isso, a comparação com `==` resultou em `false` mesmo com horários idênticos. O operador `==` só parece "funcionar por sorte" com Strings literais graças ao String Constant Pool da JVM, que reaproveita a mesma referência para textos idênticos criados diretamente no código-fonte (`"Rex"`). Contudo, em cenários reais com dados vindos de requisições HTTP ou parsing dinâmico, novas instâncias são alocadas na Heap. A correção utilizando `.equals()` compara o estado lógico e o valor dos objetos, garantindo a detecção correta de conflitos.

### 4. Sobrescrita vs sobrecarga (Aula 7)
A sobrescrita (override) ocorre quando uma subclasse redefine um método herdado mantendo exatamente a mesma assinatura (nome, quantidade e tipos de parâmetros) e retorno compatível, viabilizando o polimorfismo em tempo de execução. Já a sobrecarga (overload) acontece quando métodos com o mesmo nome possuem parâmetros distintos, constituindo métodos completamente diferentes resolvidos estaticamente pelo compilador. No bug da classe `Tosa`, o desenvolvedor escreveu `public int getDuracaoMinutos(String porte)`. Como a classe base `Atendimento` declarava `public int getDuracaoMinutos()` (sem parâmetros), o compilador interpretou a versão com parâmetro como uma sobrecarga legítima, permitindo que a classe compilasse perfeitamente. No entanto, ao invocar `atendimento.getDuracaoMinutos()` polimorficamente através de uma referência da superclasse, a JVM executava a implementação padrão de `Atendimento`, retornando 30 minutos em vez de 60. A presença da anotação `@Override` teria impedido o bug em tempo de compilação, pois forçaria o compilador a acusar erro caso não existisse um método idêntico na superclasse.

### 5. Singleton manual vs bean do Spring (Aula 14)
O padrão Singleton garante a existência de uma única instância de uma classe durante toda a execução da aplicação e provê um ponto de acesso global a ela, preservando estados compartilhados como o contador sequencial do `GeradorProtocolo`. O bug no Singleton manual ocorreu porque o método estático `getInstancia()` verificava `if (instancia == null)` e fazia `return new GeradorProtocolo();` diretamente, esquecendo-se de salvar a referência no atributo estático `instancia`. Com isso, cada chamada recriava o objeto e reiniciava o contador em zero, gerando sempre o protocolo número 1. Já o `AgendaService`, anotado com `@Service`, não corre esse risco porque seu ciclo de vida é governado pelo contêiner IoC do Spring. No Spring, o escopo padrão de qualquer bean gerenciado é justamente o Singleton do contêiner: o framework cria a instância uma única vez na inicialização, gerencia suas dependências e compartilha essa mesma instância segura entre todos os pontos de injeção, sem a necessidade de lógicas manuais propensas a falhas de instanciação ou verificação de nulos.

### 6. Cobertura de testes: onde parar? (Aula 15)
Manter os testes que nasceram verdes (como `ConsultaVeterinariaTest.deveCobrarPrecoFixoQuandoForConsultaIndependenteDoPorte` e `AgendaServiceTest.deveRecusarConclusaoDeAtendimentoCancelado`) é essencial, pois eles atuam como testes de regressão indispensáveis. Embora não tenham revelado bugs imediatos, esses testes blindam o código contra alterações e refatorações futuras que possam violar regras contratuais que hoje estão corretas. Em um projeto real sujeito a prazos restritos, perseguir obsessivamente 100% de cobertura de código é contraproducente, pois frequentemente leva a testes redundantes de getters e setters que consomem tempo e não agregam valor. A prioridade técnica deve ser cobrir o caminho feliz das regras centrais do domínio e, com igual ou maior ênfase, os caminhos de exceção e condições de borda (como transições de status inválidas, validação de entradas nulas e tentativas de agendamento no passado ou com horário em conflito). É a proteção dos cenários de falha que evita corrupção de dados e prejuízos operacionais em produção.

---

## Parte 5 — Espaço livre (opcional)


