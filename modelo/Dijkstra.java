/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 31/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Dijkstra
* Funcao...........: Classe que implementa o algoritmo de dijkstra
                     para encontrar o caminho minimo em um grafo
                     com arestas nao negativas.
*************************************************************** */
package modelo;

import util.Animador;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class Dijkstra{
  private Grafo subrede;
  private SimulacaoContexto ctx = new SimulacaoContexto();
  private Animador animador = new Animador(ctx.getIdSimulacao());
  private Label textoCaminhoMinimo;
  private ArrayList<Vertice> vertices;
  private Stack<Roteador> pilhaDeRoteadores;
  private final int INFINITO = 2147483647;
  private final int PROVISORIO = 0;
  private final int PERMANENTE = 1;

  private int idSimulacao;

  /* ***************************************************************
	* Metodo: Dijkstra
	* Funcao: Construtor da classe
	* Parametros: subrede = grafo que representa a subrede gerada na tela,
	              textoCaminhoMinimo = rotulo onde o caminho minimo sera
	              exposto na tela, idSimulacao = id da simulacao atual
	* Retorno: Objeto da classe Dijkstra
	*************************************************************** */
  public Dijkstra(Grafo subrede, Label textoCaminhoMinimo, int idSimulacao){
    this.subrede = subrede;
    this.textoCaminhoMinimo = textoCaminhoMinimo;
    this.idSimulacao = idSimulacao;
  }//Fim do metodo Dijkstra

 /* ***************************************************************
	* Metodo: caminhoMinimo
	* Funcao: Encontrar o menor caminho entre dois vertices (no caso, roteadores)
	          em um grafo (no caso, subrede), desde que nao existam arestas negativas
	* Parametros: pacote = pacote a ser enviado, contendo a origem, o destino e o caminho
	              minimo
	* Retorno: boolean, true caso o caminho seja encontrado, falso caso o contrario
	*************************************************************** */
  public boolean caminhoMinimo(Pacote pacote){
    int origem = pacote.getOrigem();
    int destino = pacote.getDestino();

    //Inicia a lista e os vertices que serao usados para a execucao do algoritmo
    iniciarVertices();

    //Capta os roteadores de origem e destino para identifica-los na interface apos a conclusao do algoritmo
    Roteador rOrigem = subrede.getVertice(pacote.getOrigem());
    Roteador rDestino = subrede.getVertice(pacote.getDestino());

    //Inicializa o vertice ativo
    Vertice verticeOrigem = vertices.get(origem);
    //Vertice final
    Vertice verticeDestino = null;

    Vertice verticeAtivo = verticeOrigem;
    verticeAtivo.setPredecessor("-1");
    verticeAtivo.setDistancia(0);
    verticeAtivo.setEstado(PERMANENTE);


    //Anima o primeiro vertice como permanente
    animador.verticePermanente(verticeAtivo.getRoteador().getImagem());
    animador.alterarEstado(rOrigem.getRotuloEstado(), String.valueOf(0), "nulo");
    animador.destaqueTexto(rOrigem.getRotuloEstado());
    while(true){
      int indiceVertice = verticeAtivo.getRoteador().getIdentificador();
      LinkedList<Aresta<Roteador>> listaAdjacencias = subrede.getLista(indiceVertice);

      //Para cada aresta na lista de ajacencias do vertice ativo
      for (Aresta<Roteador> aresta : listaAdjacencias){
        //Caso o id mude, a simulacao deve ser reiniciada
        if (this.idSimulacao != ctx.getIdSimulacao()){
          return false;
        }

        //Cada vertice analisado
        Vertice verticeAnalisado = getVerticePorId(aresta.getDestino().toString());

        //Se o vertice analisado for um dos provisorios, entao ele deve ser animado
        if (verticeAnalisado.getEstado() == PROVISORIO){
          animador.arestaProvisoria(aresta.getLinha());
          animador.destaqueTexto(aresta.getDestino().getRotuloEstado());
        }

        int distanciaTotal = (aresta.getPeso() + verticeAtivo.getDistancia());
        //Se a distancia o verrice atual + o peso da aresta for menor que a distancia
        //do vertice analisado, entao atualizar a distancia do vertice analisado e seu
        //predecessor
        if (distanciaTotal < verticeAnalisado.getDistancia()){
          verticeAnalisado.setDistancia(distanciaTotal);
          verticeAnalisado.setPredecessor(verticeAtivo.getIdentificador());

          Roteador roteadorAnalisado = verticeAnalisado.getRoteador();
          animador.alterarEstado(roteadorAnalisado.getRotuloEstado(), String.valueOf(distanciaTotal), verticeAtivo.getIdentificador());
        }
      }

      int distancia = INFINITO;
      boolean novoVerticeEncontrado = false;
      //Percorre os vertices e encontra aquele com a menor distancia para ser analisado
      for (Vertice v : vertices){
        animador.verticeProvisorio(v.getRoteador().getImagem());
        if (v.getEstado() == PROVISORIO && (v.getDistancia() < distancia)){
          verticeAtivo = v;
          distancia = v.getDistancia();
          novoVerticeEncontrado = true;
        }
      }

      //Nenhum novo vertice foi encontrado para ser o proximo ativo, entao o algoritmo chegou ao fim
      if(!novoVerticeEncontrado){
        break;
      }

      if (verticeAtivo.getIdentificador().equals(rDestino.toString())){
        verticeDestino = verticeAtivo;
      }

      //Anima o novo vertice permanente
      animador.verticePermanente(verticeAtivo.getRoteador().getImagem());
      verticeAtivo.setEstado(PERMANENTE);
    }

    //Se o vertice de destino for nulo, entao o algoritmo nao encontrou o caminho minimo
    //Isso significa que algum erro ocorreu, ou que o caminho nao existe, entao o algoritmo eh finalizado
    if (verticeDestino == null){
      Platform.runLater(() -> {
        textoCaminhoMinimo.setText("Caminho nao encontrado.");
      });
      return false;
    }

    //Chama o metodo que determina o caminho minimo em formato de string apos o fim do algoritmo
    String caminhoMinimo = retornarCaminho(verticeDestino);

    //Inicia a pilha contendo os roteadores para onde o pacote deve ser enviado
    pilhaDeRoteadores = new Stack<>();
    //Chama o metodo que preenche a pilha
    criarPilhaDeRoteadores(verticeDestino);

    //Retira o primeiro roteador, pois esse eh o que esta executando o roteamento
    pilhaDeRoteadores.pop();
    pacote.setCaminho(pilhaDeRoteadores);

    //Desta as arestas que formam o caminho final
    finalizaInterface(verticeDestino);

    Platform.runLater(() -> {
      //Destaca os roteadores de origem e destino
      rOrigem.getImagem().setOpacity(1.0);
      rDestino.getImagem().setOpacity(1.0);

      //Define o texto na tela que representa o caminho minimo
      textoCaminhoMinimo.setText(caminhoMinimo);
    });

    //Caminho encontrado
    return true;
  }//Fim do metodo caminhoMinimo

  /* ***************************************************************
	* Metodo: criarPilhaDeRoteadores
	* Funcao: Preenche a pilha de roteadores de maneira recursiva
	* Parametros: vertice = ultimo vertice a ser analisado (vertice de destino)
	* Retorno: void
	*************************************************************** */
  public void criarPilhaDeRoteadores(Vertice vertice){
    pilhaDeRoteadores.push(vertice.getRoteador());
    //Caso base (chegou no primeiro vertice)
    if (vertice.getPredecessor().equals("-1")){
      return;
    }
    //Caso geral
    Vertice verticePredecessor = getVerticePorId(vertice.getPredecessor());
    criarPilhaDeRoteadores(verticePredecessor);
  }//Fim do metodo criarPilhaDeRoteadores

  /* ***************************************************************
	* Metodo: retornarCaminho
	* Funcao: Cria uma string com o caminho minimo de maneira recursiva
	* Parametros: vertice = ultimo vertice a ser analisado (vertice de destino)
	* Retorno: String
	*************************************************************** */
  public String retornarCaminho(Vertice vertice){
    //Caso base (chegou no primeiro vertice)
    if (vertice.getPredecessor().equals("-1")){
      return vertice.getIdentificador();
    }

    //Caso geral
    Vertice verticePredecessor = getVerticePorId(vertice.getPredecessor());
    return retornarCaminho(verticePredecessor) + " -> " + vertice.getIdentificador();
  }//Fim do metodo retornarCaminho

  /* ***************************************************************
	* Metodo: getVerticePorId
	* Funcao: Retorna um vertice de acordo com o id
	* Parametros: id = id do vertice
	* Retorno: Vertice
	*************************************************************** */
  public Vertice getVerticePorId(String id){
    for (Vertice v : vertices){
      if (v.getIdentificador().equals(id)){
        return v;
      }
    }
    return null;
  }//Fim do metodo getVerticePorId

   /* ***************************************************************
	* Metodo: iniciarVertices
	* Funcao: Inicia cada vertice do grafo com distancia = infinito, predecessor
            = nulo, estado = provisorio e adiciona seu identificador e o roteador
            que ele representa
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
  public void iniciarVertices(){
    vertices = new ArrayList<>();
    int qtdVertices = subrede.getVertices().size();

    for(int i = 0; i < qtdVertices; i++){
      Vertice v = new Vertice();
      v.setRoteador(subrede.getVertice(i));
      v.setIdentificador(subrede.getVertice(i).toString());
      v.setPredecessor(null);
      v.setDistancia(INFINITO);
      v.setEstado(PROVISORIO);

      vertices.add(v);
    }
  }//Fim do metodo iniciarVertices

   /* ***************************************************************
	* Metodo: finalizaInterface
	* Funcao: Destaca as conexoes que compoem o caminho minimo
	* Parametros: verticeAtual = um dos vertices do caminho minimo
	* Retorno: void
	*************************************************************** */
  public void finalizaInterface(Vertice verticeAtual){
    Vertice verticePredecessor = getVerticePorId(verticeAtual.getPredecessor());

    //Caso base (ultimo vertice encontrado)
    if (verticeAtual.getPredecessor().equals("-1")){
      return;
    }


    int indice = verticeAtual.getRoteador().getIdentificador();
    Roteador roteadorPredecessor = verticePredecessor.getRoteador();

    LinkedList<Aresta<Roteador>> adjacencias = subrede.getLista(indice);

    for (Aresta<Roteador> aresta : adjacencias){
      if (aresta.getDestino() == roteadorPredecessor){
        animador.arestaPermanente(aresta.getLinha());
        break;
      }
    }

    //Caso geral
    finalizaInterface(verticePredecessor);
  }//Fim do metodo finalizaInterface

}//Fim da classe Dijkstra
