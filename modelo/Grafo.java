/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 16/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Grafo
* Funcao...........: Classe que serve para representar um grafo atraves
                     de listas encadeadas em um ArrayList. Como a subrede
                     pode ser entendida como um grafo, ela pode ser
                     logicamente representada por essa lista de adjacencias.
*************************************************************** */

package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import javafx.scene.shape.Line;

public class Grafo{

  ArrayList<LinkedList<Aresta<Roteador>>> listaDeAdjacencias;
  ArrayList<Roteador> vertices;

  /* ***************************************************************
	* Metodo: Grafo
	* Funcao: Construtor da classe, inicia a lista de adjacencias.
	* Parametros: Nenhum
	* Retorno: Objeto da classe Grafo
	*************************************************************** */
  public Grafo(){
    listaDeAdjacencias = new ArrayList<>();
    vertices = new ArrayList<>();
  }//Fim do metodo Grafo

  /* ***************************************************************
	* Metodo: addVertice
	* Funcao: Adiciona um vertice a lista de adjacencias ao adicionar
            uma nova lista encadeada e tambem adiciona o vertice em
            uma lista propria.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
  public void addVertice(Roteador vertice){
    listaDeAdjacencias.add(new LinkedList<>());
    vertices.add(vertice);
  }//Fim do metodo addVertice

   /* ***************************************************************
	* Metodo: addAresta
	* Funcao: Adiciona uma adjacencia na lista entre dois vertices.
            Para isso, os indices dos vertices são usados para determinar
            a origem e o destino de cada conexao, assim adicionando
            os vertices na lista de adjacencia um do outro.
	* Parametros: origem, destino = índices dos vertices da lista de
                adjacencias
	* Retorno: void
	*************************************************************** */
  public void addAresta(int origem, int destino, int peso, Line linha){
    try{
      //Acessa a listas de ajacencia de um dos vertices, indicado pela origem,
      //acessa um dos vertices do grafo atraves do getVertice, indicado pelo
      //destino e adiciona o vertice de destino como uma aresta daquele vertice
      //de origem
      listaDeAdjacencias.get(origem).add(new Aresta<Roteador>(getVertice(destino), peso, linha));

      //Como o grafo que esta sendo representado não eh direcionado, eh importante
      //que o processo reverso seja feito, pois toda vez que um vertice A se conecta
      //com um vertice B, o vertice B tambem se conecta com A
      listaDeAdjacencias.get(destino).add(new Aresta<Roteador>(getVertice(origem), peso, linha));
    }
    catch(IndexOutOfBoundsException e){
      System.out.println("A sub-rede esta descrita incorretamente no arquivo \"backbone.txt\" ");
    }
  }//Fim do metodo addAresta

  /* ***************************************************************
	* Metodo: getVertice
	* Funcao: Retorna um vertice da lista.
	* Parametros: indice = indice do vertice desejado
	* Retorno: Roteador
	*************************************************************** */
  public Roteador getVertice(int indice){
    return vertices.get(indice);
  }//Fim do metodo getVertice

  /* ***************************************************************
	* Metodo: getLista
	* Funcao: Retorna a lista de adjacencias de um vertice em especifico
	* Parametros: indice = indice do vertice desejado
	* Retorno: LinkedList<Aresta<Roteador>> (lista encadeada de arestas)
	*************************************************************** */
  public LinkedList<Aresta<Roteador>> getLista(int indice){
    return listaDeAdjacencias.get(indice);
  }//Fim do metodo getLista

  /* ***************************************************************
	* Metodo: getVertice
	* Funcao: Retorna a lista de roteadores presentes na subrede
	* Parametros: Nenhum
	* Retorno: ArrayList<Roteador> (lista roteadores)
	*************************************************************** */
  public ArrayList<Roteador> getVertices(){
    return this.vertices;
  }//Fim do metodo getVertices

  /* ***************************************************************
	* Metodo: apagarAresta
	* Funcao: Remove as arestas logicas associadas a conexao removida na
	          tela
	* Parametros: linha = linha que representa a aresta na tela
	* Retorno: void
	*************************************************************** */
  public void apagarAresta(Line linha){
    //Para referenciar as arestas a serem removidas
    Aresta<Roteador> ida = null;
    Aresta<Roteador> volta = null;

    //Para referenciar as listas que contem essas arestas
    LinkedList<Aresta<Roteador>> listaIda = null;
    LinkedList<Aresta<Roteador>> listaVolta = null;

    //Flag para indicar se a aresta de ida ja foi
    boolean idaJafoi = false;

    //Itera pelas listas de arestas e pelas arestas em si e encontra as arestas a serem removidas
    for (LinkedList<Aresta<Roteador>> lista : listaDeAdjacencias){
      for (Aresta<Roteador> aresta : lista){
        if ((aresta.getLinha() == linha) && !(idaJafoi)){
          listaIda = lista;
          ida = aresta;
          idaJafoi = true;
        }else if (aresta.getLinha() == linha){
          listaVolta = lista;
          volta = aresta;
        }
      }
    }

    //Remove as arestas em definitivo
    try{
      listaIda.remove(ida);
      listaVolta.remove(volta);
    }catch(NullPointerException e){
      System.out.println("Erro na remocao de aresta");
    }
  }//Fim do metodo apagarAresta

  /* ***************************************************************
	* Metodo: print
	* Funcao: Imprime a lista de adjacencias no terminal. Util para debug.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
  public void print(){
    for (LinkedList<Aresta<Roteador>> lista : listaDeAdjacencias){
      for (Aresta<Roteador> aresta : lista){
        System.out.print(aresta.toString() + " -> ");
      }
      System.out.println();
    }
  }//Fim do metodo print
}//Fim da classe Grafo
